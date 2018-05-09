package utechandroid.com.radio.util.ItemDecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Dharmik Patel on 16-Oct-17.
 */

public class PaddingItemDecoration extends RecyclerView.ItemDecoration {

    private boolean verticalOrientation = true;
    private int space;

    public PaddingItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if(parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space;
        }
    }
}
