package utechandroid.com.radio.util.GlidePalette;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;

/**
 * Created by Dharmik Patel on 01-Dec-17.
 */

public class PaletteBitmap {
    public final Palette palette;
    public final Bitmap bitmap;

    public PaletteBitmap(@NonNull Bitmap bitmap, @NonNull Palette palette) {
        this.bitmap = bitmap;
        this.palette = palette;
    }
    Bitmap getBitmap()
    {
        return bitmap;
    }
}
