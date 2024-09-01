import src.Serializer;
import src.WeatherData;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ContentServerOnThread extends Thread {
    private final Socket socket;
    ContentServerOnThread(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemp(20);
        DataOutputStream dout = null;
        try {
            dout = new DataOutputStream(socket.getOutputStream());
            dout.writeUTF(Serializer.toJSON(weatherData));
            dout.flush();
            dout.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
