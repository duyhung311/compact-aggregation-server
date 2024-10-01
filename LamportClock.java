
public class LamportClock {

    private static int currentTime = 0;

    // Two mode of maintain event order
    // mode 1: local time
    /**
     * Increase the Lamport clock value by 1.
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
        currentTime = Math.max(currentTime, receiveTime) + 1;
        return currentTime;
    }
    /**
     * Retrieves the current Lamport clock value and increase by 1 for future usage
     *
     * @return The current Lamport clock value.
     */
    public synchronized int issueLamportClockValue() {
        return currentTime++;
    }

}
