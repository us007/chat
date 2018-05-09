package utechandroid.com.radio.data.api.model.Notification.request;

/**
 * Created by AFF41 on 12-Mar-18.
 */

public class NotificationGetDataRequest {

    public NotificationGetDataRequest(String title, String body, String imageUrl, String address, String expiryDate, String createdDate, String corpCode, String topic) {
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.address = address;

        this.expiryDate = expiryDate;
        this.createdDate = createdDate;
        this.corpCode = corpCode;
        this.topic = topic;
    }

    /**
     * title : string
     * body : string
     * imageUrl : string
     * address : string
     * priority : 0
     * expiryDate : string
     * createdDate : string
     * corpCode : string
     * topic : Radio007
     */

    private String title;
    private String body;
    private String imageUrl;
    private String address;
    private String expiryDate;
    private String createdDate;
    private String corpCode;
    private String topic;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
