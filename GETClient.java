import java.io.*;
import java.net.Socket;

public class GETClient {
    private static final LamportClock lamportClock = new LamportClock();
    public static void main(String[] args) {
        if (args.length < 1) {
            System.exit(1);
        }

        // Extract the server URL from command line arguments
        String clientUrl = args[0];
        String host = clientUrl.split(":")[0];
        int port = Integer.parseInt(clientUrl.split(":")[1]);
        boolean listening = true;
        System.out.println(host +  " " + port);
        try (Socket socket = new Socket(host, port);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream outputData = new DataOutputStream(socket.getOutputStream());
        ) {

            startHeartbeatThread(outputData);
            // Send a GET request to the server
            sendGetRequest(outputData, dis);
//            processServerResponse(inputReader);
            while (listening) {}
            // Process the server's response
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void sendGetRequest(DataOutputStream outputData, DataInputStream dis) throws IOException {
        Thread getRequestThread = new Thread(() -> {
            while (true) {
                String response = """
                        GET /weather.json HTTP/1.1
                        User-Agent: ATOMClient/1/0
                        Accept: application/json
                        Lamport-Clock: %d
                        """;
                response = String.format(response, lamportClock.issueLamportClockValue());
                try {
                    outputData.writeUTF(response);
                    outputData.flush();
                    System.out.println(dis.readUTF());
                    Thread.sleep(3000);
                } catch (IOException | InterruptedException e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }

            }
        });
        getRequestThread.start();
    }

    static void startHeartbeatThread(DataOutputStream outputData) {
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
