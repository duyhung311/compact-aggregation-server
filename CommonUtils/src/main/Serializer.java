package src.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import src.main.Request;
import src.main.WeatherData;

import java.lang.reflect.Type;
import java.net.http.HttpRequest;

import java.util.ArrayList;
import java.util.List;

public class Serializer {
    private static final Gson gson = new GsonBuilder().create();

    public static String toJSON(WeatherData obj) {
        return gson.toJson(obj);
    }

    public static String toJSON1(Request obj) {
        return gson.toJson(obj);
    }
    public static String toListJSON(List<WeatherData> obj) {
        return gson.toJson(obj);
    }

    public static List<WeatherData> fromJSON(String json) {
        Type listType = new TypeToken<ArrayList<WeatherData>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    public static WeatherData fromJSONToSingleEntity(String json) {
        Type listType = new TypeToken<WeatherData>(){}.getType();
        return gson.fromJson(json, listType);
    }

    public static Request parseRequest(String json) {
        return gson.fromJson(json, Request.class);
    }

    public static boolean parseJSON(String json) {
        Type listType = new TypeToken<ArrayList<WeatherData>>(){}.getType();
        try {
            gson.fromJson(json, listType);
            return true;
        } catch (JsonSyntaxException e) {
            return false;
        }
    }




}
