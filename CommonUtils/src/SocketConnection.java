package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.http.WebSocket;

public class SocketConnection {
    private int iAmPort;
    private static int connectToPort;
    private static SocketConnection instance = null;
    ServerSocket serverSocket;
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    private SocketConnection(int connectToPort) {
        SocketConnection.connectToPort = connectToPort;
    }

    public static SocketConnection getInstance(int serverPort) {
        if (instance == null) {
            instance = new SocketConnection(serverPort);
        } else {

            System.err.printf("Server %d already running.\n", serverPort);
        }
        return instance;
    }

    public void makeServer() {
        try {
            serverSocket = new ServerSocket(connectToPort);
            setConnectToPort(connectToPort);
            socket = serverSocket.accept();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Socket makeClient() throws IOException {
        socket = new Socket("127.0.0.1", connectToPort);
        return socket;
    }

    private void setConnectToPort(int runOnPort) {
        connectToPort = runOnPort;
    }

    public SocketConnection setiAmPort(int iAmPort) {
        this.iAmPort = iAmPort;
        return this;
    }

    public WeatherData acceptRemoteRequest() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        //parse in.
        return new WeatherData();
    }
}
