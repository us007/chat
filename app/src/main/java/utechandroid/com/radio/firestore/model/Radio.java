package utechandroid.com.radio.firestore.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Utsav Shah on 09-Oct-17.
 */

@IgnoreExtraProperties
public class Radio {

    public Radio() {
    }

    public static final String FIELD_COLLECTION = "radios";

    public static final String FIELD_RADIO_ID = "radioId";
    public static final String FIELD_RADIO_NAME = "radioName";
    public static final String FIELD_RADIO_DESCRIPTION = "radioDescription";
    public static final String FIELD_RADIO_CATEGORY = "radioCategoryId";
    public static final String FIELD_RADIO_USER_ID = "radioUserId";
    public static final String FIELD_RADIO_PHOTO_URL = "radioPhotoUrl";
    public static final String FIELD_RADIO_COLOR = "radioColor";
    public static final String FIELD_RADIO_CREATED_DATE = "radioCreatedDate";

    private String radioId = "";

    public Map<String, Object> getChannels() {
        return channels;
    }

    public Radio setChannels(Map<String, Object> channels) {
        this.channels = channels;
        return this;
    }

    private Map<String, Object> channels = new HashMap<>();


    public String getRadioId() {
        return radioId;
    }

    public Radio setRadioId(String radioId) {
        this.radioId = radioId;
        return this;
    }

    public String getRadioName() {
        return radioName;
    }

    public Radio setRadioName(String radioName) {
        this.radioName = radioName;
        return this;
    }

    public String getRadioDescription() {
        return radioDescription;
    }

    public Radio setRadioDescription(String radioDescription) {
        this.radioDescription = radioDescription;
        return this;
    }

    public String getRadioCategoryId() {
        return radioCategoryId;
    }

    public Radio setRadioCategoryId(String radioCategoryId) {
        this.radioCategoryId = radioCategoryId;
        return this;
    }

    public String getRadioUserId() {
        return radioUserId;
    }

    public Radio setRadioUserId(String radioUserId) {
        this.radioUserId = radioUserId;
        return this;
    }

    public String getRadioPhotoUrl() {
        return radioPhotoUrl;
    }

    public Radio setRadioPhotoUrl(String radioPhotoUrl) {
        this.radioPhotoUrl = radioPhotoUrl;
        return this;
    }

    public int getRadioColor() {
        return radioColor;
    }

    public Radio setRadioColor(int radioColor) {
        this.radioColor = radioColor;
        return this;
    }

    public Date getRadioCreatedDate() {
        return radioCreatedDate;
    }

    public Radio setRadioCreatedDate(Date radioCreatedDate) {
        this.radioCreatedDate = radioCreatedDate;
        return this;
    }

    private String radioName = "";
    private String radioDescription = "";
    private String radioCategoryId = "";
    private String radioUserId = "";
    private String radioPhotoUrl = "";
    private int radioColor = 0;
    private @ServerTimestamp
    Date radioCreatedDate;


}
