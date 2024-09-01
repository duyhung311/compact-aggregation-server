package src;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.file.Watchable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class CustomWebSocketListener implements WebSocket.Listener {
    DataManipulator dataManipulator;
    public CustomWebSocketListener() {
        dataManipulator = new DataManipulator();
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<WeatherData> onText(WebSocket webSocket, CharSequence data, boolean last) {
        CompletionStage<WeatherData> completionStage = new CompletableFuture<>();
        completionStage.thenAcceptAsync(x -> System.out.println(x.id));
        return completionStage;
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        return WebSocket.Listener.super.onPing(webSocket, message);
    }

    @Override
    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
        return WebSocket.Listener.super.onPong(webSocket, message);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        WebSocket.Listener.super.onError(webSocket, error);
    }
}
