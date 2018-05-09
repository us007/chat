package utechandroid.com.radio.ui.messageStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dharmik Patel on 28-Nov-17.
 */

public class MessageStatus {

    private String headerTitle;
    private List<String> allItemsInSection;


    public MessageStatus() {

    }
    public MessageStatus(String headerTitle, List<String> allItemsInSection) {
        this.headerTitle = headerTitle;
        this.allItemsInSection = allItemsInSection;
    }



    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public List<String> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(List<String> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }

}
