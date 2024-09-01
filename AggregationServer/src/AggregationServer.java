package src;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;

public class AggregationServer {
    SocketConnection socketConnection;
    CustomWebSocketListener customWebSocketListener;

    AggregationServer() {
        customWebSocketListener = new CustomWebSocketListener();
        socketConnection = SocketConnection.getInstance(4567);

    }

    public static void main(String[] args) {


        int portNumber = 4567;
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new AggregationServerMultiThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }


    //@Override
//    public void run() {
//        System.out.println("Sever started");
//        socketConnection.makeServer();
//
////        socketConnection.serverSockets.get(1).getInputStream();
//        DataInputStream dis = null;
//        try {
//
//            dis = new DataInputStream(socketConnection.serverSockets.get(1).getInputStream());
//            String str = dis.readUTF();
//            System.out.println("message= " + str);
//            WeatherData wd = Serializer.fromJSON(str);
//            System.out.println(wd.lat);
//            //socketConnection.serverSockets.get(1).close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
