package utechandroid.com.radio.util.fullScreenFragmentDialog;

/**
 * Created by Dharmik Patel on 13-Oct-17.
 */

import android.view.MenuItem;

/**
 * Interface that the content {@link android.support.v4.app.Fragment} must implement. It allows to control over the container dialog.
 */
public interface FullScreenDialogContent {
    /**
     * Called after the dialog has been initialized. It is invoked before the content onCreateView.
     *
     * @param dialogController that allows to control the container dialog
     */
    void onDialogCreated(FullScreenDialogController dialogController);

    /**
     * Called when the confirm button is clicked.
     *
     * @param dialogController allows to control the container dialog
     * @return true if the event has been consumed, false otherwise
     */
    boolean onConfirmClick(FullScreenDialogController dialogController);

    /**
     * Called when the discard button is clicked.
     *
     * @param dialogController allows to control the container dialog
     * @return true if the event has been consumed, false otherwise
     */
    boolean onDiscardClick(FullScreenDialogController dialogController);

    /**
     * Called when a extra action button is clicked.
     * @param actionItem menu item id to identify the action
     * @param dialogController allows to control the container dialog
     * @return true if the event has been consumed, false otherwise
     */
    boolean onExtraActionClick(MenuItem actionItem, FullScreenDialogController dialogController);
}
