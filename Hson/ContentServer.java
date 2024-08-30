import src.SocketConnection;

import java.io.IOException;
import java.net.Socket;

public class ContentServer {

    SocketConnection socketConnection;
    Socket socket;
    public static void main(String[] args) throws IOException {
        ContentServer server = new ContentServer();
        server.socketConnection = SocketConnection.getInstance(123213);
        server.socket = server.socketConnection.makeClient();

    }
}
