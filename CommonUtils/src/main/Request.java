package src.main;

import java.util.Collections;
import java.util.List;

public class Request {
    private String method;
    private int contentLength;
    private String contentType;
    private String data;
    private String userAgent;
    private String accept;
    private Integer lamportClockValue;

    public String method() {
        return method;
    }

    public Request setMethod(String method) {
        this.method = method;
        return this;
    }

    public int contentLength() {
        return contentLength;
    }

    public Request setContentLength(int contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    public String contentType() {
        return contentType;
    }

    public Request setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String data() {
        return data;
    }

    public Request setData(String data) {
        this.data = data;
        return this;
    }

    public String userAgent() {
        return userAgent;
    }

    public Request setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public String accept() {
        return accept;
    }

    public Request setAccept(String accept) {
        this.accept = accept;
        return this;
    }

    public Integer lamportClockValue() {
        return lamportClockValue;
    }

    public Request setLamportClockValue(int lamportClockValue) {
        this.lamportClockValue = lamportClockValue;
        return this;
    }

    public static Request buildPutRequestWithData(WeatherData data) {
        return new Request()
                .setData(Serializer.toJSON(data))
                .setMethod("PUT")
                .setContentLength(data.toString().length())
                .setContentType("application/json")
                .setAccept("application/json")
                .setUserAgent("ATOMClient/1/0");
    }

    public static Request buildGetRequestWithData(WeatherData data) {
        return new Request()
                .setMethod("GET")
                .setContentLength(data.toString().length())
                .setAccept("application/json")
                .setUserAgent("ATOMClient/1/0");
    }


}
