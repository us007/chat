package utechandroid.com.radio.firestore.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Utsav Shah on 27-Nov-17.
 */
@IgnoreExtraProperties
public class MessageReadData {

    /*  Message Status
   *  1 = sent
   *  2 = unread
   *  3 = read
   * */

    public MessageReadData(){}

    public static final String FIELD_COLLECTION = "messagesReads";

    public Message getMessage() {
        return message;
    }

    public MessageReadData setMessage(Message message) {
        this.message = message;
        return this;
    }

    public Map<String, Integer> getMembers() {
        return members;
    }

    public MessageReadData setMembers(Map<String, Integer> members) {
        this.members = members;
        return this;
    }

    private Message message;
    private Map<String, Integer> members = new HashMap<>();

}
