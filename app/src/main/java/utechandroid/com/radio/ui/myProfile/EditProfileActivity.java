package utechandroid.com.radio.ui.myProfile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import utechandroid.com.radio.data.pref.SessionManager;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class EditProfileActivity extends ColorsAppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.img_profile)
    CircleImageView imgProfile;
    @BindView(R.id.img_profile_add)
    FloatingActionButton imgProfileAdd;
    @BindView(R.id.img_profile_pg)
    ProgressBar imgProfilePg;
    @BindView(R.id.edit_user_name)
    EditText editUserName;
    @BindView(R.id.txtlyt_user_name)
    TextInputLayout txtlytUserName;
    @BindView(R.id.edt_user_email)
    EditText edtUserEmail;
    @BindView(R.id.txtlyt_user_email)
    TextInputLayout txtlytUserEmail;
    @BindView(R.id.crd_user_1)
    CardView crdUser1;
    @BindView(R.id.btn_update_profile)
    Button btnUpdateProfile;

    private SessionManager sessionManager;
    private ImagePicker imagePicker = new ImagePicker();
    StorageReference storageRef, imageRef;
    FirebaseStorage storage;
    UploadTask uploadTask;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        SetUpProgressDialog();

        sessionManager = new SessionManager(getApplicationContext());

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        imagePicker.setTitle("Choose Picture");
        imagePicker.setCropImage(true);

        LoadProfile();
    }

    private void LoadProfile() {
        String name = sessionManager.GetUserName();
        String email = sessionManager.GetUserEmail();
        String url = sessionManager.GetUserProfileUrl();

        editUserName.setText(name);
        edtUserEmail.setText(email);

        Glide.with(getApplicationContext())
                .load(url)
                .animate(R.anim.fade_in)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .placeholder(R.drawable.profile_image)
                .into(imgProfile);

    }

    @OnClick({R.id.img_profile_add, R.id.btn_update_profile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_profile_add:
                startChooser();
                break;
            case R.id.btn_update_profile:
                SaveData();
                break;
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

    private void SetUpProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    private void ShowProgressDialog() {
        progressDialog.show();
    }

    private void DismissProgressDialog() {
        progressDialog.dismiss();
    }

    public void uploadImage(Uri uri) {
        imgProfileAdd.hide();
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

                    imgProfileAdd.show();
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

    private void SaveData() {
        String name = editUserName.getText().toString();
        String email = edtUserEmail.getText().toString();
        if (TextUtils.isEmpty(name)) {
            txtlytUserName.setError("Name cannot be empty");
        } else if (TextUtils.isEmpty(name)) {
            txtlytUserEmail.setError("Email cannot be empty");
        } else {
            ShowProgressDialog();
            Map<String, Object> data = new HashMap<>();
            data.put(User.FIELD_USER_NAME, name);
            data.put(User.FIELD_USER_EMAIL, email);
            DocumentReference documentReference = new FireStoreHelper(getApplicationContext()).GetUserByDocument(sessionManager.GetUserId());
            documentReference.update(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sessionManager.SaveUserData(name, email);
                            DismissProgressDialog();
                            Toast.makeText(getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            DismissProgressDialog();
                            Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
