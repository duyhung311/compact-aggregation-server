import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class ContentServerTests {
    private DataOutputStream mockDout;
    private DataInputStream mockDin;
    private LamportClock mockLamportClock;

    @Before
    public void setUp() {
        mockDout = mock(DataOutputStream.class);
        mockDin = mock(DataInputStream.class);
        mockLamportClock = mock(LamportClock.class);
    }

    @BeforeEach
    public void reset() {
        Mockito.reset(mockDout);
        Mockito.reset(mockDin);
        Mockito.reset(mockLamportClock);

    }
    @Test
    public void testWorkerThreadToStartFeedingData() throws IOException, InterruptedException {
        // Mock lamportClock values
        when(mockLamportClock.issueLamportClockValue()).thenReturn(0);

        // Mock the input stream to return a fixed response
        when(mockDin.readUTF()).thenReturn("HTTP/1.1 200 OK");

        // Mock the messageParser method to simulate processing the server response

        // Mock convertDataToJson to return mock data
        List<String> mockDataToBeFeeded = Arrays.asList("{\"data\":\"sample1\"}");

        // Start the thread that simulates feeding the data
        Thread testThread = new Thread(() -> ContentServer.workerThreadToStartFeedingData(mockDataToBeFeeded, mockDout, mockDin));

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
                    """, data.length(), 0, data);

            verify(mockDout).writeUTF(expectedRequest);
            verify(mockDout).flush();
            verify(mockDin).readUTF();
        }

        // Stop the thread
        testThread.interrupt();
    }

    @Test
    public void testWorkerThreadToStartFeedingData_BadRequest() throws IOException, InterruptedException {
        // Mock lamportClock values
        when(mockLamportClock.issueLamportClockValue()).thenReturn(0);

        // Mock the input stream to return a fixed response
        when(mockDin.readUTF()).thenReturn("HTTP/1.1 400 Bad Request");

        // Mock the messageParser method to simulate processing the server response

        // Mock convertDataToJson to return mock data
        List<String> mockDataToBeFeeded = Arrays.asList("{\"data\":\"sample1\"}");

        // Start the thread that simulates feeding the data
        Thread testThread = new Thread(() -> ContentServer.workerThreadToStartFeedingData(mockDataToBeFeeded, mockDout, mockDin));

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
                    """, data.length(), 1, data);

            verify(mockDout).writeUTF(expectedRequest);
            verify(mockDout).flush();
            verify(mockDin).readUTF();
        }

        // Stop the thread
        testThread.interrupt();
    }


}
