package utechandroid.com.radio.chatModels;

import utechandroid.com.radio.util.chatkit.commons.models.IUser;

/**
 * Created by Utsav Shah on 26-Oct-17.
 */

public class ChatUser{
    private String id;
    private String name;

    public ChatUser(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
