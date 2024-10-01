

public class ResponseParser {
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    private static final String BAD_REQUEST = "Bad Request";
    private static final String OK = "OK";
    private static final String CREATED = "CREATED";

    public static String messageParser(String message) {
        if (message.contains("400")) {
            return BAD_REQUEST;
        }
        if (message.contains("200")) {
            return OK;
        }
        if (message.contains("201")) {
            return CREATED;
        }
        return INTERNAL_SERVER_ERROR;
    }
}
