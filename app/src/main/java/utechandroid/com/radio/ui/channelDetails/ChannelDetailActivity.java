package utechandroid.com.radio.ui.channelDetails;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;
import com.prudenttechno.radioNotification.R;


import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utechandroid.com.radio.adapter.ChannelPrivateTabAdapter;
import utechandroid.com.radio.events.ChannelEvent;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.ChannelData;
import utechandroid.com.radio.ui.channelEdit.ChannelEditActivity;
import utechandroid.com.radio.ui.message.MessageActivity;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class ChannelDetailActivity extends ColorsAppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    @BindView(R.id.img_myChannel)
    ImageView imgMyChannel;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs_channel_detail)
    TabLayout tabsChannelDetail;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.viewPager_channel_detail)
    ViewPager viewPagerChannelDetail;
    @BindView(R.id.fab_message)
    FloatingActionButton fabMessage;
    @BindView(R.id.textview_title_detail_channel)
    TextView textviewTitleDetailChannel;
    @BindView(R.id.appbar_channel_detail)
    AppBarLayout appbarChannelDetail;

    private String channelId;
    private ListenerRegistration channelListener;

    ChannelData channelData;
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTransition();
        setContentView(R.layout.activity_channel_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        fabMessage.setScaleX(0);
        fabMessage.setScaleY(0);
        getWindow().getEnterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                getWindow().getEnterTransition().removeListener(this);
                fabMessage.animate().scaleX(1).scaleY(1);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        appbarChannelDetail.addOnOffsetChangedListener(this);
        startAlphaAnimation(textviewTitleDetailChannel, 0, View.INVISIBLE);

        if (getIntent() != null) {
            channelId = getIntent().getExtras().getString("id");

            DocumentReference docRef = new FireStoreHelper().GetChannelByIdDocument(channelId);
            channelListener = docRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    channelData = snapshot.toObject(ChannelData.class);
                    toolbarLayout.setTitle("");
                    String url = channelData.getChannelPhotoUrl();
                    int color = channelData.getChannelColor();
                    textviewTitleDetailChannel.setText(channelData.getChannelName());
                    LoadCollapseImageView(url, color);
                }
            });

            ChannelPrivateTabAdapter channelPrivateTabAdapter = new ChannelPrivateTabAdapter(getSupportFragmentManager(), channelId);
            viewPagerChannelDetail.setAdapter(channelPrivateTabAdapter);
            tabsChannelDetail.setupWithViewPager(viewPagerChannelDetail);
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
    public void onBackPressed() {
        fabMessage.animate().scaleX(0).scaleY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                supportFinishAfterTransition();
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitleDetailChannel, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitleDetailChannel, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_channel_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_channel_edit:
                Intent intent = new Intent(getApplicationContext(), ChannelEditActivity.class);
                intent.putExtra("channelId", channelId);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                break;
            case R.id.action_channel_share:
                String shareBody = "http://radiobroadcast.ooo/channelJoin?id=" + channelId;
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Please, Join Channel through below...");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share"));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public static int returnDarkerColor(int color) {
        float ratio = 1.0f - 0.2f;
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * ratio);
        int g = (int) (((color >> 8) & 0xFF) * ratio);
        int b = (int) ((color & 0xFF) * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private void LoadCollapseImageView(String url, int color) {
        if (url.isEmpty()) {
            imgMyChannel.setColorFilter(color);
            toolbarLayout.setContentScrimColor(color);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int mColor = returnDarkerColor(color);
                getWindow().setStatusBarColor(mColor);
            }
        } else {
            imgMyChannel.setColorFilter(null);
            Glide.with(getApplicationContext())
                    .load(url)
                    .animate(R.anim.fade_in)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .placeholder(R.drawable.placeholder)
                    .into(imgMyChannel);
        }
    }

    @Override
    protected void onStop() {
        if (channelListener != null) {
            channelListener.remove();
        }
        super.onStop();
    }

    private void SendMessage() {
        EventBus.getDefault().postSticky(new ChannelEvent(channelData));
        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @OnClick(R.id.fab_message)
    public void onViewClicked() {
        SendMessage();
    }
}
