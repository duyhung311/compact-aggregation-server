package src;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;

public class Serializer {
    private static final Gson gson = new GsonBuilder().create();

    public static String toJSON(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJSON(String json, Type clazz) {
        return gson.fromJson(json, clazz);
    }
}
