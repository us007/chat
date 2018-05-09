package utechandroid.com.radio.ui.signup;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.prudenttechno.radioNotification.R;


import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utechandroid.com.radio.data.pref.SessionManager;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.ui.SignUpComplete.CompleteProfileActivity;

public class SignupActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar_sign_up)
    AppBarLayout appbarSignUp;
    @BindView(R.id.edt_sign_up_email)
    TextInputEditText edtSignUpEmail;
    @BindView(R.id.txtinlyt_sign_up_email)
    TextInputLayout txtinlytSignUpEmail;
    @BindView(R.id.edt_sign_up_password)
    TextInputEditText edtSignUpPassword;
    @BindView(R.id.txtinlyt_sign_up_password)
    TextInputLayout txtinlytSignUpPassword;
    @BindView(R.id.edt_sign_up_confirm_password)
    TextInputEditText edtSignUpConfirmPassword;
    @BindView(R.id.txtinlyt_sign_up_confirm_password)
    TextInputLayout txtinlytSignUpConfirmPassword;
    @BindView(R.id.btn_sign_up)
    AppCompatButton btnSignUp;
    @BindView(R.id.lyt_sign_up)
    LinearLayout lytSignUp;

    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTransition();

        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        SetUpProgressDialog();

        sessionManager = new SessionManager(getApplicationContext());

    }

    private void SetTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition enter_transition =
                    TransitionInflater.from(this).
                            inflateTransition(R.transition.sign_up_enter);
            w.setEnterTransition(enter_transition);
        }
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

    private void SignUpWithEmail(String email, String password) {
        ShowProgressDialog();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user != null) {
                            LoadUserData(user.getUid(), email);
                        }

                    } else {
                        DismissProgressDialog();
                        Toast.makeText(getApplicationContext(), "" + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void LoadUserData(String id, String email) {
        String name = email.split("@")[0];
        User user = new User();
        user.setUserEmail(email);
        user.setTimestamp(new Date());
        user.setUserId(id);
        user.setUserName(name);
        user.setUserPhotoUrl("");

        DocumentReference documentReference = new FireStoreHelper(getApplicationContext()).GetUserByDocument(id);
        documentReference
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DismissProgressDialog();
                        sessionManager.createLoginSession(user.getUserId(),
                                name, email, "");
                        LoadCompleteUi(name);
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

    private void LoadCompleteUi(String name) {
        Intent intent = new Intent(getApplicationContext(), CompleteProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("name", name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @OnClick(R.id.btn_sign_up)
    public void onViewClicked() {
        if (validateForm()) {
            String email = edtSignUpEmail.getText().toString();
            String password = edtSignUpPassword.getText().toString();

            SignUpWithEmail(email, password);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = edtSignUpEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            txtinlytSignUpEmail.setError("Required.");
            valid = false;
        }

        String password = edtSignUpPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtinlytSignUpPassword.setError("Required.");
            valid = false;
        }

        String confirm_password = edtSignUpConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(confirm_password)) {
            txtinlytSignUpConfirmPassword.setError("Required.");
            valid = false;
        }
        return valid;
    }
}
