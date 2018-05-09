package utechandroid.com.radio.events;

import utechandroid.com.radio.firestore.model.Channel;
import utechandroid.com.radio.firestore.model.ChannelData;

/**
 * Created by Utsav Shah on 17-Oct-17.
 */

public class ChannelEvent {

    public ChannelData getmChannel() {
        return mChannel;
    }

    public ChannelEvent setmChannel(ChannelData mChannel) {
        this.mChannel = mChannel;
        return this;
    }

    public ChannelEvent(ChannelData mChannel) {
        this.mChannel = mChannel;
    }

    private ChannelData mChannel;
}
