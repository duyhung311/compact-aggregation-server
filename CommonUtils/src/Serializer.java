package src;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;

public class Serializer {
    private static final Gson gson = new GsonBuilder().create();

    public static String toJSON(WeatherData obj) {
        return gson.toJson(obj);
    }

    public static WeatherData fromJSON(String json) {
        return gson.fromJson(json, WeatherData.class);
    }



}
