package utechandroid.com.radio.firestore.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Utsav Shah on 23-Nov-17.
 */

@IgnoreExtraProperties
public class ChannelData {

    public ChannelData() {
    }

    public static final String FIELD_COLLECTION = "channels";
    public static final String FIELD_COLLECTION_MEMBERS = "members";
    public static final String FIELD_COLLECTION_MEMBERS_WRITE_MESSAGE = "messageWritePermissions";
    public static final String FIELD_CHANNEL_ID = "channelId";
    public static final String FIELD_CHANNEL_NAME = "channelName";
    public static final String FIELD_CHANNEL_DESCRIPTION = "channelDescription";
    public static final String FIELD_CHANNEL_PHOTO_URL = "channelPhotoUrl";
    public static final String FIELD_CHANNEL_CATEGORY_ID = "channelSubCategoryId";
    public static final String FIELD_CHANNEL_RADIO_ID = "channelRadioId";
    public static final String FIELD_CHANNEL_USER_ID = "channelAdminId";
    public static final String FIELD_CHANNEL_PRIVATE = "channelPrivate";
    public static final String FIELD_CHANNEL_COLOR = "channelColor";
    public static final String FIELD_CHANNEL_CREATED_DATE = "channelCreatedDate";

    public String getChannelId() {
        return channelId;
    }

    public ChannelData setChannelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public void setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
    }

    public String getChannelPhotoUrl() {
        return channelPhotoUrl;
    }

    public ChannelData setChannelPhotoUrl(String channelPhotoUrl) {
        this.channelPhotoUrl = channelPhotoUrl;
        return this;
    }

    public Boolean getChannelPrivate() {
        return channelPrivate;
    }

    public void setChannelPrivate(Boolean channelPrivate) {
        this.channelPrivate = channelPrivate;
    }

    public String getChannelAdminId() {
        return channelAdminId;
    }

    public void setChannelAdminId(String channelAdminId) {
        this.channelAdminId = channelAdminId;
    }

    public String getChannelSubCategoryId() {
        return channelSubCategoryId;
    }

    public void setChannelSubCategoryId(String channelSubCategoryId) {
        this.channelSubCategoryId = channelSubCategoryId;
    }

    public int getChannelColor() {
        return channelColor;
    }

    public void setChannelColor(int channelColor) {
        this.channelColor = channelColor;
    }

    public String getChannelRadioId() {
        return channelRadioId;
    }

    public void setChannelRadioId(String channelRadioId) {
        this.channelRadioId = channelRadioId;
    }

    public Date getChannelCreatedDate() {
        return channelCreatedDate;
    }

    public void setChannelCreatedDate(Date channelCreatedDate) {
        this.channelCreatedDate = channelCreatedDate;
    }

    private String channelId = "";
    private String channelName = "";
    private String channelDescription = "";
    private String channelPhotoUrl = "";
    private Boolean channelPrivate = false;
    private String channelAdminId = "";
    private String channelSubCategoryId = "";
    private int channelColor = 0;
    private String channelRadioId;
    private @ServerTimestamp
    Date channelCreatedDate;

    public Map<String, Integer> getMembers() {
        return members;
    }

    public ChannelData setMembers(Map<String, Integer> members) {
        this.members = members;
        return this;
    }

    private Map<String, Integer> members = new HashMap<>();

    public Map<String, Boolean> getMessageWritePermissions() {
        return messageWritePermissions;
    }

    public ChannelData setMessageWritePermissions(Map<String, Boolean> messageWritePermissions) {
        this.messageWritePermissions = messageWritePermissions;
        return this;
    }

    private Map<String, Boolean> messageWritePermissions = new HashMap<>();
}
