package utechandroid.com.radio.ui.message;

import java.util.Date;

/**
 * Created by Dharmik Patel on 01-Nov-17.
 */

public class DateItem extends ListItem {

    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return TYPE_DATE;
    }
}