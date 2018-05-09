package utechandroid.com.radio.events;

import utechandroid.com.radio.firestore.model.Radio;
import utechandroid.com.radio.firestore.model.RadioData;

/**
 * Created by Utsav Shah on 16-Oct-17.
 */

public class RadioEvent {

    public RadioData getmRadio() {
        return mRadio;
    }

    public RadioEvent setmRadio(RadioData mRadio) {
        this.mRadio = mRadio;
        return this;
    }

    public RadioEvent(RadioData mRadio) {
        this.mRadio = mRadio;
    }

    private RadioData mRadio;
}
