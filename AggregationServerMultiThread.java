import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AggregationServerMultiThread extends Thread {
    private final Socket socket;
    private final DataManipulator dataManipulator;
    private static final int TTL_IN_MILLISECOND = 30000;

    private static final Map<Socket, String> serverIds = new ConcurrentHashMap<>();

    private static final Map<Socket, Long> serverLastActiveTime = new ConcurrentHashMap<>();
    private static String recentWeatherData = null;
    private static final LamportClock serverLamportClock = new LamportClock();


    public AggregationServerMultiThread(Socket socket, String uuid) throws SocketException {
        super("AggregationServerMultiThread");
        System.out.println("A client started");
        this.socket = socket;
        this.socket.setKeepAlive(true);

        System.out.println(socket.getLocalAddress());
        System.out.println(socket.getLocalPort());
        this.dataManipulator = new DataManipulator();
        serverIds.put(socket, uuid);
    }

    /**
     * A thread main run() function that comprises forking 3 new other threads
     */
    @Override
    public void run() {

        try (
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
        ) {
            workerThreadCleanDataPeriodically();
            //worker thread to manage no interaction connection
            workerThreadToManageConnections();
            //worker thread to receive data
            workerThreadRecevingData(dos, dis);
            while (true) {
                if (!isSocketConnected(socket)) {
                    System.out.println("Socket connection lost. Attempting to reconnect...");
                    throw new IOException("Socket disconnected");
                }
                // Simulate doing work or sending/receiving data
                Thread.sleep(3000); // Example: simulate a keep-alive message or heartbeat
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //worker thread to clean data periodically

    }

    /**
     * Create a thread to clean data that has no interaction
     */
    private void workerThreadCleanDataPeriodically() {
        Thread dataCleanupThread = new Thread(() -> {
            while (true) {
                cleanupStaleData();
                try {
                    Thread.sleep(1000); // Sleep for 1 second before the next cleanup cycle.
                } catch (InterruptedException e) {
                    e.printStackTrace(); // Handle any interruption and print an error message.
                }
            }
        });
        dataCleanupThread.start();
    }

    /**
     * Create a thread to manage connect that has no interaction within 30 seconds
     */
    private void workerThreadToManageConnections() {
        Thread manageConnectionsThread = new Thread(() -> {
            while (true) {
                manageConnections();
                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        manageConnectionsThread.start();
    }


    private static boolean isSocketConnected(Socket socket) {
        // Implement logic to check if the socket is still connected
        // For example, check if the socket is closed or if input/output streams are still valid
        return !socket.isClosed() && socket.isConnected();
    }

    /**
     * Create a thread to handle incoming request of Content Server and GET Client
     */
    public synchronized void workerThreadRecevingData(DataOutputStream dos, DataInputStream dis) {
        Thread recevingThread = new Thread(() -> {
            boolean listening = true;
            try {
                synchronized (dis) {
                    while (listening) {
                        String str = dis.readUTF();
                        System.out.println(str);
                        if (str.equals("Bup Bup")) {
                            // Receiving default message
                            System.out.println("Received heartbeat message.");
                        } else if (isRequestHeadersValid(str)) {
                            handleValidRequest(str, dis, dos);
                        } else {
                            String badRequestResponse = """
                                         HTTP/1.1 500 Internal Server Error
                                         User-Agent: ATOMClient/1/0
                                         Content-Length: 0
                                    """;
                            dos.writeUTF(badRequestResponse);
                            dos.flush();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        recevingThread.start();
    }

    public synchronized void handleValidRequest(String str, DataInputStream dis, DataOutputStream dos) throws IOException {

        // If fall in here, it could either a PUT or a GET request
        int lamportClock = checkAndGetLamportClock(str);
        //But first, let's check if the lamport clock present
        if (lamportClock >= 0) {
            serverLamportClock.receiveEvent(lamportClock);
            Long timeStamp = System.currentTimeMillis();
            serverLastActiveTime.put(this.socket, timeStamp);
            // TODO: need to use serverLamportClock to update request.
            //  May be send with checkAndGetLamportClock() to modify request's lamport clock value
            if (str.contains("PUT")) {
                handleValidPUTRequest(str, dos, dis, timeStamp);
            } else if (str.contains("GET")) { // GET request
                handleValidGetRequest(dos);
            } else {
                String badRequestResponse = """
                                                 HTTP/1.1 400 Bad Request
                                                 User-Agent: ATOMClient/1/0
                                                 Content-Length: 0
                                            """;
                dos.writeUTF(badRequestResponse);
                dos.flush();
            }
        } else {
            String badRequestResponse = """
                                             HTTP/1.1 400 Bad Request
                                             User-Agent: ATOMClient/1/0
                                             Content-Length: 0
                                        """;
            dos.writeUTF(badRequestResponse);
            dos.flush();
        }
    }

    public synchronized void handleValidPUTRequest(String str, DataOutputStream dos, DataInputStream dis, Long timeStamp) throws IOException {
        String putBody = str.substring(str.indexOf("{"));
        recentWeatherData = putBody;

        // put data
        boolean newDir = dataManipulator.putFeed(str, serverIds.get(this.socket), timeStamp);
        String responseToSuccessPut;
        if (newDir) {
            responseToSuccessPut = """
                HTTP/1.1 201 CREATED
                Content-Type: application/json
                Content-Length: 0;
                """;
        } else {
            responseToSuccessPut = """
                HTTP/1.1 200 OK
                 Content-Type: application/json
                 Content-Length: 0;
                """;
        }
        dos.writeUTF(responseToSuccessPut);
        dos.flush();
    }

    public synchronized void handleValidGetRequest(DataOutputStream dos) throws IOException {
        if (Objects.isNull(recentWeatherData)) {
            String emptyResponse = """
                                                HTTP/1.1 404 Not Found
                                                User-Agent: ATOMClient/1/0
                                                Content-Type: application/json
                                                Content-Length: %d
                                                    
                                                No weather data available.""";
            emptyResponse = String.format(emptyResponse,
                    "No weather data available.".length());
            dos.writeUTF(emptyResponse);
            dos.flush();
        } else {

            String dataResponse = """
                    HTTP/1.1 200 OK
                    User-Agent: ATOMClient/1/0
                    Content-Type: application/json
                    Content-Length: %d
                                                            
                    %s
                    """;
            String jsonData = recentWeatherData;
            dataResponse = String.format(dataResponse, jsonData.length(), jsonData);
            dos.writeUTF(dataResponse);
            dos.flush();
        }
    }
    /**
     * check if {@code Lamport-Value} present in the request then check if the value >= 0
     *
     * @param req A full request sent by a client
     * @return the lamport clock value that presents in the request
     */
    private int checkAndGetLamportClock(String req) {
        int index = req.indexOf("Lamport-Clock");

        if (index > 0) {
            String cutRequest = req.substring(index);
            return retrieveLamportValue(cutRequest);
        }
        return -1;
    }


    public boolean isRequestHeadersValid(String request) {
        if (request.contains("PUT")) {
            return request.contains("Content-Length") &&
                    request.contains("Content-Type") &&
                    request.contains("Lamport-Clock") &&
                    request.contains("User-Agent");

        } else if (request.contains("GET")) {
            return request.contains("Lamport-Clock") &&
                    request.contains("User-Agent");
        } else
            return false;
    }

    /**
     * Get lamport value in the form of key-value pair
     *
     * @param cutRequest From {@code Lamport-Clock:} to the end to the request
     * @return the lamport clock value
     */
    private int retrieveLamportValue(String cutRequest) {
        int indexOfColon = cutRequest.indexOf(":");
        if (indexOfColon > 0) {
            String fromColon = cutRequest.substring(indexOfColon + 1, cutRequest.indexOf("\n"));
            return Integer.parseInt(fromColon.trim());
        }
        return -1;
    }

    /**
     * Clean up data from data directory
     */
    public void cleanupStaleData() {
        try {
            //System.out.println("cleaning data...");
            // Define the directory where data files are stored
            String dataDirectory = "data/";

            // List all files in the data directory
            File[] files = new File(dataDirectory).listFiles();

            if (files != null) {
                // Maximum number of recent updates retained
                int maxUpdatesToRetain = 20;

                // Sort files by last modified timestamp in ascending order (oldest first)
                Arrays.sort(files, Comparator.comparingLong(File::lastModified));

                // Determine the number of files to delete to retain the most recent updates
                int filesToDelete = Math.max(0, files.length - maxUpdatesToRetain);

                // Delete the oldest files (stale data)
                for (int i = 0; i < filesToDelete; i++) {
                    if (files[i].delete()) {
                        System.out.println("Deleted stale data file: " + files[i].getName());
                    } else {
                        System.err.println("Failed to delete stale data file: " + files[i].getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * CLose connection that has no interaction within 30 seconds
     */
    public void manageConnections() {
        long currentTime = System.currentTimeMillis();
        List<Socket> socketsToClose = new ArrayList<>();

        // The client connection is idle for too long, mark it for closure
        for (Socket clientSocket : serverLastActiveTime.keySet()) {
            long lastActiveTime = serverLastActiveTime.get(clientSocket);
            if (currentTime - lastActiveTime > TTL_IN_MILLISECOND) {
                socketsToClose.add(clientSocket);
            }
        }

        // Close idle connections and perform cleanup
        for (Socket socketToClose : socketsToClose) {
            try (DataOutputStream dos = new DataOutputStream(socketToClose.getOutputStream());
            ) {
                dos.writeUTF("Close connection");
                dos.flush();
                String serverId = serverIds.get(socketToClose);

                System.out.println("Closing idle connection with client: " + socketToClose.getRemoteSocketAddress());
                serverIds.remove(socketToClose);
                serverLastActiveTime.remove(socketToClose);
                socketToClose.close();

                cleanupClientFiles(serverId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cleanupClientFiles(String serverId) {
        // Define the data directory where client files are stored
        String dataDirectory = "data/";

        // List all files in the data directory
        File[] files = new File(dataDirectory).listFiles();

        if (files != null) {
            for (File file : files) {
                // Check if the file's name contains the serverId
                if (!file.isDirectory() && file.getName().contains(serverId)) {
                    if (file.delete()) {
                        System.out.println("Deleted data file: " + file.getName());
                    } else {
                        System.err.println("Failed to delete data file: " + file.getName());
                    }
                }
            }
        }
    }

}
