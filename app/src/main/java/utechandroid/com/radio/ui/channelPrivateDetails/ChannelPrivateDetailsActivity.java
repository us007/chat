package utechandroid.com.radio.ui.channelPrivateDetails;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.prudenttechno.radioNotification.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import utechandroid.com.radio.adapter.ChannelPrivateTabAdapter;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class ChannelPrivateDetailsActivity extends ColorsAppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs_channel_private)
    TabLayout tabsChannelPrivate;
    @BindView(R.id.viewPager_channel_private)
    ViewPager viewPagerChannelPrivate;

    private ChannelPrivateTabAdapter channelPrivateTabAdapter;
    private String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_private_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        id = getIntent().getExtras().getString("id");

        channelPrivateTabAdapter = new ChannelPrivateTabAdapter(getSupportFragmentManager(), id);
        viewPagerChannelPrivate.setAdapter(channelPrivateTabAdapter);
        tabsChannelPrivate.setupWithViewPager(viewPagerChannelPrivate);


    }

}
