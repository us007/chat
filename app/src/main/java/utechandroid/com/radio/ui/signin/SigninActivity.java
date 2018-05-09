package utechandroid.com.radio.ui.signin;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.prudenttechno.radioNotification.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utechandroid.com.radio.data.pref.SessionManager;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.ui.home.HomeActivity;
import utechandroid.com.radio.ui.signup.SignupActivity;

public class SigninActivity extends AppCompatActivity {

    @BindView(R.id.edt_sign_in_email)
    EditText edtSignInEmail;
    @BindView(R.id.edt_sign_in_password)
    EditText edtSignInPassword;
    @BindView(R.id.btn_sign_in)
    Button btnSignIn;
    @BindView(R.id.btn_sign_up_activity)
    Button btnSignUpActivity;
    @BindView(R.id.txtinlyt_sign_in_email)
    TextInputLayout txtinlytSignInEmail;
    @BindView(R.id.txtinlyt_sign_in_password)
    TextInputLayout txtinlytSignInPassword;
    @BindView(R.id.tv_forgot_password)
    TextView tvForgotPassword;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTransition();

        setContentView(R.layout.activity_signin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AutoStart();
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
                            inflateTransition(R.transition.sign_in_enter);
            w.setEnterTransition(enter_transition);
            Transition return_transition =
                    TransitionInflater.from(this).
                            inflateTransition(R.transition.sign_up_enter);
            w.setReturnTransition(return_transition);
        }
    }

    @OnClick({R.id.btn_sign_in, R.id.btn_sign_up_activity, R.id.tv_forgot_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_in:
                if (validateForm()) {
                    String email = edtSignInEmail.getText().toString();
                    String password = edtSignInPassword.getText().toString();
                    SignIn(email, password);
                }
                break;
            case R.id.btn_sign_up_activity:
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                break;
            case R.id.tv_forgot_password:
                showChangeLangDialog();
                break;
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = edtSignInEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            txtinlytSignInEmail.setError("Required.");
            valid = false;
        }

        String password = edtSignInPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtinlytSignInPassword.setError("Required.");
            valid = false;
        }

        return valid;
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

    private void SignIn(String email, String password) {
        ShowProgressDialog();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            LoadUserData(user.getUid());
                        }

                    } else {
                        DismissProgressDialog();
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getApplicationContext(), "" + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void LoadUserData(String id) {

        DocumentReference documentReference = new FireStoreHelper(getApplicationContext()).GetUserByDocument(id);

        documentReference.get().addOnCompleteListener(task -> {
            DismissProgressDialog();
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    FirebaseMessaging.getInstance().subscribeToTopic("radio007");

                    User user = document.toObject(User.class);
                    sessionManager.createLoginSession(user.getUserId(),
                            user.getUserName(), user.getUserEmail(), user.getUserPhotoUrl());
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
                        startActivity(intent, options.toBundle());
                    } else {
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "User not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    public void showChangeLangDialog() {
        AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_forgot, null);
        dialogBuilder.setView(dialogView);

        AppCompatEditText etForgotPassword = dialogView.findViewById(R.id.edt_forgot_email);
        AppCompatButton btnSend = dialogView.findViewById(R.id.btn_forgot_password);
        dialogBuilder.setTitle("Forgot Password");

        btnSend.setOnClickListener(view -> {
            String email = etForgotPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                etForgotPassword.setError("enter email");
                return;
            }
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    task.isComplete();
                    Toast.makeText(SigninActivity.this, "Send", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SigninActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
            dialogBuilder.dismiss();
        });

        dialogBuilder.show();

    }

    private void AutoStart() {
        String model = Build.MANUFACTURER;
        if (model.equalsIgnoreCase("Xiaomi")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("RadioBroadcast Need Auto Start Permission");
            builder.setMessage("Allow AutoStart Permission");
            builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("error", e.getLocalizedMessage());
                    }
                }

            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (model.equalsIgnoreCase("oppo")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("RadioBroadcast Need Auto Start Permission");
            builder.setMessage("Allow AutoStart Permission");
// Add the buttons
            builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("error", e.getLocalizedMessage());
                    }
                }

            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (model.equalsIgnoreCase("vivo")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Add the buttons
            builder.setTitle("RadioBroadcast Need Auto Start Permission");
            builder.setMessage("Allow AutoStart Permission");
            builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("error", e.getLocalizedMessage());
                    }
                }

            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}

