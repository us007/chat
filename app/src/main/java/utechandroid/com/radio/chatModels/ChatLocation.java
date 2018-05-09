package utechandroid.com.radio.chatModels;

/**
 * Created by Utsav Shah on 14-Dec-17.
 */

public class ChatLocation {

    private String place;

    public String getPlace() {
        return place;
    }

    public ChatLocation setPlace(String place) {
        this.place = place;
        return this;
    }

    public ChatLocation() {
    }

    public ChatLocation(String place, double latitude, double longitude) {
        this.place = place;
        this.latitude = latitude;

        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public ChatLocation setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public ChatLocation setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    private double latitude;
    private double longitude;
}
