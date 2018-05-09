package utechandroid.com.radio.ui.home;

import utechandroid.com.radio.ui.base.Presenter;

/**
 * Created by Dharmik Patel on 13-Oct-17.
 */

public class HomePresenter implements Presenter<HomeView> {

    private HomeView homeView;

    @Override
    public void onAttach(HomeView view) {
        homeView = view;
    }

    @Override
    public void onDetach() {
        homeView = null;
    }

}
