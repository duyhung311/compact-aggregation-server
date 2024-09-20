package src.main;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class GETClient {
    private static final LamportClock lamportClock = new LamportClock();
    public static void main(String[] args) {
        if (args.length < 1) {
            System.exit(1);
        }

        // Extract the server URL from command line arguments
        String clientUrl = args[0];
        String host = clientUrl.split(":")[0];
//        String host = "127.0.0.1";
        int port = Integer.parseInt(clientUrl.split(":")[1]);
//        int port = 4567;
        boolean listening = true;
        // Open a socket connection to the server
        try (Socket socket = new Socket(host, port);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream outputData = new DataOutputStream(socket.getOutputStream());
             BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            startHeartbeatThread(outputData, dis);
            // Send a GET request to the server
            sendGetRequest(outputData, dis);
//            processServerResponse(inputReader);
            while (listening) {}
            // Process the server's response
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendGetRequest(DataOutputStream outputData, DataInputStream dis) throws IOException {
        Thread getRequestThread = new Thread(() -> {
            while (true) {
                String request = """
                        GET /weather.json HTTP/1.1
                        User-Agent: ATOMClient/1/0
                        Accept: application/json
                        Lamport-Clock: %d
                        """;
                request = String.format(request, lamportClock.issueLamportClockValue());
                try {
                    outputData.writeUTF(request);
                    outputData.flush();
                    System.out.println(dis.readUTF());
                    Thread.sleep(3000);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        getRequestThread.start();
    }

    private static void startHeartbeatThread(DataOutputStream outputData, DataInputStream dis) {
        Thread heartbeatThread = new Thread(() -> {
            while (true) {
                String heartbeatMessage = "Bup Bup";

                // Write the message to the output stream and flush it
                try {
                    System.out.println("Heartbeat sent");
                    outputData.writeUTF(heartbeatMessage);
                    outputData.flush();

                    // Sleep for 20 seconds before sending the next heartbeat
                    Thread.sleep(20000);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }


            }
        });
        heartbeatThread.start();
    }
}
