package utechandroid.com.radio.ui.splashscreen;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.prudenttechno.radioNotification.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import lucifer.org.snackbartest.MySnack;
import utechandroid.com.radio.data.pref.SessionManager;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.ui.home.HomeActivity;
import utechandroid.com.radio.ui.signin.SigninActivity;
import utechandroid.com.radio.util.NetworkUtils;

public class SplashScreenActivity extends AppCompatActivity {

    @BindView(R.id.progress_splash)
    ProgressBar progressSplash;
    @BindView(R.id.layout_splash_screen)
    LinearLayout layoutSplashScreen;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ButterKnife.bind(this);

        sessionManager = new SessionManager(getApplicationContext());

        progressSplash.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(() -> {
            LoadHome();
        }, SPLASH_TIME_OUT);
    }

    private void LoadHome() {
     //   progressSplash.setVisibility(View.INVISIBLE);
        if (NetworkUtils.isConnected(getApplicationContext())) {
          //  progressSplash.setVisibility(View.VISIBLE);
            if (!sessionManager.isLoggedIn()) {
                Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
            } else {
                LoadUserData(sessionManager.GetUserId());
            }
        } else {
            LoadData();
        }
    }

    private void LoadData() {
        //optional
        new MySnack.SnackBuilder(layoutSplashScreen)
                .setText("Internet Error...")
                .setTextColor("#000000")   //optional
                .setTextSize(20)           //optional
                .setBgColor("#ffffff")      //optional
                .setDurationInSeconds(1000)  //will display for 10 seconds
                .setActionBtnColor("#FF5252") //optional
                .setIcon(R.drawable.ic_wifi_black)
                //or  .setIcon(R.drawable.ic_info_black_24dp)
                .setActionListener("Retry", view -> LoadHome())
                .build();
    }

    private static int getThemeAccentColor(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = context.getResources().getIdentifier("colorAccent", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    private void LoadUserData(String id) {
        DocumentReference documentReference = new FireStoreHelper(getApplicationContext()).GetUserByDocument(id);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
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
                }
            }
        });
    }
}
