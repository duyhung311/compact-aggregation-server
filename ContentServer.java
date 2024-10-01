import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ContentServer {
    private final static LamportClock lamportClock = new LamportClock();
    private static String dataFileLocation;
    ContentServer() {
    }


    /**
     * Start up a new Content server to connect to Aggregation Server <br>
     * Create a thread to feed data to Aggregation Server<br>
     * Keep this content Server on all the time to feed data continuously with a while loop<br>
     * @param args
     */
    public static void main(String[] args) {
        if (args.length !=2) {
            System.exit(1);
        }
        String serverUrl = args[0];
        dataFileLocation = args[1];

        String host = serverUrl.split(":")[0];
        int port = Integer.parseInt(serverUrl.split(":")[1]);
        try (
            Socket kkSocket = new Socket(host, port);
            DataOutputStream dout = new DataOutputStream(kkSocket.getOutputStream());
            DataInputStream din = new DataInputStream(kkSocket.getInputStream())
        ) {
//            workerThreadToStartFeedingData(dout, din);
            List<String> dataToBeFeeded = convertDataToJson(dataFileLocation);
            for (String data : dataToBeFeeded) {
                String request = """
                        PUT /weather.json HTTP/1.1
                        User-Agent: ATOMClient/1/0
                        Content-Type: application/json
                        Content-Length: %d
                        Lamport-Clock: %d
                                                
                        %s
                        """;
                request = String.format(request,
                        data.length(),
                        lamportClock.issueLamportClockValue(),
                        data);
                System.out.println(request);
                try {
                    dout.writeUTF(request);
                    dout.flush();

                    System.out.println(ResponseParser.messageParser(din.readUTF()));
                    Thread.sleep(3000);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
            while (true) {}
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + "localhost");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    "localhost");
            System.exit(1);
        }
    }

    /**
     * <ul>
     * <li>Start an individual thread to send data via PUT request to Aggregation Server</li>
     * <li>Data to be sent contains in {@code weather_data.txt} </li>
     * <li>The thread is designed to parse whole data at once and send them every 3 seconds </li>
     * <li>After sending, the thread also receive response from Aggregation Server</li>
     *</ul>
     * @param dout {@code DataOutputStream} to send request to Aggregation Server
     * @param din {@code DataInputStream} to receive response from Aggregation Server
     */
    public static void workerThreadToStartFeedingData(DataOutputStream dout , DataInputStream din) {
        Thread feedData = new Thread(() -> {
            // for each data in data file, after sending wait for reply and sleep for 2 seconds


        });
        feedData.start();
    }


    /**
     * Read all pieces of data in the
     * @param filePath
     * @return
     */
    public static List<String> convertDataToJson(String filePath) {
        List<String> dataToBeProcessed = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder jsonDataBuilder = new StringBuilder(); // Initialize a StringBuilder to build the JSON data
            String line;
            boolean firstEntry = true;
            boolean validID = false;
            // Read a line from the input file, and continue while there are more lines
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()){
                    // identify end of an entry by a blank line
                    // Print an error message if 'id' key is missing
                    if (!validID) {
                        throw new IllegalArgumentException("Error: The first key is not 'id'.");
                    }

                    // Add the closing brace for the JSON object
                    if (!firstEntry) {
                        jsonDataBuilder.append("}");
                    }
                    dataToBeProcessed.add(jsonDataBuilder.toString());
                    firstEntry = true;
                    validID = false;
                    jsonDataBuilder.delete(
                            0, jsonDataBuilder.length()
                    );
                    continue;
                }
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    // Extract the key and value parts
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    // Add a comma to separate JSON entries, if needed
                    if (!jsonDataBuilder.isEmpty()) {
                        jsonDataBuilder.append(",");
                    }

                    // Add the opening brace for the JSON object

                    if (firstEntry) {
                        jsonDataBuilder.append("{");
                        firstEntry = false;
                        if (key.equals("id")) {
                            validID = true;
                        } else {
                            return new ArrayList<>();
                        }
                    }

                    // Indicate that the 'id' key exists in the input
                    // Build a JSON key-value pair
                    jsonDataBuilder.append("\"").append(key).append("\":\"").append(value).append("\"");
                }
            }
            if (!jsonDataBuilder.isEmpty() &&
                    !jsonDataBuilder.toString().endsWith("}")
                    && jsonDataBuilder.toString().startsWith("{")
            ) {
                jsonDataBuilder.append("}");
               dataToBeProcessed.add(jsonDataBuilder.toString());
            }


        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return dataToBeProcessed;
    }


}
