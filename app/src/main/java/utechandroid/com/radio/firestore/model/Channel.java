package utechandroid.com.radio.firestore.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Utsav Shah on 12-Oct-17.
 */
@IgnoreExtraProperties
public class Channel {

    public Channel() {
    }

    public static final String FIELD_COLLECTION = "channels";
    public static final String FIELD_COLLECTION_SUBSCRIBE = "subscribeChannels";
    public static final String FIELD_CHANNEL_ID = "channelId";
    public static final String FIELD_CHANNEL_NAME = "channelName";
    public static final String FIELD_CHANNEL_DESCRIPTION = "channelDescription";
    public static final String FIELD_CHANNEL_PHOTO_URL = "channelPhotoUrl";
    public static final String FIELD_CHANNEL_CATEGORY_ID = "channelCategoryId";
    public static final String FIELD_CHANNEL_RADIO_ID = "channelRadioId";
    public static final String FIELD_CHANNEL_USER_ID = "channelUserId";
    public static final String FIELD_CHANNEL_PRIVATE = "channelPrivate";
    public static final String FIELD_CHANNEL_COLOR = "channelColor";
    public static final String FIELD_CHANNEL_CREATED_DATE = "channelCreatedDate";

    public Date getChannelCreatedDate() {
        return channelCreatedDate;
    }

    public Channel setChannelCreatedDate(Date channelCreatedDate) {
        this.channelCreatedDate = channelCreatedDate;
        return this;
    }

    public String getChannelRadioId() {
        return channelRadioId;
    }

    public Channel setChannelRadioId(String channelRadioId) {
        this.channelRadioId = channelRadioId;
        return this;
    }

    private String channelRadioId;

    private @ServerTimestamp
    Date channelCreatedDate;

    public String getChannelId() {
        return channelId;
    }

    public Channel setChannelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public String getChannelName() {
        return channelName;
    }

    public Channel setChannelName(String channelName) {
        this.channelName = channelName;
        return this;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public Channel setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
        return this;
    }

    public String getChannelPhotoUrl() {
        return channelPhotoUrl;
    }

    public Channel setChannelPhotoUrl(String channelPhotoUrl) {
        this.channelPhotoUrl = channelPhotoUrl;
        return this;
    }

    public String getChannelUserId() {
        return channelUserId;
    }

    public Channel setChannelUserId(String channelUserId) {
        this.channelUserId = channelUserId;
        return this;
    }

    public String getChannelCategoryId() {
        return channelCategoryId;
    }

    public Channel setChannelCategoryId(String channelCategoryId) {
        this.channelCategoryId = channelCategoryId;
        return this;
    }

    public int getChannelColor() {
        return channelColor;
    }

    public Channel setChannelColor(int channelColor) {
        this.channelColor = channelColor;
        return this;
    }

    private String channelId = "";
    private String channelName = "";
    private String channelDescription = "";
    private String channelPhotoUrl = "";

    public Boolean getChannelPrivate() {
        return channelPrivate;
    }

    public Channel setChannelPrivate(Boolean channelPrivate) {
        this.channelPrivate = channelPrivate;
        return this;
    }

    private Boolean channelPrivate = false;
    private String channelUserId = "";
    private String channelCategoryId = "";
    private int channelColor = 0;

}
