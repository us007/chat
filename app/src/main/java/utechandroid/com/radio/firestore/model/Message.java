package utechandroid.com.radio.firestore.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Utsav Shah on 18-Oct-17.
 */

@IgnoreExtraProperties
public class Message {
    public Message() {
    }

    /*  Message Type
  *  1 = text
  *  2 = image
  *  3 = pdf
  *  5 = location
  * */

    public static final String FIELD_COLLECTION = "messages";

    public static final String FIELD_MESSAGE_ID = "messageId";
    public static final String FIELD_MESSAGE_SENDER = "messageSender";
    public static final String FIELD_MESSAGE_TYPE = "messageType";
    public static final String FIELD_MESSAGE_CHANNEL = "messageChannel";
    public static final String FIELD_MESSAGE_DATE = "messageDate";
    public static final String FIELD_MESSAGE_DATA = "data";

    public static final String FIELD_MESSAGE_FILE_NAME = "filename";
    public static final String FIELD_MESSAGE_DATA_MESSAGE = "text";
    public static final String FIELD_MESSAGE_DATA_MESSAGE_IMAGE_URL = "StorageUrl";
    public static final String FIELD_MESSAGE_DATA_MESSAGE_LOCAL_URL = "localUrl";
    public static final String FIELD_MESSAGE_DATA_MESSAGE_SIZE = "size";
    public static final String FIELD_MESSAGE_DATA_MESSAGE_DURATION = "duration";
    public static final String FIELD_MESSAGE_DATA_MESSAGE_LATITUDE = "latitude";
    public static final String FIELD_MESSAGE_DATA_MESSAGE_LONGITUDE = "longitude";
    public static final String FIELD_MESSAGE_DATA_MESSAGE_PLACE = "place";

    private @ServerTimestamp
    Date messageDate;
    private String messageId;
    private String messageSender;
    private String messageChannel;
    private int messageType;
    private String messageSenderName;

    public Map<String, Object> getData() {
        return data;
    }

    public Message setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    private Map<String, Object> data = new HashMap<>();

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(String messageSender) {
        this.messageSender = messageSender;
    }

    public String getMessageChannel() {
        return messageChannel;
    }

    public void setMessageChannel(String messageChannel) {
        this.messageChannel = messageChannel;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageSenderName() {
        return messageSenderName;
    }

    public void setMessageSenderName(String messageSenderName) {
        this.messageSenderName = messageSenderName;
    }

}
