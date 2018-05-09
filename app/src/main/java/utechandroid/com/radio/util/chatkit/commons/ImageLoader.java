package utechandroid.com.radio.util.chatkit.commons;

import android.widget.ImageView;

/**
 * Callback for implementing images loading in message list
 */
public interface ImageLoader {

    void loadImage(ImageView imageView, String url);

}
