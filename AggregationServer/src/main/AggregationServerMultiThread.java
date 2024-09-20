package src.main;

import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AggregationServerMultiThread extends Thread {
    private Socket socket = null;
    private final DataManipulator dataManipulator;
    private static final int TTL_IN_MILLISECOND = 30000;
    private static final Map<Socket, String> serverIds = new ConcurrentHashMap<>();
    private static final Map<Socket, Long> serverLastActiveTime = new ConcurrentHashMap<>();
    private static WeatherData recentWeatherData = null;
    private static final Map<String, String> dataMap = new ConcurrentHashMap<>();
    public AggregationServerMultiThread(Socket socket, String uuid) {
        super("AggregationServerMultiThread");
        System.out.println("A client started");
        this.socket = socket;
        this.dataManipulator = new DataManipulator();
        serverIds.put(socket, uuid);
    }

    @Override
    public void run() {
        System.out.println("AggregationServerMultiThread started");
        //check data from corruption before start up
        //worker thread to clean data periodically
        workerThreadCleanDataPeriodically();
        //worker thread to manage no interaction connection
        workerThreadToManageConnections();
        //worker thread to receive data
        workerThreadRecevingData();
    }

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

    private void workerThreadRecevingData() {
        Thread recevingThread = new Thread(this::handlingIncomingData);
        recevingThread.start();
    }

    private void handlingIncomingData() {
        boolean listening = true;

        try (
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        ) {

            while (listening) {
                String str = dis.readUTF();
                if (str.equals("Bup Bup")) {
                    System.out.println("Received heartbeat message.");
                } else {

                    boolean isLamportPresent = checkLamportClock(str);
                    if (isLamportPresent) {
                        if (str.contains("PUT")) {
                            String putBody = str.substring(str.indexOf("{"));
                            recentWeatherData = Serializer.fromJSONToSingleEntity(putBody);
                            Long timeStamp = System.currentTimeMillis();
                            serverLastActiveTime.put(this.socket, timeStamp);

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
                            // then remove outdated data from where?

                        } else if (str.contains("GET")) { // GET request
                            // return recent data
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
                                    
                                    %s""\"
                                    """;
                                String jsonData = Serializer.toJSON(recentWeatherData);
                                dataResponse = String.format(dataResponse,jsonData.length(), jsonData);
                                dos.writeUTF(dataResponse);
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
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkLamportClock(String req) {
        int index = req.indexOf("Lamport-Clock");

        if ( index > 0) {
            String cutRequest = req.substring(index);
            return retrieveLamportValue(cutRequest, req.contains("PUT")) >= 0;

        }
        return false;
    }

    private int retrieveLamportValue(String cutRequest, boolean isPut) {
        int indexOfColon = cutRequest.indexOf(":");
        if ( indexOfColon > 0) {
            String fromColon  = cutRequest.substring(indexOfColon + 1, cutRequest.indexOf("\n"));
            return Integer.parseInt(fromColon.trim());
        }
        return -1;
    }

    private void cleanupStaleData() {
        try {
            //System.out.println("cleaning data...");
            // Define the directory where data files are stored
            String dataDirectory = "../data/";

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
            try {
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
                    if ( file.delete()) {
                        System.out.println("Deleted client-specific data file: " + file.getName());
                    } else {
                        System.err.println("Failed to delete client-specific data file: " + file.getName());
                    }
                }
            }
        }
    }

    public void getFeed() {
        // access file (lock access to file as well) TODO: (after this line, unlock)?  no
        // once get the data, transform into list weather data and filter data within recent 20 seconds
        // access file to write back filter (may handle corruption and server collapse) data while returning to GET client


    }

    public void putFeed() {
        // access file (lock all access to file as well)
        // start appending new data to data
        
    }
}
