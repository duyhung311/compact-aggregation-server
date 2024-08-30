package src;

import java.net.URI;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class CustomWebSocketBuilder implements WebSocket.Builder {
    @Override
    public WebSocket.Builder header(String name, String value) {
        return null;
    }

    @Override
    public WebSocket.Builder connectTimeout(Duration timeout) {
        return null;
    }

    @Override
    public WebSocket.Builder subprotocols(String mostPreferred, String... lesserPreferred) {
        return null;
    }

    @Override
    public CompletableFuture<WebSocket> buildAsync(URI uri, WebSocket.Listener listener) {
        return null;
    }
}
