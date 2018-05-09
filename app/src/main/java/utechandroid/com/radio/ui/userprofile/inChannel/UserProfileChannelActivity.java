package utechandroid.com.radio.ui.userprofile.inChannel;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Transaction;
import com.prudenttechno.radioNotification.R;


import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.ChannelData;
import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class UserProfileChannelActivity extends ColorsAppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    @BindView(R.id.imageView_user_profile_channel)
    ImageView imageViewUserProfileChannel;
    @BindView(R.id.txt_user_name_channel)
    TextView txtUserNameChannel;
    @BindView(R.id.txt_user_email_channel)
    TextView txtUserEmailChannel;
    @BindView(R.id.linearlayout_user_profile_channel)
    LinearLayout linearlayoutUserProfileChannel;
    @BindView(R.id.framelayout_user_profile_channel)
    FrameLayout framelayoutUserProfileChannel;
    @BindView(R.id.collapse_user_profile_channel)
    CollapsingToolbarLayout collapseUserProfileChannel;
    @BindView(R.id.appbar_user_profile_channel)
    AppBarLayout appbarUserProfileChannel;
    @BindView(R.id.textview_title_user_profile_channel)
    TextView textviewTitleUserProfileChannel;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.avatar_userProfile_channel)
    CircleImageView avatarUserProfileChannel;
    @BindView(R.id.switchButton_user_channel_write_permission)
    SwitchCompat switchButtonUserChannelWritePermission;

    private String channelId, userId;

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private ListenerRegistration mRegistration;
    private Boolean flag = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTransition();
        setContentView(R.layout.activity_user_profile_channel);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        getIntentValues();

        appbarUserProfileChannel.addOnOffsetChangedListener(this);

        startAlphaAnimation(textviewTitleUserProfileChannel, 0, View.INVISIBLE);

        CheckPermission();

        switchButtonUserChannelWritePermission.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                WritePermission();
            }
        });

    }

    private void SetTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition return_transition =
                    TransitionInflater.from(this).
                            inflateTransition(R.transition.channel_user_profile_exit);
            Transition enter_transition =
                    TransitionInflater.from(this).
                            inflateTransition(R.transition.channel_user_profile_enter);
            w.setReturnTransition(return_transition);
            w.setEnterTransition(enter_transition);
        }
    }

    private void CheckPermission() {
        DocumentReference documentReference = new FireStoreHelper()
                .GetChannelByIdDocument(channelId);
        mRegistration = documentReference.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                // Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot.exists()) {
                ChannelData channelData = snapshot.toObject(ChannelData.class);
                if (channelData.getMessageWritePermissions().entrySet().size() > 0) {
                    for (Map.Entry<String, Boolean> f : channelData.getMessageWritePermissions().entrySet()) {
                        String key = f.getKey();
                        if (flag) {
                            if (key.equals(userId)) {
                                Boolean value2 = f.getValue();
                                if (value2) {
                                    switchButtonUserChannelWritePermission.setChecked(true);
                                } else {
                                    switchButtonUserChannelWritePermission.setChecked(false);
                                }
                            }
                        }
                    }
                } else {
                    switchButtonUserChannelWritePermission.setChecked(false);
                }
                flag = false;
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
                startAlphaAnimation(textviewTitleUserProfileChannel, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitleUserProfileChannel, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
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

    private void getIntentValues() {
        channelId = getIntent().getExtras().getString("channelId");
        userId = getIntent().getExtras().getString("userId");

        LoadUserProfile();
    }

    private void LoadUserProfile() {
        DocumentReference docRef = new FireStoreHelper().GetUserByDocument(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    final User user = document.toObject(User.class);
                    textviewTitleUserProfileChannel.setText(user.getUserName());
                    txtUserNameChannel.setText(user.getUserName());
                    txtUserEmailChannel.setText(user.getUserEmail());

                    String url = user.getUserPhotoUrl();

                   /* if (membersData.getMemberJoinedDate() != null){
                        tvDateRequestChannelJoin.setText("Joined on "+ GetDateName(membersData.getMemberJoinedDate().toString()));
                    }*/

                    Glide.with(getApplicationContext())
                            .load(url)
                            .animate(R.anim.fade_in)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter()
                            .placeholder(R.drawable.profile_image)
                            .into(avatarUserProfileChannel);
                }
            }
        });
    }

    private void WritePermission() {
        if (!flag) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference sfDocRef = new FireStoreHelper()
                    .GetChannelByIdDocument(channelId);
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);
                ChannelData channelData = snapshot.toObject(ChannelData.class);
                if (channelData.getMessageWritePermissions().entrySet().size() > 0) {
                    for (Map.Entry<String, Boolean> f : channelData.getMessageWritePermissions().entrySet()) {
                        String key = f.getKey();
                        if (key.equals(userId)) {
                            Boolean value2 = f.getValue();
                            if (value2) {
                                transaction.update(sfDocRef, ChannelData.FIELD_COLLECTION_MEMBERS_WRITE_MESSAGE + "." + userId, false);
                            } else {
                                transaction.update(sfDocRef, ChannelData.FIELD_COLLECTION_MEMBERS_WRITE_MESSAGE + "." + userId, true);
                            }
                        }
                    }
                }

                return null;
            }).addOnSuccessListener(result -> {
                Toast.makeText(getApplicationContext(), "Successfully Updated...", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Error!While Updating Data...", Toast.LENGTH_SHORT).show();
            });
            flag = false;
        }
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

    @Override
    protected void onStop() {
        if (mRegistration != null) {
            mRegistration.remove();
        }
        super.onStop();
    }
}
