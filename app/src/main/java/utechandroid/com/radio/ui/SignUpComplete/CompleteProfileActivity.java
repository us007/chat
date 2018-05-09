package utechandroid.com.radio.ui.SignUpComplete;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dmitrymalkovich.android.ProgressFloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.prudenttechno.radioNotification.R;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import utechandroid.com.radio.data.pref.SessionManager;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.ui.home.HomeActivity;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class CompleteProfileActivity extends ColorsAppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.img_profile)
    CircleImageView imgProfile;
    @BindView(R.id.img_profile_add)
    CircleImageView imgProfileAdd;
    @BindView(R.id.edt_sign_up_display_name)
    TextInputEditText edtSignUpDisplayName;
    @BindView(R.id.txtinlyt_sign_up_display_name)
    TextInputLayout txtinlytSignUpDisplayName;
    @BindView(R.id.lyt_sign_up_update)
    LinearLayout lytSignUpUpdate;
    @BindView(R.id.fab_progressBar)
    ProgressBar fabProgressBar;
    @BindView(R.id.fab_done)
    ProgressFloatingActionButton fabDone;
    @BindView(R.id.img_profile_pg)
    ProgressBar imgProfilePg;

    private String name;
    private SessionManager sessionManager;
    private ImagePicker imagePicker = new ImagePicker();
    StorageReference storageRef, imageRef;
    FirebaseStorage storage;
    UploadTask uploadTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        GetIntentValue();
        edtSignUpDisplayName.setText(name);
        sessionManager = new SessionManager(getApplicationContext());

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        imagePicker.setTitle("Choose Picture");
        imagePicker.setCropImage(true);

        fabProgressBar.getIndeterminateDrawable().setColorFilter(getThemeAccentColor(getApplicationContext()), PorterDuff.Mode.MULTIPLY);
    }

    private static int getThemeAccentColor(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorPrimaryDark;
        } else {
            colorAttr = context.getResources().getIdentifier("colorPrimaryDark", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    private void GetIntentValue() {
        name = getIntent().getExtras().getString("name");
    }

    @OnClick({R.id.img_profile_add, R.id.fab_done})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_profile_add:
                startChooser();
                break;
            case R.id.fab_done:
                SaveName();
                break;
        }
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
                imgProfile.setImageURI(imageUri);
            }

            // 用户拒绝授权回调
            @Override
            public void onPermissionDenied(int requestCode, String[] permissions,
                                           int[] grantResults) {
            }
        });
    }

    public void uploadImage(Uri uri) {
        imgProfileAdd.setVisibility(View.INVISIBLE);
        imgProfilePg.setVisibility(View.VISIBLE);
        try {
            Bitmap mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            byte[] data = baos.toByteArray();
            imageRef = storageRef.child("users/ " + sessionManager.GetUserId() + ".jpg");
            //starting upload
            uploadTask = imageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "Error in uploading!", Toast.LENGTH_SHORT).show();

                    imgProfileAdd.setVisibility(View.VISIBLE);
                    imgProfilePg.setVisibility(View.INVISIBLE);

                }
            }).addOnSuccessListener(taskSnapshot -> {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                SavePhoto(downloadUrl);

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void SavePhoto(Uri uri) {
        DocumentReference documentReference = new FireStoreHelper(getApplicationContext()).GetUserByDocument(sessionManager.GetUserId());
        documentReference
                .update(User.FIELD_USER_PHOTO_URL, uri.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        imgProfileAdd.setVisibility(View.VISIBLE);
                        imgProfilePg.setVisibility(View.INVISIBLE);
                        sessionManager.SaveProfile(uri.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        imgProfileAdd.setVisibility(View.VISIBLE);
                        imgProfilePg.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void SaveName() {
        String name = edtSignUpDisplayName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            txtinlytSignUpDisplayName.setError("Name cannot be empty");
        } else {
            fabProgressBar.setVisibility(View.VISIBLE);
            DocumentReference documentReference = new FireStoreHelper(getApplicationContext()).GetUserByDocument(sessionManager.GetUserId());
            documentReference
                    .update(User.FIELD_USER_NAME, name)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            fabProgressBar.setVisibility(View.INVISIBLE);
                            sessionManager.SaveName(name);
                            LoadHomeScreen();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            fabProgressBar.setVisibility(View.INVISIBLE);
                            LoadHomeScreen();
                            Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void LoadHomeScreen() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);

    }
}
