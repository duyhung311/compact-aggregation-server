package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.Map;

public class SocketConnection {
    private int iAmPort;
    private static int connectToPort;
    private static SocketConnection instance = null;
    ServerSocket serverSocket;
    Map<Integer, Socket> serverSockets;
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    private SocketConnection(int connectToPort) {
        SocketConnection.connectToPort = connectToPort;
        serverSockets = new HashMap<>();
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
            int i = 0;
            while (true) {
                System.out.println("start");

                Socket s1 = serverSocket.accept();
                s1.setKeepAlive(true);
                serverSockets.put(i++, s1);
                Thread t1 = new Thread();
                t1.start();
                System.out.println("started");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Socket makeClient() throws IOException {
        socket = new Socket("127.0.0.1", connectToPort);
        socket.setKeepAlive(true);
        System.out.println(socket.getLocalAddress());
        System.out.println(socket.getLocalPort());
        System.out.println(socket.getPort());
        System.out.println(socket.getRemoteSocketAddress());
        System.out.println(socket.getReuseAddress());
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
