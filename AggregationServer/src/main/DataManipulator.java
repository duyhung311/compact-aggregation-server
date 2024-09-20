package src.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataManipulator {

    private static final String defaultFileName = "data.json";
    private static final String defaultTempFileName = "data_temp.json";
    private static final String defaultDir = "./data/";
    private final File defaultFile;

    public DataManipulator() {
        defaultFile = new File(defaultFileName);
    }

    public void putFeed(Request req, String uuid, Long timestamp) {

        String fileName = defaultDir + uuid + "_" + timestamp + ".txt";
        StringBuilder content = new StringBuilder();
        content.append("Method: ").append(req.method()).append("\n");
        content.append("Content-Length: ").append(req.contentLength()).append("\n");
        content.append("User-Agent: ").append(req.userAgent()).append("\n");
        content.append("Accept: ").append(req.accept()).append("\n");
        content.append("Lamport-Value: ").append(req.lamportClockValue()).append("\n");
        content.append(req.data());
        File directory = new File(fileName);
        try {
            FileWriter fileWriter = new FileWriter(directory);
            fileWriter.write(content.toString());
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error storing data: " + e.getMessage());
        }


    }


    public void updateFeed(Request req, String uuid, String timeStamp) {

    }
    public boolean appendData() {
        return true;
    }


    public boolean createTempFile(String data) {
        return true;
    }


    public void writeBack(String data) {

    }

    public void deleteTempFile() {

    }

    public String readFile() {
        return "";
    }

    public boolean createFile() {
        try {
            File myObj = new File(defaultFileName);
            return myObj.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
            return false;
        }
    }

    public boolean checkFileExist() {
        return defaultFile.exists();
    }


    public boolean isMainFileCorrupted() {
        return Serializer.parseJSON(readFile());

    }

    public void recoverMainFromTempFile() {
        File tempFile = new File(defaultTempFileName);
    }
}
