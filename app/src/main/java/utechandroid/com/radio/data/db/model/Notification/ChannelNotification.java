package utechandroid.com.radio.data.db.model.Notification;

import io.realm.RealmObject;

/**
 * Created by Utsav Shah on 30-Nov-17.
 */

public class ChannelNotification extends RealmObject{

    public ChannelNotification(){}

    public String getChannelId() {
        return channelId;
    }

    public ChannelNotification setChannelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public Boolean getNotification() {
        return Notification;
    }

    public void setNotification(Boolean notification) {
        Notification = notification;
    }

    public ChannelNotification(int id, String channelId, Boolean notification) {
        this.id = id;
        this.channelId = channelId;
        Notification = notification;
    }

    public int getId() {
        return id;
    }

    public ChannelNotification setId(int id) {
        this.id = id;
        return this;
    }

    private int id;
    private String channelId;
    private Boolean Notification;
}
