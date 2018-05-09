package utechandroid.com.radio.ui.CreateRadio;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jackandphantom.circularprogressbar.CircleProgressbar;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.prudenttechno.radioNotification.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utechandroid.com.radio.firestore.model.Radio;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;
import utechandroid.com.radio.util.Constants;

public class CreateRadioActivity extends ColorsAppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pg_create_radio_upload_image)
    CircleProgressbar pgCreateRadioUploadImage;
    @BindView(R.id.img_create_radio_upload_image)
    RoundedImageView imgCreateRadioUploadImage;
    @BindView(R.id.edit_create_radio_name)
    EditText editCreateRadioName;
    @BindView(R.id.txtlyt_create_radio_name)
    TextInputLayout txtlytCreateRadioName;
    @BindView(R.id.edt_create_radio_description)
    EditText edtCreateRadioDescription;
    @BindView(R.id.txtlyt_create_radio_description)
    TextInputLayout txtlytCreateRadioDescription;
    @BindView(R.id.chk_create_radio_private)
    CheckBox chkCreateRadioPrivate;
    @BindView(R.id.btn_create_radio_save)
    Button btnCreateRadioSave;

    StorageReference storageReference;
    private String mRadioId, mRadioName, mRadioUrl = "";
    private ImagePicker imagePicker = new ImagePicker();
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_radio);
        ButterKnife.bind(this);

        GetIntentValues();

        setSupportActionBar(toolbar);

        imagePicker.setTitle("Choose Picture");
        imagePicker.setCropImage(true);

        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        editCreateRadioName.setText(mRadioName);

    }

    private void GetIntentValues() {
        Bundle bundle = getIntent().getExtras();
        mRadioId = bundle.getString(Constants.RADIO_ID);
        mRadioName = bundle.getString(Constants.RADIO_NAME);
    }

    @OnClick({R.id.img_create_radio_upload_image, R.id.btn_create_radio_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_create_radio_upload_image:
                startChooser();
                break;
            case R.id.btn_create_radio_save:
                String desc = edtCreateRadioDescription.getText().toString();
                Boolean flag = false;
                if (chkCreateRadioPrivate.isChecked()) {
                    flag = true;
                } else {
                    flag = false;
                }
                CompleteRadio(desc, flag);
                break;
        }
    }

    private void UploadImage(Uri uri) {
        pgCreateRadioUploadImage.setVisibility(View.VISIBLE);
        StorageReference sRef = storageReference.child(Constants.STORAGE_PATH_RADIO_IMAGE + mRadioId + "." + getFileExtension(uri));
        sRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pgCreateRadioUploadImage.setVisibility(View.GONE);
                        UpdateRadioWithUrl(taskSnapshot.getDownloadUrl().toString());
                        //adding an upload to firebase database
                        /*String uploadId = mDatabase.push().getKey();
                        mDatabase.child(uploadId).setValue(upload);*/
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //displaying the upload progress
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        pgCreateRadioUploadImage.setProgress((float) progress);
                    }
                });
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
                imgCreateRadioUploadImage.setImageURI(imageUri);
                UploadImage(imageUri);
            }

            @Override
            public void cropConfig(CropImage.ActivityBuilder builder) {
                builder
                        .setMultiTouchEnabled(false)
                        .setGuidelines(CropImageView.Guidelines.OFF)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setRequestedSize(960, 540)
                        .setAspectRatio(16, 9);
            }

            // 用户拒绝授权回调
            @Override
            public void onPermissionDenied(int requestCode, String[] permissions,
                                           int[] grantResults) {
            }
        });
    }

    public String getFileExtension(Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    private void UpdateRadioWithUrl(String url) {
        mRadioUrl = url;
        DocumentReference Ref = db.collection("radios").document(mRadioId);
        Ref.update(Radio.FIELD_RADIO_PHOTO_URL, url)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void CompleteRadio(final String description, Boolean flag) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(Radio.FIELD_RADIO_DESCRIPTION, description);
        DocumentReference Ref = db.collection("radios").document(mRadioId);
        Ref.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                      /* // Intent intent = new Intent(getApplicationContext(), MyRadioChannelsActivity.class);
                        intent.putExtra(Constants.RADIO_ID, mRadioId);
                        intent.putExtra(Constants.RADIO_NAME, mRadioName);
                        intent.putExtra(Constants.RADIO_DESCRIPTION, description);
                        intent.putExtra(Constants.RADIO_PHOTO_URL, mRadioUrl);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptions options =
                                    ActivityOptions.
                                            makeSceneTransitionAnimation(CreateRadioActivity.this);
                            startActivity(intent, options.toBundle());
                            finish();
                        }else {
                            startActivity(intent);
                            finish();
                        }*/
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
