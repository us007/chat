package utechandroid.com.radio.ui.message;

/**
 * Created by Dharmik Patel on 01-Nov-17.
 */

public abstract class ListItem {

    public static final int TYPE_DATE = 1;
    public static final int TYPE_TEXT_INCOMING = 2;
    public static final int TYPE_TEXT_OUTCOMING = 3;
    public static final int TYPE_IMAGE_INCOMING = 4;
    public static final int TYPE_IMAGE_OUTCOMING = 5;

    abstract public int getType();
}
