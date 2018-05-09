package utechandroid.com.radio.chatModels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Utsav Shah on 26-Oct-17.
 */

public class ChatMessage {

    private String id;
    private int type;
    private Date createdAt;
    private ChatUser user;

    public ChatImage getChatImage() {
        return chatImage;
    }

    public void setChatImage(ChatImage chatImage) {
        this.chatImage = chatImage;
    }

    public ChatText getChatText() {
        return chatText;
    }

    public void setChatText(ChatText chatText) {
        this.chatText = chatText;
    }

    public ChatLocation getChatLocation() {
        return chatLocation;
    }

    public void setChatLocation(ChatLocation chatLocation) {
        this.chatLocation = chatLocation;
    }

    public utechandroid.com.radio.chatModels.chatPdf getChatPdf() {
        return chatPdf;
    }

    public void setChatPdf(utechandroid.com.radio.chatModels.chatPdf chatPdf) {
        this.chatPdf = chatPdf;
    }

    private ChatImage chatImage;
    private ChatText chatText;
    private ChatLocation chatLocation;
    private chatPdf chatPdf;

    public ChatMessage() {
    }

    public ChatMessage setId(String id) {
        this.id = id;
        return this;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public ChatMessage setUser(ChatUser user) {
        this.user = user;
        return this;
    }

    public int getType() {
        return type;
    }

    public ChatMessage setType(int type) {
        this.type = type;
        return this;
    }

    public String getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public ChatUser getUser() {
        return this.user;
    }

    private String getGetDateOnly() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d hh:mm:ss z yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date dat1e = dateFormat.parse(createdAt.toString());
            return dateFormat2.format(dat1e);
        } catch (ParseException e) {
            return e.toString();
        }
    }

    public Date getDateOnly() {
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
        Date dat1e = null;
        try {
            dat1e = dateFormat2.parse(getGetDateOnly());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dat1e;
    }

    public String getGetDateName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d hh:mm:ss z yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM yyyy");
        try {
            Date dat1e = dateFormat.parse(createdAt.toString());
            return dateFormat2.format(dat1e);
        } catch (ParseException e) {
            return e.toString();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChatMessage)
            return (this.id.equals(((ChatMessage) obj).id));
        else
            return false;

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null == id ? 0 : id.hashCode());
        return hash;
    }
}
