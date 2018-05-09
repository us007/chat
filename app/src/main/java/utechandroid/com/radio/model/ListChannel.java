package utechandroid.com.radio.model;

/**
 * Created by Utsav Shah on 24-Oct-17.
 */

public class ListChannel {

    private String channelId;
    private String channelName;

    public String getChannelId() {
        return channelId;
    }

    public ListChannel setChannelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public String getChannelName() {
        return channelName;
    }

    public ListChannel setChannelName(String channelName) {
        this.channelName = channelName;
        return this;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public ListChannel setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
        return this;
    }

    public String getChannelPhotoUrl() {
        return channelPhotoUrl;
    }

    public ListChannel setChannelPhotoUrl(String channelPhotoUrl) {
        this.channelPhotoUrl = channelPhotoUrl;
        return this;
    }

    public String getChannelCategoryId() {
        return channelCategoryId;
    }

    public ListChannel setChannelCategoryId(String channelCategoryId) {
        this.channelCategoryId = channelCategoryId;
        return this;
    }

    public int getChannelColor() {
        return channelColor;
    }

    public ListChannel setChannelColor(int channelColor) {
        this.channelColor = channelColor;
        return this;
    }

    public String getChannelUserId() {
        return channelUserId;
    }

    public ListChannel setChannelUserId(String channelUserId) {
        this.channelUserId = channelUserId;
        return this;
    }

    private String channelDescription;
    private String channelPhotoUrl;
    private String channelCategoryId;
    private int channelColor;
    private String channelUserId;
}
