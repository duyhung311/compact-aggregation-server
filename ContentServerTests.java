import org.junit.Before;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ContentServerTests {
    private DataOutputStream mockDout;
    private DataInputStream mockDin;
    private LamportClock mockLamportClock;
    private ResponseParser mockResponseParser;

    @Before
    public void setUp() {
        mockDout = mock(DataOutputStream.class);
        mockDin = mock(DataInputStream.class);
        mockLamportClock = mock(LamportClock.class);
        mockResponseParser = mock(ResponseParser.class);
    }
    @Test
    public void testWorkerThreadToStartFeedingData() throws IOException, InterruptedException {
        // Mock the data to be sent
        List<String> mockData = Arrays.asList("{\"temp\": 30}", "{\"temp\": 31}");

        // Mock lamportClock values
        when(mockLamportClock.issueLamportClockValue()).thenReturn(0);

        // Mock the input stream to return a fixed response
        when(mockDin.readUTF()).thenReturn("HTTP/1.1 200 OK");

        // Mock the messageParser method to simulate processing the server response
        when(mockResponseParser.messageParser(anyString())).thenReturn("Parsed Response");

        // Mock convertDataToJson to return mock data
        List<String> mockDataToBeFeeded = Arrays.asList("{\"data\":\"sample1\"}", "{\"data\":\"sample2\"}");
        when(ContentServer.convertDataToJson(anyString())).thenReturn(mockDataToBeFeeded);

        // Start the thread that simulates feeding the data
        Thread testThread = new Thread(() -> ContentServer.workerThreadToStartFeedingData(mockDout, mockDin));

        testThread.start();
        Thread.sleep(500); // Allow the thread to run at least once

        // Verify the interactions
        for (String data : mockDataToBeFeeded) {
            String expectedRequest = String.format("""
                    PUT /weather.json HTTP/1.1
                    User-Agent: ATOMClient/1/0
                    Content-Type: application/json
                    Content-Length: %d
                    Lamport-Clock: %d
                                        
                    %s
                    """, data.length(), 123L, data);

            verify(mockDout).writeUTF(expectedRequest);
            verify(mockDout).flush();
            verify(mockDin).readUTF();
            verify(mockResponseParser).messageParser(anyString());
        }

        // Stop the thread
        testThread.interrupt();
    }
}
