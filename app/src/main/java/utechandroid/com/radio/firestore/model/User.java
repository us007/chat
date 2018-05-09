package utechandroid.com.radio.firestore.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utechandroid.com.radio.util.chatkit.commons.models.IUser;

/**
 * Created by Utsav Shah on 09-Oct-17.
 */

@IgnoreExtraProperties
public class User{

    public User() {
    }

    public static final String FIELD_COLLECTION = "users";
    public static final String FIELD_CREATED_COLLECTION = "createdChannels";
    public static final String FIELD_SUBSCRIBE_COLLECTION = "subscribeChannels";
    public static final String FIELD_USER_RADIO = "radios";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_USER_NAME = "userName";
    public static final String FIELD_USER_EMAIL = "userEmail";
    public static final String FIELD_USER_PHOTO_URL = "userPhotoUrl";
    public static final String FIELD_USER_CREATED_DATE = "userPhotoUrl";
    public static final String FIELD_USER_RADIOS = "radios";

    private String userId;
    private String userName;
    private String userEmail;
    private String userPhotoUrl;
    private @ServerTimestamp Date timestamp;

    public Map<String, Object> getRadios() {
        return radios;
    }

    public User setRadios(Map<String, Object> radios) {
        this.radios = radios;
        return this;
    }

    public Map<String, Integer> getSubscribeChannels() {
        return subscribeChannels;
    }

    public User setSubscribeChannels(Map<String, Integer> subscribeChannels) {
        this.subscribeChannels = subscribeChannels;
        return this;
    }

    private Map<String, Object> radios = new HashMap<>();

    public Map<String, Object> getCreatedChannels() {
        return createdChannels;
    }

    public User setCreatedChannels(Map<String, Object> createdChannels) {
        this.createdChannels = createdChannels;
        return this;
    }

    private Map<String, Object> createdChannels = new HashMap<>();
    private Map<String, Integer> subscribeChannels = new HashMap<>();

    public User(String userId, String userName, String userEmail, String userPhotoUrl,Date timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhotoUrl = userPhotoUrl;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public User setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public User setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public User setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
        return this;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
