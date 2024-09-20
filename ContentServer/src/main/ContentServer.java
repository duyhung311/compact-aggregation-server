package src.main;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ContentServer {
    private final static LamportClock lamportClock = new LamportClock();
    ContentServer() {
    }

    public static void main(String[] args) {

        try (
            Socket kkSocket = new Socket("127.0.0.1", 4567);
            DataOutputStream dout = new DataOutputStream(kkSocket.getOutputStream())
        ) {
            while (true) {
                // fake data
                WeatherData weatherData = new WeatherData();
                weatherData.setAirTemp((1+1)*10);

                Request req = Request.buildPutRequestWithData(weatherData);
                lamportClock.appendLamportClockToRequest(req);
                dout.writeUTF(Serializer.toJSON1(req));
                dout.flush();
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + "localhost");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    "localhost");
            System.exit(1);
        }
    }


}
