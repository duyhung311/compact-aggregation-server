package src;

public class LamportClockElement {
    WeatherData weatherData;
    LamportClock lamportClock;

    public WeatherData weatherData() {
        return weatherData;
    }

    public LamportClockElement setWeatherData(WeatherData weatherData) {
        this.weatherData = weatherData;
        return this;
    }

    public LamportClock lamportClock() {
        return lamportClock;
    }

    public LamportClockElement setLamportClock(LamportClock lamportClock) {
        this.lamportClock = lamportClock;
        return this;
    }
}
