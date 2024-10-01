
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataManipulator {
    private static final String defaultDir = "./data/";

    public DataManipulator() {}

    /**
     * Put data in the following dir structure: /uuid/timestamp[.txt]
     * @param req Content of what to be saved
     * @param uuid data's parent directory
     * @param timestamp data's file name
     * @return whether uuid directory was created previously
     */
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

}
