package src;

import java.util.concurrent.BlockingQueue;

public class DataManipulator {
    BlockingQueue<LamportClockElement> dataQueue;

    public BlockingQueue<LamportClockElement> dataQueue() {
        return dataQueue;
    }

    public DataManipulator setDataQueue(BlockingQueue<LamportClockElement> dataQueue) {
        this.dataQueue = dataQueue;
        return this;
    }
}
