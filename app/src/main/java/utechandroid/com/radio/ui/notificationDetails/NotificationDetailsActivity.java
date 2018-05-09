package utechandroid.com.radio.ui.notificationDetails;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.prudenttechno.radioNotification.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import utechandroid.com.radio.data.api.model.Notification.response.NotificationGetDataResponse;

public class NotificationDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    //
//    @BindView(R.id.iv_details)
//    ImageView ivDetails;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_body)
    TextView tvBody;
    NotificationGetDataResponse.Table1 table11;
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    @BindView(R.id.img_myChannel)
    ImageView imgMyChannel;
    //    @BindView(R.id.tabs_channel_detail)
//    TabLayout tabsChannelDetail;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.appbar_channel_detail)
    AppBarLayout appbarChannelDetail;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTransition();
        setContentView(R.layout.activity_notification_details);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appbarChannelDetail.addOnOffsetChangedListener(this);
        //startAlphaAnimation(textviewTitleDetailChannel, 0, View.INVISIBLE);
        if (getIntent() == null) {
            Toast.makeText(this, "no data", Toast.LENGTH_SHORT).show();
        } else {
            table11 = getIntent().getParcelableExtra("notification_data");
            tvTitle.setText(table11.getTitle());
tvAddress.setText(table11.getAddress());
            tvBody.setText(table11.getBody());
            if (table11.getImageUrl() != null) {
                Glide.with(this)
                        .load(table11.getImageUrl())
                        .animate(R.anim.fade_in)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                        .placeholder(R.drawable.placeholder)
                        .into(imgMyChannel);
            } else {
                imgMyChannel.setVisibility(View.GONE);
            }


        }
    }

    private void SetTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            w.setReturnTransition(new Explode());
            w.setEnterTransition(new Explode());
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                //  startAlphaAnimation(textviewTitleDetailChannel, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                //   startAlphaAnimation(textviewTitleDetailChannel, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                // startAlphaAnimation(linearlayoutUserProfileChannel, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                //   startAlphaAnimation(linearlayoutUserProfileChannel, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

}
