package utechandroid.com.radio.ui.channels.myChannels;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;
import com.prudenttechno.radioNotification.R;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utechandroid.com.radio.adapter.MyChannelsAdapter;
import utechandroid.com.radio.events.RadioEvent;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.ChannelData;
import utechandroid.com.radio.firestore.model.RadioData;
import utechandroid.com.radio.ui.CreateChannel.CreateChannelFragment;
import utechandroid.com.radio.ui.channelDetails.ChannelDetailActivity;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;
import utechandroid.com.radio.util.ItemDecoration.PaddingItemDecoration;
import utechandroid.com.radio.util.fullScreenFragmentDialog.FullScreenDialogFragment;

public class MyChannelsActivity extends ColorsAppCompatActivity implements MyChannelsView, MyChannelsAdapter.OnMyChannelSelectedListener,
        FullScreenDialogFragment.OnDiscardListener, FullScreenDialogFragment.OnConfirmListener {

    @BindView(R.id.img_radio_my_channels)
    ImageView imgRadioMyChannels;
    @BindView(R.id.toolbar_my_radio_channels)
    Toolbar toolbarMyRadioChannels;
    @BindView(R.id.toolbar_layout_my_radio_channels)
    CollapsingToolbarLayout toolbarLayoutMyRadioChannels;
    @BindView(R.id.appbar_layout_my_radio_channels)
    AppBarLayout appbarLayoutMyRadioChannels;
    @BindView(R.id.recyclerView_my_radio_channels)
    RecyclerView recyclerViewMyRadioChannels;
    @BindView(R.id.pg_my_radio_channels)
    ProgressBar pgMyRadioChannels;
    @BindView(R.id.fab_my_channels_create)
    FloatingActionButton fabMyChannelsCreate;
    @BindView(R.id.lyt_my_radio_channels)
    LinearLayout lytMyRadioChannels;

    private MyChannelsPresenter myChannelsPresenter;
    private String mRadioId, mRadioCategory;
    private RadioData mRadio;
    private int mColor;
    private FullScreenDialogFragment dialogFragment;
    private MyChannelsAdapter myChannelsAdapter;
    private ListenerRegistration registration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SetTransition();

        setContentView(R.layout.activity_my_channels);
        ButterKnife.bind(this);

        setSupportActionBar(toolbarMyRadioChannels);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        InitPresenter();
        onAttach();

        SetUpRecyclerView();

        EventBus eventbus = EventBus.getDefault();
        RadioEvent radioEvent = eventbus.getStickyEvent(RadioEvent.class);
        if (radioEvent != null) {
            mRadio = radioEvent.getmRadio();
            final int color = mRadio.getRadioColor();
            mRadioId = mRadio.getRadioId();
            mRadioCategory = mRadio.getRadioCategoryId();
            String name = mRadio.getRadioName();
            String url = mRadio.getRadioPhotoUrl();

            LoadMyChannels();

            toolbarLayoutMyRadioChannels.setTitle(name);

            if (url.isEmpty()) {
                imgRadioMyChannels.setColorFilter(color);
                toolbarLayoutMyRadioChannels.setContentScrimColor(color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mColor = returnDarkerColor(color);
                    getWindow().setStatusBarColor(mColor);
                }
            } else {
                imgRadioMyChannels.setColorFilter(null);
                Glide.with(getApplicationContext())
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .crossFade()
                        .fitCenter()
                        .placeholder(R.drawable.placeholder)
                        .into(new ImageViewTarget<GlideDrawable>(imgRadioMyChannels) {
                            @Override
                            protected void setResource(GlideDrawable resource) {
                                setImage(resource);

                                if (!hasExtractedColorAlready()) {
                                    extractColor(resource);
                                }
                            }

                            private boolean hasExtractedColorAlready() {
                                return mRadio.getRadioColor() != 0;
                            }

                            private void setImage(GlideDrawable resource) {
                                imgRadioMyChannels.setImageDrawable(resource.getCurrent());
                            }

                            private void extractColor(GlideDrawable resource) {
                                Bitmap b = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
                                Palette p = Palette.from(b).generate();
                                int defaultColor = getResources().getColor(R.color.colorPrimary);
                                int colorn = p.getDarkVibrantColor(defaultColor);

                                toolbarLayoutMyRadioChannels.setContentScrimColor(colorn);

                                //   toolbarLayoutMyRadioChannels.setBackgroundResource(colorn);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    mColor = returnDarkerColor(color);
                                    getWindow().setStatusBarColor(mColor);
                                }
                                //radio.setColor(color);
                            }
                        });
            }
            eventbus.removeStickyEvent(radioEvent);
        }

        if (savedInstanceState != null) {
            dialogFragment =
                    (FullScreenDialogFragment) getSupportFragmentManager().findFragmentByTag("dialog");
            if (dialogFragment != null) {
                dialogFragment.setOnDiscardListener(this);
            }
        }

        if (savedInstanceState == null) {
            fabMyChannelsCreate.setScaleX(0);
            fabMyChannelsCreate.setScaleY(0);
            getWindow().getEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    getWindow().getEnterTransition().removeListener(this);
                    fabMyChannelsCreate.animate().scaleX(1).scaleY(1);
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
        }

    }

    private void SetUpRecyclerView() {
        recyclerViewMyRadioChannels.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMyRadioChannels.setLayoutManager(layoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.paddingItemDecorationDefault);
        // int padding = (int) getResources().getDimension(R.dimen.paddingItemDecorationDefault);
        //    int paddingEdge = (int) getResources().getDimension(R.dimen.paddingItemDecorationDefault);
        recyclerViewMyRadioChannels.addItemDecoration(new PaddingItemDecoration(spacingInPixels));

    }

    @Override
    public void onDiscard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(mColor);
        }
        // Toast.makeText(MainActivity.this, R.string.dialog_discarded, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void OnRadioEvent(RadioEvent radioEvent) {
        //supportStartPostponedEnterTransition();


        //imgRadioMyChannels.setImageResource(R.drawable.bg_circle);

    }

    public static int returnDarkerColor(int color) {
        float ratio = 1.0f - 0.2f;
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * ratio);
        int g = (int) (((color >> 8) & 0xFF) * ratio);
        int b = (int) ((color & 0xFF) * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private void SetTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            //  w.setAllowEnterTransitionOverlap(false);
            //     w.setAllowReturnTransitionOverlap(false);
            Transition return_transition =
                    TransitionInflater.from(this).
                            inflateTransition(R.transition.my_radio_channel_exit);
            Transition enter_transition =
                    TransitionInflater.from(this).
                            inflateTransition(R.transition.my_radio_channel);
            w.setReturnTransition(return_transition);
            w.setEnterTransition(enter_transition);
            w.getEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {

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
        }
    }

    private void InitPresenter() {
        myChannelsPresenter = new MyChannelsPresenter();
    }

    private void LoadMyChannels() {
        DocumentReference documentReference = new FireStoreHelper().GetRadioByIdDocument(mRadioId);
        registration = documentReference.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }

            String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                    ? "Local" : "Server";

            if (snapshot != null && snapshot.exists()) {
                RadioData user = snapshot.toObject(RadioData.class);
                List<String> stringList = new ArrayList<>(user.getChannels().keySet());
                myChannelsAdapter = new MyChannelsAdapter(stringList, this);
                recyclerViewMyRadioChannels.setAdapter(myChannelsAdapter);
            }

        });
    }

    @Override
    public void onAttach() {
        myChannelsPresenter.onAttach(this);
    }

    @Override
    public void onDetach() {
        myChannelsPresenter.onDetach();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        onDetach();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        if (registration != null) {
            registration.remove();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        fabMyChannelsCreate.animate().scaleX(0).scaleY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                supportFinishAfterTransition();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab_my_channels_create)
    public void onViewClicked() {

        final Bundle args = new Bundle();
        args.putString(CreateChannelFragment.RADIOCATEGORY, mRadioCategory);
        args.putString(CreateChannelFragment.RADIOID, mRadioId);

        EventBus.getDefault().postSticky(new RadioEvent(mRadio));

        dialogFragment = new FullScreenDialogFragment.Builder(this)
                .setTitle("Create Channel")
                .setExtraActions(R.menu.menu_fullscreen_dialog)
                .setOnDiscardListener(this)
                .setOnConfirmListener(this)
                .setContent(CreateChannelFragment.class, args)
                .build();

        dialogFragment.show(getSupportFragmentManager(), "dialog");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getThemeAccentColor(getApplicationContext()));
        }
    }

    private static int getThemeAccentColor(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorPrimary;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = context.getResources().getIdentifier("colorPrimary", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    @Override
    public void onConfirm(@Nullable Bundle result) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(mColor);
        }
    }

    @Override
    public void onChannelSelected(ChannelData channel, ImageView imageView) {
        Intent intent = new Intent(getApplicationContext(), ChannelDetailActivity.class);
        intent.putExtra("id", channel.getChannelId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }
}