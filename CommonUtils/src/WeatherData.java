package src;


public class WeatherData {
    String id;
    String name;
    String state;
    String timeZone;
    float lat;
    float lon;
    String localDateTime;
    String localDateTimeFull;
    float airTemp;
    float apparentTemperature;
    String cloud;
    float dewPoint;
    float press;
    float relHum;
    String windDir;
    float windSpdKmh;
    float windSpdKt;

    public String id() {
        return id;
    }

    public WeatherData setId(String id) {
        this.id = id;
        return this;
    }

    public String name() {
        return name;
    }

    public WeatherData setName(String name) {
        this.name = name;
        return this;
    }

    public String state() {
        return state;
    }

    public WeatherData setState(String state) {
        this.state = state;
        return this;
    }

    public String timeZone() {
        return timeZone;
    }

    public WeatherData setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public float lat() {
        return lat;
    }

    public WeatherData setLat(float lat) {
        this.lat = lat;
        return this;
    }

    public float lon() {
        return lon;
    }

    public WeatherData setLon(float lon) {
        this.lon = lon;
        return this;
    }

    public String localDateTime() {
        return localDateTime;
    }

    public WeatherData setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
        return this;
    }

    public String localDateTimeFull() {
        return localDateTimeFull;
    }

    public WeatherData setLocalDateTimeFull(String localDateTimeFull) {
        this.localDateTimeFull = localDateTimeFull;
        return this;
    }

    public float airTemp() {
        return airTemp;
    }

    public WeatherData setAirTemp(float airTemp) {
        this.airTemp = airTemp;
        return this;
    }

    public float apparentTemperature() {
        return apparentTemperature;
    }

    public WeatherData setApparentTemperature(float apparentTemperature) {
        this.apparentTemperature = apparentTemperature;
        return this;
    }

    public String cloud() {
        return cloud;
    }

    public WeatherData setCloud(String cloud) {
        this.cloud = cloud;
        return this;
    }

    public float dewPoint() {
        return dewPoint;
    }

    public WeatherData setDewPoint(float dewPoint) {
        this.dewPoint = dewPoint;
        return this;
    }

    public float press() {
        return press;
    }

    public WeatherData setPress(float press) {
        this.press = press;
        return this;
    }

    public float relHum() {
        return relHum;
    }

    public WeatherData setRelHum(float relHum) {
        this.relHum = relHum;
        return this;
    }

    public String windDir() {
        return windDir;
    }

    public WeatherData setWindDir(String windDir) {
        this.windDir = windDir;
        return this;
    }

    public float windSpdKmh() {
        return windSpdKmh;
    }

    public WeatherData setWindSpdKmh(float windSpdKmh) {
        this.windSpdKmh = windSpdKmh;
        return this;
    }

    public float windSpdKt() {
        return windSpdKt;
    }

    public WeatherData setWindSpdKt(float windSpdKt) {
        this.windSpdKt = windSpdKt;
        return this;
    }
}
