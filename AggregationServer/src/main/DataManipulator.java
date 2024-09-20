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

    public boolean putFeed(String req, String uuid, Long timestamp) {

        String fileName = defaultDir + "/"  + uuid + "/" + timestamp + ".txt";
        boolean newDir = new File(defaultDir + "/"  + uuid + "/").mkdirs();
        File directory = new File(fileName);
        try {
            FileWriter fileWriter = new FileWriter(directory);
            fileWriter.write(req);
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error storing data: " + e.getMessage());
        }
        return newDir;
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
