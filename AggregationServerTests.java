import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Integration test and unit tests included
 */
public class AggregationServerTests {

    @Mock
    private DataInputStream dis;
    @Mock
    private DataOutputStream dos;
    @Mock
    private Socket mockSocket;
    @Mock
    private AggregationServerMultiThread aggregationServer;
    @Mock
    private DataManipulator dataManipulator;
    @BeforeEach
    public void setUp() throws IOException {

        mockSocket = mock(Socket.class);
        dis = mock(DataInputStream.class);
        dos = mock(DataOutputStream.class);
        aggregationServer = new AggregationServerMultiThread(mockSocket, "uuid");  // Your class' constructor
        when(mockSocket.getInputStream()).thenReturn(dis);
        when(mockSocket.getOutputStream()).thenReturn(dos);
        dataManipulator = mock(DataManipulator.class);
    }
    @AfterEach
    public void reset() throws IOException {
        Mockito.reset(dis, dos);
    }

    @Test
    public void testWorkerThreadRecevingData_HandlesHeartbeat() throws IOException, InterruptedException {
        // Mock the incoming "Bup Bup" heartbeat message
        when(dis.readUTF()).thenReturn("Bup Bup");
        // Act: Start the worker thread
        aggregationServer.workerThreadRecevingData(dos, dis);

        // Sleep to give the thread some time to process (adjust the sleep time if necessary)
        Thread.sleep(1000);
        verify(dis, atLeastOnce()).readUTF(); // At least read once
    }

    @Test
    public void testWorkerThreadRecevingData_HandlesGETRequest_NoData() throws IOException, InterruptedException {
        // Arrange: Mock a GET request with no data available
        String getRequest = MockRequests.GET_REQUEST;

        when(dis.readUTF()).thenReturn(getRequest); // Simulate a GET request

        // Act: Start the worker thread
//        aggregationServer.workerThreadRecevingData(dos, dis);
        aggregationServer.handleValidGetRequest(dos);
        // Sleep to give the thread some time to process (adjust the sleep time if necessary)
        Thread.sleep(10);

        // Assert: Verify that the 404 response is sent when no data is available
        verify(dos, atLeastOnce()).writeUTF(contains("404 Not Found"));
    }

    @Test
    public void testWorkerThreadRecevingData_HandlesValidPUTRequest() throws IOException, InterruptedException {
        // Arrange: Mock a PUT request
        String putRequest = MockRequests.VALID_PUT_REQUEST;

        when(dis.readUTF()).thenReturn(putRequest); // Simulate a PUT request

        // Act: Start the worker thread
//        aggregationServer.workerThreadRecevingData(dos, dis);
        aggregationServer.handleValidPUTRequest(putRequest, dos, dis, System.currentTimeMillis());

        // Sleep to give the thread some time to process (adjust the sleep time if necessary)
        Thread.sleep(10);

        // Assert: Verify that the response to the PUT request is written correctly
        verify(dos, atLeastOnce()).writeUTF(contains("HTTP/1.1 200 OK"));

    }

    @Test
    public void testWorkerThreadRecevingData_HandlesNoLamportClockRequest() throws IOException, InterruptedException {
        // Arrange: Mock a PUT request
        String putRequest = MockRequests.NO_LAMPORT_CLOCK_REQUEST;

        when(dis.readUTF()).thenReturn(putRequest); // Simulate a PUT request

        // Act: Start the worker thread
        aggregationServer.workerThreadRecevingData(dos, dis);

        // Sleep to give the thread some time to process (adjust the sleep time if necessary)
        Thread.sleep(10);

        // Assert: Verify that the response to the PUT request is written correctly
        verify(dos, atLeastOnce()).writeUTF(contains("HTTP/1.1 400 Bad Request"));
    }

    @Test
    public void testServerLostConnection() {

    }

    @Test
    public void testWorkerThreadRecevingData_HandlesBrokeRequest() throws IOException, InterruptedException {
        // Arrange: Mock a PUT request
        String putRequest = MockRequests.BROKE_REQUEST;

        when(dis.readUTF()).thenReturn(putRequest); // Simulate a PUT request

        // Act: Start the worker thread
        aggregationServer.workerThreadRecevingData(dos, dis);

        // Sleep to give the thread some time to process (adjust the sleep time if necessary)
        Thread.sleep(10);


        // Assert: Verify that the response to the PUT request is written correctly
        verify(dos, atLeastOnce()).writeUTF(contains("HTTP/1.1 500 Internal Server Error"));
    }

    @Test
    public void unitTestCleanupStaleData() throws IOException {
        // create 30 files
        String dataDirectory = "data/";
        for (int i =0; i < 30; i++) {
            String fileNames = i+".txt";
            // List all files in the data directory
            File file = new File(dataDirectory+fileNames);
            if (!file.exists()) {
                file.createNewFile();
            }
        }

        // run cleanUpStaleData()
        aggregationServer.cleanupStaleData();


        // List all files in the data directory
        File[] files = new File(dataDirectory).listFiles();
        int countFile= 0;
        for (File file : files) {
            if (file.isFile())
                countFile++;
        }
        // delete file first to prevent conflict
        // then assertEquals() later
        for (File file : files) {
            file.delete();
        }

        assertEquals(20, countFile);
    }


    /**
     * Since 200 or 201 response base on the existence of the server uuid directory, so I test
     * it if the directory exists. <br>
     * If it it exist {@code putFeed()} will return false, signaling a 200 response
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testStatusCode201() throws IOException, InterruptedException {
        String putRequest = MockRequests.VALID_PUT_REQUEST;
        Long time = System.currentTimeMillis();
        File dir = new File("data/uuid");
        dir.mkdir();
        File file = new File("data/uuid/"+time+".txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        boolean is201 = dataManipulator.putFeed(putRequest, "uuid", time);

        assertFalse(is201);
    }


}
