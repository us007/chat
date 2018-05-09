package utechandroid.com.radio.ui.base;

/**
 * Created by Dharmik Patel on 13-Oct-17.
 */

public interface Presenter<T extends View> {
    void onAttach(T view);

    void onDetach();
}
