import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.assertTrue;

public class ContentServerTests {
//    @Test
//    public void testSendDataToServerSuccess() throws IOException {
//        // Mock socket creation
//        Socket socket = Mockito.mock(Socket.class);
//        Mockito.when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
//        Mockito.when(socket.getInputStream()).thenReturn(new ByteArrayInputStream("HTTP/1.1 201 Created".getBytes()));
//
//        Mockito.when(lamportClock.getValue()).thenReturn(1);
//
//        // Create a temporary file with some data
//        File tempFile = File.createTempFile("temp-feed", ".txt");
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
//            writer.write("id: value\n");
//        }
//
//        assertTrue(ContentServer.sendDataToServer("example.com:80", tempFile.getAbsolutePath(), lamportClock));
//    }
}
