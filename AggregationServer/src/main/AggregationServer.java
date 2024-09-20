package src.main;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;

public class AggregationServer {
    AggregationServer() {

    }

    public static void main(String[] args) {

        int portNumber = 4567;
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Aggregation Server started on port " + portNumber);
            String dataDirectory = "./data/";
            File directory = new File(dataDirectory);
            if (!directory.exists()) {
                if (directory.mkdirs()) {
                    System.out.println("Data directory created: " + dataDirectory);
                } else {
                    System.err.println("Failed to create data directory: " + dataDirectory);
                    return;
                }
            }
            while (listening) {
                new AggregationServerMultiThread(serverSocket.accept(), UUID.randomUUID().toString()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }



}
