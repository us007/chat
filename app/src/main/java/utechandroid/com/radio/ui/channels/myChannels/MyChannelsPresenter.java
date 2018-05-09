package utechandroid.com.radio.ui.channels.myChannels;

import utechandroid.com.radio.ui.base.Presenter;

/**
 * Created by Dharmik Patel on 16-Oct-17.
 */

public class MyChannelsPresenter implements Presenter<MyChannelsView> {

    private MyChannelsView myChannelsView;

    @Override
    public void onAttach(MyChannelsView view) {
        myChannelsView = view;
    }

    @Override
    public void onDetach() {
        myChannelsView = null;
    }

}
