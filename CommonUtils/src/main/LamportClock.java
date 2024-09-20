package src.main;

public class LamportClock {

    private static int currentTime = 0;

    // Two mode of maintain event order
    // mode 1: local time
    /**
     * Increments the Lamport clock value by 1.
     */
    synchronized int localEvent() {
        return  currentTime++;
    }

    // mode 2: receiving event from other process
    /**
     * Updates the Lamport clock value based on the received value.
     *
     * @param receiveTime The received Lamport clock value.
     */
    synchronized int receiveEvent(int receiveTime) {
        return Math.max(currentTime, receiveTime) + 1;
    }
    /**
     * Retrieves the current Lamport clock value.
     *
     * @return The current Lamport clock value.
     */
    public synchronized int issueLamportClockValue() {
        return currentTime++;
    }
    public synchronized int getValue() {
        return currentTime;
    }

    public synchronized void appendLamportClockToRequest(Request request) {
        request.setLamportClockValue(localEvent());
    }
}
