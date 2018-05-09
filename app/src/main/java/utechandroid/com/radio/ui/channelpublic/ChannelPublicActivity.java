package utechandroid.com.radio.ui.channelpublic;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.prudenttechno.radioNotification.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utechandroid.com.radio.events.ChannelEvent;
import utechandroid.com.radio.firestore.helper.auth.FireStoreAuthHelper;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.Channel;
import utechandroid.com.radio.firestore.model.ChannelData;
import utechandroid.com.radio.firestore.model.MembersData;
import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class ChannelPublicActivity extends ColorsAppCompatActivity {

    @BindView(R.id.img_public_channel)
    ImageView imgPublicChannel;
    @BindView(R.id.toolbar_public_channel)
    Toolbar toolbarPublicChannel;
    @BindView(R.id.toolbar_layout_public_channel)
    CollapsingToolbarLayout toolbarLayoutPublicChannel;
    @BindView(R.id.appbar_layout_public_channel)
    AppBarLayout appbarLayoutPublicChannel;
    @BindView(R.id.edit_channel_public_name)
    EditText editChannelPublicName;
    @BindView(R.id.txtlyt_channel_public_name)
    TextInputLayout txtlytChannelPublicName;
    @BindView(R.id.edt_channel_public_description)
    EditText edtChannelPublicDescription;
    @BindView(R.id.txtlyt_channel_public_description)
    TextInputLayout txtlytChannelPublicDescription;
    @BindView(R.id.crd_channel_public_1)
    CardView crdChannelPublic1;
    @BindView(R.id.edit_channel_public_category)
    EditText editChannelPublicCategory;
    @BindView(R.id.txtlyt_channel_public_category)
    TextInputLayout txtlytChannelPublicCategory;
    @BindView(R.id.crd_channel_public_2)
    CardView crdChannelPublic2;
    @BindView(R.id.layout_channel_public)
    LinearLayout layoutChannelPublic;
    @BindView(R.id.btn_subscribe_channel)
    Button btnSubscribeChannel;
    @BindView(R.id.txt_channel_subscribe)
    TextView txtChannelSubscribe;
    @BindView(R.id.txt_channel_public_members)
    TextView txtChannelPublicMembers;
    @BindView(R.id.txt_channel_public_owner)
    TextView txtChannelPublicOwner;

    private String mChannelId;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private ListenerRegistration mRegistration;
    private ChannelData channel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTransition();
        setContentView(R.layout.activity_channel_public);
        ButterKnife.bind(this);

        db = FirebaseFirestore.getInstance();
        FirebaseFirestore.setLoggingEnabled(true);

        setSupportActionBar(toolbarPublicChannel);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        SetProgressDialog();

        EventBus eventbus = EventBus.getDefault();
        ChannelEvent channelEvent = eventbus.getStickyEvent(ChannelEvent.class);
        if (channelEvent != null) {
            channel = channelEvent.getmChannel();
            final int color = channel.getChannelColor();
            mChannelId = channel.getChannelId();

            LoadMemberData(mChannelId, channel.getChannelAdminId());

            String Category = channel.getChannelSubCategoryId();
            String Desc = channel.getChannelDescription();
            String name = channel.getChannelName();
            String url = channel.getChannelPhotoUrl();

            if (channel.getChannelAdminId().equals(new FireStoreAuthHelper().GetUserId())) {
                btnSubscribeChannel.setVisibility(View.GONE);
            }

            toolbarLayoutPublicChannel.setTitle("");
            getSupportActionBar().setTitle(name);
            editChannelPublicName.setText(name);
            editChannelPublicCategory.setText(Category);
            edtChannelPublicDescription.setText(Desc);

            if (url.isEmpty()) {
                imgPublicChannel.setColorFilter(color);
                toolbarLayoutPublicChannel.setContentScrimColor(color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int mColor = returnDarkerColor(color);
                    getWindow().setStatusBarColor(mColor);
                }
            } else {
                imgPublicChannel.setColorFilter(null);
                Glide.with(getApplicationContext())
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .crossFade()
                        .fitCenter()
                        .placeholder(R.drawable.placeholder)
                        .into(new ImageViewTarget<GlideDrawable>(imgPublicChannel) {
                            @Override
                            protected void setResource(GlideDrawable resource) {
                                setImage(resource);

                                if (!hasExtractedColorAlready()) {
                                    extractColor(resource);
                                }
                            }

                            private boolean hasExtractedColorAlready() {
                                return channel.getChannelColor() != 0;
                            }

                            private void setImage(GlideDrawable resource) {
                                imgPublicChannel.setImageDrawable(resource.getCurrent());
                            }

                            private void extractColor(GlideDrawable resource) {
                                Bitmap b = ((GlideBitmapDrawable) resource.getCurrent()).getBitmap();
                                Palette p = Palette.from(b).generate();
                                int defaultColor = getResources().getColor(R.color.colorPrimary);
                                int colorn = p.getDarkVibrantColor(defaultColor);

                                toolbarLayoutPublicChannel.setContentScrimColor(colorn);

                                //   toolbarLayoutMyRadioChannels.setBackgroundResource(colorn);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    int mColor = returnDarkerColor(color);
                                    getWindow().setStatusBarColor(mColor);
                                }
                            }
                        });
            }
            eventbus.removeStickyEvent(channelEvent);
        }
        CheckSubscribe();
    }

    private void LoadMemberData(String id, String uid) {
        DocumentReference docRef = new FireStoreHelper().GetChannelByIdDocument(id);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            final ChannelData channelData = documentSnapshot.toObject(ChannelData.class);
            List<String> set = new ArrayList<>();
            for (Map.Entry<String, Integer> f : channelData.getMembers().entrySet()) {
                String key = f.getKey();
                Integer value = f.getValue();
                if (value == 2) {
                    set.add(key);
                }
            }
            txtChannelPublicMembers.setText("Total Members : " + set.size());
        });

        DocumentReference userRef = new FireStoreHelper().GetUserByDocument(uid);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            final User channelData = documentSnapshot.toObject(User.class);
            txtChannelPublicOwner.setText("Owner : " + channelData.getUserName());
        });
    }

    private void SetProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
    }

    private void ProgressShow() {
        progressDialog.show();
    }

    private void ProgressHide() {
        progressDialog.dismiss();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void OnRadioEvent(ChannelEvent channelEvent) {

    }

    private void SetTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            //  w.setAllowEnterTransitionOverlap(false);
            //     w.setAllowReturnTransitionOverlap(false);
            Transition return_transition =
                    TransitionInflater.from(this).
                            inflateTransition(R.transition.channel_public_exit);
            Transition enter_transition =
                    TransitionInflater.from(this).
                            inflateTransition(R.transition.channel_public_enter);
            w.setReturnTransition(return_transition);
            w.setEnterTransition(enter_transition);
        }
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
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        if (mRegistration != null) {
            mRegistration.remove();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
        super.onBackPressed();
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
                /*if (mDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }*/
                onBackPressed();
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

    private void CheckSubscribe() {
        DocumentReference documentReference = new FireStoreHelper()
                .GetCurrentUserDocument();
        mRegistration = documentReference.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                // Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot.exists()) {
                User user = snapshot.toObject(User.class);
                if (user.getSubscribeChannels().entrySet().size() > 0) {
                    for (Map.Entry<String, Integer> f : user.getSubscribeChannels().entrySet()) {
                        String key = f.getKey();
                        if (key.equals(mChannelId)) {
                            Integer value2 = f.getValue();
                            Log.e("datacount", "" + value2);
                            if (value2 == 2) {
                                btnSubscribeChannel.setVisibility(View.VISIBLE);
                                btnSubscribeChannel.setText("UnSubscribe");
                                txtChannelSubscribe.setVisibility(View.INVISIBLE);
                            } else if (value2 == 1) {
                                btnSubscribeChannel.setVisibility(View.INVISIBLE);
                                txtChannelSubscribe.setVisibility(View.VISIBLE);
                                txtChannelSubscribe.setText("Your Subscribe request is pending...");
                            } else if (value2 == 3) {
                                btnSubscribeChannel.setVisibility(View.INVISIBLE);
                                txtChannelSubscribe.setVisibility(View.VISIBLE);
                                txtChannelSubscribe.setText("You already rejected by channel Admin...");
                            } else {
                                btnSubscribeChannel.setVisibility(View.VISIBLE);
                                btnSubscribeChannel.setText("Subscribe");
                                txtChannelSubscribe.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            btnSubscribeChannel.setVisibility(View.VISIBLE);
                            btnSubscribeChannel.setText("Subscribe");
                            txtChannelSubscribe.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    btnSubscribeChannel.setVisibility(View.VISIBLE);
                    btnSubscribeChannel.setText("Subscribe");
                    txtChannelSubscribe.setVisibility(View.INVISIBLE);
                }
                if (user.getCreatedChannels().entrySet().size() > 0) {
                    for (Map.Entry<String, Object> f : user.getCreatedChannels().entrySet()) {
                        String key = f.getKey();
                        if (key.equals(channel.getChannelId())) {

                            btnSubscribeChannel.setVisibility(View.INVISIBLE);
                            txtChannelSubscribe.setVisibility(View.VISIBLE);
                            txtChannelSubscribe.setText("You are channel Admin...");

                        } /*else {
                            btnSubscribeChannel.setVisibility(View.VISIBLE);
                            btnSubscribeChannel.setText("Subscribe");
                            txtChannelSubscribe.setVisibility(View.INVISIBLE);
                        }*/
                    }
                }
            }
        });
    }

    private void CheckSub() {
        progressDialog.setTitle("Loading...");
        ProgressShow();
        new FireStoreHelper().GetMemberCollection(mChannelId)
                .whereEqualTo(MembersData.FIELD_MEMBER_USER_ID,
                        new FireStoreAuthHelper().GetUserId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> snapshots = task.getResult().getDocuments();
                            if (snapshots.size() > 0) {
                                MembersData membersDataList = snapshots.get(0).toObject(MembersData.class);
                                UnSubscribe(membersDataList);
                            } else {
                                Subscribe();
                            }
                        }
                    }
                });

    }

    private void Subscribe() {
        //  ProgressShow();
        progressDialog.setTitle("Subscribing");
        WriteBatch batch = db.batch();

        if (channel.getChannelPrivate()) {
            final DocumentReference memberRef = new FireStoreHelper(this)
                    .GetMemberDocument(mChannelId);
            String memberId = memberRef.getId();
            MembersData members = new MembersData();
            members.setMemberId(memberId);
            members.setMemberChannelId(mChannelId);
            members.setMemberJoinedDate(new Date());
            members.setMemberRequestDate(new Date());
            members.setMemberRejectedDate(new Date());
            members.setMemberStatus(1);
            members.setMemberUserId(new FireStoreAuthHelper().GetUserId());
            batch.set(memberRef, members);

            DocumentReference userRef = new FireStoreHelper(this).GetCurrentUserDocument();
            Map<String, Object> docData = new HashMap<>();
            Map<String, Integer> nestedData = new HashMap<>();
            nestedData.put(mChannelId, 1);
            docData.put(User.FIELD_SUBSCRIBE_COLLECTION, nestedData);
            batch.set(userRef, docData, SetOptions.merge());

            DocumentReference radioRef = db.collection(Channel.FIELD_COLLECTION).document(mChannelId);
            Map<String, Object> radioData = new HashMap<>();
            Map<String, Integer> radionestedData = new HashMap<>();
            radionestedData.put(new FireStoreAuthHelper().GetUserId(), 1);
            radioData.put(MembersData.FIELD_COLLECTION, radionestedData);
            batch.set(radioRef, radioData, SetOptions.merge());
        } else {
            final DocumentReference memberRef = new FireStoreHelper(this).GetMemberDocument(mChannelId);
            String memberId = memberRef.getId();
            MembersData members = new MembersData();
            members.setMemberId(memberId);
            members.setMemberChannelId(mChannelId);
            members.setMemberJoinedDate(new Date());
            members.setMemberRequestDate(new Date());
            members.setMemberRejectedDate(new Date());
            members.setMemberStatus(2);
            members.setMemberUserId(new FireStoreAuthHelper().GetUserId());
            batch.set(memberRef, members);

            DocumentReference userRef = new FireStoreHelper(this).GetCurrentUserDocument();
            Map<String, Object> docData = new HashMap<>();
            Map<String, Integer> nestedData = new HashMap<>();
            nestedData.put(mChannelId, 2);
            docData.put(User.FIELD_SUBSCRIBE_COLLECTION, nestedData);
            batch.set(userRef, docData, SetOptions.merge());

            DocumentReference radioRef = db.collection(Channel.FIELD_COLLECTION).document(mChannelId);
            Map<String, Object> radioData = new HashMap<>();
            Map<String, Integer> radionestedData = new HashMap<>();
            radionestedData.put(new FireStoreAuthHelper().GetUserId(), 2);
            radioData.put(MembersData.FIELD_COLLECTION, radionestedData);
            batch.set(radioRef, radioData, SetOptions.merge());

            DocumentReference channelPermissionRef = db.collection(Channel.FIELD_COLLECTION).document(mChannelId);
            Map<String, Object> channelPermissionData = new HashMap<>();
            Map<String, Boolean> channelPermissionnestedData = new HashMap<>();
            channelPermissionnestedData.put(new FireStoreAuthHelper().GetUserId(), true);
            channelPermissionData.put(ChannelData.FIELD_COLLECTION_MEMBERS_WRITE_MESSAGE, channelPermissionnestedData);
            batch.set(channelPermissionRef, channelPermissionData, SetOptions.merge());
        }

        batch.commit().addOnCompleteListener(task -> {
            ProgressHide();
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Subscribe Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void UnSubscribe(MembersData membersData) {
        progressDialog.setTitle("UnSubscribing");

        String memberId = membersData.getMemberId();
        WriteBatch batch = db.batch();

        DocumentReference userRef = new FireStoreHelper(this).GetCurrentUserDocument();
        Map<String, Object> updates = new HashMap<>();
        updates.put(User.FIELD_SUBSCRIBE_COLLECTION + "." + mChannelId, FieldValue.delete());
        batch.update(userRef, updates);

        DocumentReference radioRef = db.collection(Channel.FIELD_COLLECTION).document(mChannelId);
        Map<String, Object> radioUpdates = new HashMap<>();
        radioUpdates.put(MembersData.FIELD_COLLECTION + "." + new FireStoreAuthHelper().GetUserId(), FieldValue.delete());
        batch.update(radioRef, radioUpdates);

        DocumentReference channelPermissionRef = db.collection(Channel.FIELD_COLLECTION).document(mChannelId);
        Map<String, Object> channelPermissionData = new HashMap<>();
        channelPermissionData.put(ChannelData.FIELD_COLLECTION_MEMBERS_WRITE_MESSAGE + "." + new FireStoreAuthHelper().GetUserId(), FieldValue.delete());
        batch.update(channelPermissionRef, channelPermissionData);

        final DocumentReference memberRef = new FireStoreHelper(this).GetMemberDocument(mChannelId, memberId);
        batch.delete(memberRef);

        batch.commit().addOnCompleteListener(task -> {
            ProgressHide();
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "UnSubscribe Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("errorrrr", "" + task.getException());
                //  Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btn_subscribe_channel)
    public void onViewClicked() {
        /*if (flag) {
            UnSubscribe();
        } else {*/
        CheckSub();
        //   }
    }
}
