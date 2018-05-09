package utechandroid.com.radio.ui.joinChannel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.prudenttechno.radioNotification.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class JoinChannelActivity extends ColorsAppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_channel_join_name)
    TextView txtChannelJoinName;
    @BindView(R.id.btn_channel_join)
    Button btnChannelJoin;
    @BindView(R.id.txt_channel_join_category)
    TextView txtChannelJoinCategory;
    @BindView(R.id.lyt_channel_join_details)
    LinearLayout lytChannelJoinDetails;
    @BindView(R.id.progressBar_channel_join)
    ProgressBar progressBarChannelJoin;

    private FirebaseFirestore db;
    private String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_channel);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();

        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if (appLinkData != null) {
            id = appLinkData.getQueryParameter("id");
            //LoadChannel(id);
        }

    }


}
