package utechandroid.com.radio.util.ColorPicker;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.view.Window;

public class CustomDialog extends BottomSheetDialog {
    private View view;

    public CustomDialog(Context context, View layout) {
        super(context);
        view = layout;
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(view);
        super.onCreate(savedInstanceState);
    }
}
