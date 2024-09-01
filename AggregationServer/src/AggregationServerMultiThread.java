package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AggregationServerMultiThread extends Thread{
    private Socket socket = null;
    public AggregationServerMultiThread(Socket socket) {
        super("AggregationServerMultiThread");
        System.out.println("AggregationServerMultiThread started");
        this.socket = socket;
    }

    @Override
    public void run() {

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String inputLine, outputLine;
//            KnockKnockProtocol kkp = new KnockKnockProtocol();
//            outputLine = kkp.processInput(null);
//            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                //outputLine = kkp.processInput(inputLine);
                out.println(inputLine);

            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
