package src;

public class LamportClock {

    private DataManipulator dataManipulator;

    public void updateTime(int a){

    }

    // Two mode of maintain event order
    // mode 1: local time
    int localEvent(int currentTime) {
        return  ++currentTime;
    }

    // mode 2: receiving event from other process
    int receiveEvent(int localTime, int recieveTime) {
        return Math.max(localTime, recieveTime) + 1;
    }


}
