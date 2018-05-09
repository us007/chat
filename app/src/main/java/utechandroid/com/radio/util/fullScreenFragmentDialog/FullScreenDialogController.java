package utechandroid.com.radio.util.fullScreenFragmentDialog;

/**
 * Created by Dharmik Patel on 13-Oct-17.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Interface to control the dialog
 */
public interface FullScreenDialogController {

    /**
     * Enable or disable the confirm button.
     *
     * @param enabled true to enable the button, false to disable it
     */
    void setConfirmButtonEnabled(boolean enabled);

    /**
     * Closes the dialog with a confirm action. {@link com.franmontiel.fullscreendialog.FullScreenDialogFragment.OnConfirmListener} will be called.
     *
     * @param result optional bundle with result data that will be passed to the
     *               {@link com.franmontiel.fullscreendialog.FullScreenDialogFragment.OnConfirmListener} callback
     */
    void confirm(@Nullable Bundle result);

    /**
     * Closes the dialog with a discard action. {@link com.franmontiel.fullscreendialog.FullScreenDialogFragment.OnDiscardListener} will be called.
     */
    void discard();

    /**
     * Closes de dialog from extra action. {@link com.franmontiel.fullscreendialog.FullScreenDialogFragment.OnDiscardFromExtraActionListener} will be called
     *
     * @param actionId menu item id to identify the action
     * @param result   optional bundle with result data that will be passed to the
     *                 {@link com.franmontiel.fullscreendialog.FullScreenDialogFragment.OnDiscardFromExtraActionListener} callback
     */
    void discardFromExtraAction(int actionId, @Nullable Bundle result);


}
