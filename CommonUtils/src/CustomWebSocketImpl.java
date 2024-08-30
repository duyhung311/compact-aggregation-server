package src;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class CustomWebSocketImpl implements WebSocket, Runnable {

    BlockingQueue<Request> requestQueue = new LinkedBlockingQueue<>();

    @Override
    public CompletableFuture<WebSocket> sendText(CharSequence data, boolean last) {
        return null;
    }

    @Override
    public CompletableFuture<WebSocket> sendBinary(ByteBuffer data, boolean last) {
        return null;
    }

    @Override
    public CompletableFuture<WebSocket> sendPing(ByteBuffer message) {
        return null;
    }

    @Override
    public CompletableFuture<WebSocket> sendPong(ByteBuffer message) {
        return null;
    }

    @Override
    public CompletableFuture<WebSocket> sendClose(int statusCode, String reason) {
        return null;
    }

    @Override
    public void request(long n) {

    }

    @Override
    public String getSubprotocol() {
        return "";
    }

    @Override
    public boolean isOutputClosed() {
        return false;
    }

    @Override
    public boolean isInputClosed() {
        return false;
    }

    @Override
    public void abort() {

    }

    @Override
    public void run() {

    }
}
