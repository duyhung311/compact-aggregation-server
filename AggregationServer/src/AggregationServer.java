package src;

import java.io.IOException;

public class AggregationServer {
    SocketConnection socketConnection;
    CustomWebSocketListener customWebSocketListener;

    AggregationServer() {
        customWebSocketListener = new CustomWebSocketListener();
        socketConnection = SocketConnection.getInstance(4567);

    }

    public static void main(String[] args) throws InterruptedException, IOException {
        AggregationServer server = new AggregationServer();

        server.socketConnection = SocketConnection.getInstance(6666);
        server.socketConnection.makeServer();
        Thread.sleep(2000);
        System.out.println("slept 2s now turn off");
        server.socketConnection.serverSocket.close();
    }
}
