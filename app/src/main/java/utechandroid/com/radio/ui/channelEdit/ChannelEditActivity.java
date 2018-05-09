package utechandroid.com.radio.ui.channelEdit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;
import com.prudenttechno.radioNotification.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.ChannelData;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class ChannelEditActivity extends ColorsAppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    @BindView(R.id.imageView_edit_channel)
    ImageView imageViewEditChannel;
    @BindView(R.id.avatar_edit_channel)
    CircleImageView avatarEditChannel;
    @BindView(R.id.avatar_edit_channel_txt)
    TextView avatarEditChannelTxt;
    @BindView(R.id.framelayout_edit_channel)
    FrameLayout framelayoutEditChannel;
    @BindView(R.id.textview_title_edit_channel)
    TextView textviewTitleEditChannel;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapse_edit_channel)
    CollapsingToolbarLayout collapseEditChannel;
    @BindView(R.id.appbar_user_profile_channel)
    AppBarLayout appbarUserProfileChannel;
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
    @BindView(R.id.btn_update_channel)
    Button btnUpdateChannel;
    @BindView(R.id.layout_channel)
    LinearLayout layoutChannel;
    @BindView(R.id.img_profile_add)
    FloatingActionButton imgProfileAdd;
    @BindView(R.id.img_profile_pg)
    ProgressBar imgProfilePg;

    private String channelId;

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private ImagePicker imagePicker = new ImagePicker();
    StorageReference storageRef, imageRef;
    FirebaseStorage storage;
    UploadTask uploadTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTransition();
        setContentView(R.layout.activity_channel_edit);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        imagePicker.setTitle("Choose Picture");
        imagePicker.setCropImage(true);

        getIntentValues();

        appbarUserProfileChannel.addOnOffsetChangedListener(this);

        startAlphaAnimation(textviewTitleEditChannel, 0, View.INVISIBLE);

        LoadChannel();
    }

    private void SetTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition return_transition =
                    TransitionInflater.from(this).
                            inflateTransition(R.transition.channel_edit_exit);
            Transition enter_transition =
                    TransitionInflater.from(this).
                            inflateTransition(R.transition.channel_edit_enter);
            w.setReturnTransition(return_transition);
            w.setEnterTransition(enter_transition);
        }
    }

    @OnClick({R.id.img_profile_add, R.id.btn_update_channel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_profile_add:
                startChooser();
                break;
            case R.id.btn_update_channel:
                UpdateData();
                break;
        }
    }

    private void LoadChannel() {
        DocumentReference documentReference = new FireStoreHelper()
                .GetChannelByIdDocument(channelId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        ChannelData channelData = document.toObject(ChannelData.class);

                        editChannelPublicName.setText(channelData.getChannelName());
                        edtChannelPublicDescription.setText(channelData.getChannelDescription());
                        editChannelPublicCategory.setText(channelData.getChannelSubCategoryId());

                        if (channelData.getChannelPhotoUrl() != null) {
                            String url = channelData.getChannelPhotoUrl();

                            if (url.isEmpty()) {
                                int color = channelData.getChannelColor();
                                String letter = (channelData.getChannelName().substring(0, 1).toUpperCase());
                                avatarEditChannel.setImageResource(R.drawable.br_rect);
                                avatarEditChannel.setColorFilter(color);
                                avatarEditChannelTxt.setText(letter);
                                avatarEditChannelTxt.setVisibility(View.VISIBLE);
                            } else {
                                Glide.with(getApplicationContext())
                                        .load(url)
                                        .animate(R.anim.fade_in)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .fitCenter()
                                        .placeholder(R.drawable.placeholder)
                                        .into(avatarEditChannel);
                                avatarEditChannelTxt.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        // Log.d(TAG, "No such document");
                    }
                } else {
                    //  Log.d(TAG, "get failed with ", task.getException());
                }
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
                startAlphaAnimation(textviewTitleEditChannel, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitleEditChannel, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private void startChooser() {
        imagePicker.startChooser(this, new ImagePicker.Callback() {

            @Override
            public void onPickImage(Uri imageUri) {

            }

            @Override
            public void onCropImage(Uri imageUri) {
                uploadImage(imageUri);
                avatarEditChannel.setImageURI(imageUri);
            }

            @Override
            public void cropConfig(CropImage.ActivityBuilder builder) {
                builder.setGuidelines(CropImageView.Guidelines.OFF)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setRequestedSize(640, 640)
                        .setAspectRatio(5, 5);
            }

            // 用户拒绝授权回调
            @Override
            public void onPermissionDenied(int requestCode, String[] permissions,
                                           int[] grantResults) {
            }
        });
    }

    public void uploadImage(Uri uri) {
        imgProfileAdd.hide();
        imgProfilePg.setVisibility(View.VISIBLE);
        avatarEditChannelTxt.setVisibility(View.INVISIBLE);
        try {
            Bitmap mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            byte[] data = baos.toByteArray();
            imageRef = storageRef.child("channelImages/ " + channelId + ".jpg");
            //starting upload
            uploadTask = imageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(getApplicationContext(), "Error in uploading!", Toast.LENGTH_SHORT).show();
                    imgProfileAdd.hide();
                    imgProfilePg.setVisibility(View.VISIBLE);
                    avatarEditChannelTxt.setVisibility(View.INVISIBLE);
                }
            }).addOnSuccessListener(taskSnapshot -> {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                imgProfileAdd.show();
                imgProfilePg.setVisibility(View.INVISIBLE);
                avatarEditChannelTxt.setVisibility(View.INVISIBLE);

                new FireStoreHelper().GetChannelByIdDocument(channelId)
                        .update(ChannelData.FIELD_CHANNEL_PHOTO_URL, downloadUrl.toString())
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = editChannelPublicName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            txtlytChannelPublicName.setError("Channel Name can't be empty.");
            valid = false;
        }

        String desc = edtChannelPublicDescription.getText().toString();
        if (TextUtils.isEmpty(desc)) {
            txtlytChannelPublicDescription.setError("Channel Description can't be empty.");
            valid = false;
        }

        return valid;
    }

    public void UpdateData() {
        if (validateForm()) {
            String name = editChannelPublicName.getText().toString();
            String desc = edtChannelPublicDescription.getText().toString();

            Map<String, Object> data = new HashMap<>();
            data.put(ChannelData.FIELD_CHANNEL_NAME, name);
            data.put(ChannelData.FIELD_CHANNEL_DESCRIPTION, desc);

            DocumentReference Ref = new FireStoreHelper(getApplicationContext()).GetChannelByIdDocument(channelId);
            Ref.update(data)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getApplicationContext(), "Successfully Updated...", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Error While Updating Data...", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }
}
