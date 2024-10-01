import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class GETClientTests {
    @Mock
    private DataOutputStream mockOutputData;
    @Mock
    private DataInputStream mockDis;
    @Mock
    private LamportClock mockLamportClock;

    @Before
    public void setUp() {
        mockOutputData = mock(DataOutputStream.class);
        mockDis = mock(DataInputStream.class);
        mockLamportClock = mock(LamportClock.class);
    }

    @Test
    public void testSendGetRequest() throws IOException, InterruptedException {
        // Mock the lamport clock value
        when(mockLamportClock.issueLamportClockValue()).thenReturn(1);

        // Mock the input stream to return a fixed response
        when(mockDis.readUTF()).thenReturn("HTTP/1.1 200 OK");

        // Create a thread to test the method, and use a shorter sleep time
        Thread testThread = new Thread(() -> {
            try {
                GETClient.sendGetRequest(mockOutputData, mockDis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        testThread.start();
        Thread.sleep(500); // Give some time for the thread to run at least once

        // Verify that the request was written to the output stream
        String expectedRequest = """
                GET /weather.json HTTP/1.1
                User-Agent: ATOMClient/1/0
                Accept: application/json
                Lamport-Clock: 0
                """;
        verify(mockOutputData).writeUTF(expectedRequest);
        verify(mockOutputData).flush();

        // Verify that the input stream was read
        verify(mockDis).readUTF();

        // Stop the thread (in a real-world scenario, we'd need a way to gracefully stop this)
        testThread.interrupt();
    }

    @Test
    public void testStartHeartbeatThread() throws IOException, InterruptedException {
        // Start the heartbeat thread
        Thread testThread = new Thread(() -> GETClient.startHeartbeatThread(mockOutputData));

        testThread.start();
        Thread.sleep(500); // Give it time to run at least once (less than 20 seconds)

        // Verify that the heartbeat message was sent to the output stream
        verify(mockOutputData).writeUTF("Bup Bup");
        verify(mockOutputData).flush();

        // Stop the thread after the first heartbeat is sent
        testThread.interrupt();
    }


}
