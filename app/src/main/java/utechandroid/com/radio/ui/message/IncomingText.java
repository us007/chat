package utechandroid.com.radio.ui.message;

import utechandroid.com.radio.chatModels.ChatMessage;

/**
 * Created by Dharmik Patel on 01-Nov-17.
 */

public class IncomingText extends ListItem {
    private ChatMessage pojoOfJsonArray;

    public ChatMessage getPojoOfJsonArray() {
        return pojoOfJsonArray;
    }

    public void setPojoOfJsonArray(ChatMessage pojoOfJsonArray) {
        this.pojoOfJsonArray = pojoOfJsonArray;
    }

    @Override
    public int getType() {
        return TYPE_TEXT_INCOMING;
    }

}
