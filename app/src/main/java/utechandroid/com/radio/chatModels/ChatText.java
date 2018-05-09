package utechandroid.com.radio.chatModels;

/**
 * Created by Utsav Shah on 14-Dec-17.
 */

public class ChatText {

    public ChatText(String text) {
        this.text = text;
    }

    public ChatText() {
    }

    public String getText() {
        return text;
    }

    public ChatText setText(String text) {
        this.text = text;
        return this;
    }

    private String text;
}
