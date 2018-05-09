package utechandroid.com.radio.ui.setting;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prudenttechno.radioNotification.R;


import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utechandroid.com.radio.MyApplication;
import utechandroid.com.radio.data.pref.SessionManager;
import utechandroid.com.radio.util.ColorPicker.ColorPicker;
import utechandroid.com.radio.util.Colors.AccentColor;
import utechandroid.com.radio.util.Colors.Colors;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;
import utechandroid.com.radio.util.Colors.NightMode;
import utechandroid.com.radio.util.Colors.PrimaryColor;

public class SettingActivity extends ColorsAppCompatActivity {
    private static final String TAG = "SettingActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar_setting)
    ProgressBar progressBarSetting;
    @BindView(R.id.txt_theme_title)
    TextView txtThemeTitle;
    @BindView(R.id.txt_color_circularview)
    ImageView txtColorCircularview;
    @BindView(R.id.layout_color_change)
    LinearLayout layoutColorChange;
    @BindView(R.id.title_change_mode)
    TextView titleChangeMode;
    LinearLayout layoutChangeMode;
    @BindView(R.id.layout_setting)
    LinearLayout layoutSetting;
    private SessionManager sessionManager;
    private boolean ModeSwitch;
    //  private MyApplication myApplication;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  sessionManager = new SessionManager(getApplicationContext());
        // myApplication = new MyApplication();

        ChangeTheme();
        layoutChangeMode = findViewById(R.id.layout_change_mode);
        layoutChangeMode.setOnClickListener(v -> showChangeLangDialog());
//        if (MyApplication.instance().getSessionManager().getMode()) {
//            modeSwitchButton.setChecked(false);
//            // modeSwitchButton.isChecked() = ;
//        } else {
//            modeSwitchButton.setChecked(true);
//        }
//        modeSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    ModeSwitch = false;
//                    MyApplication.instance().getSessionManager().SaveMode(ModeSwitch);
//                } else {
//                    ModeSwitch = true;
//
//                    MyApplication.instance().getSessionManager().SaveMode(ModeSwitch);
//
//                }
//                SetTheme();
//
//            }
//        });


    }

    private void SetTheme() {
        if (MyApplication.instance().getSessionManager().getMode()) {
            Colors.setTheme(MyApplication.instance().getSessionManager().getPrimary_color(), MyApplication.instance().getSessionManager().getAccent_color(), NightMode.NIGHT);
            recreate();
        } else {
            Colors.setTheme(MyApplication.instance().getSessionManager().getPrimary_color(), MyApplication.instance().getSessionManager().getAccent_color(), NightMode.DAY);
            recreate();
        }
    }

    private void ChangeTheme() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;

        GradientDrawable gD = new GradientDrawable();
        gD.setColor(color);
        gD.setShape(GradientDrawable.OVAL);
        txtColorCircularview.setBackground(gD);
    }


    @OnClick(R.id.layout_color_change)
    public void onViewClicked() {
        ColorPicker colorPicker = new ColorPicker(this);
        colorPicker.disableDefaultButtons(true);
        colorPicker.setRoundColorButton(true);
        colorPicker.setColumns(4);
        colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
            @Override
            public void setOnFastChooseColorListener(int position, int color) {
                colorPicker.dismissDialog();
                Object[] var6 = new Object[]{16777215 & color};
                String var10000 = String.format("#%06X", Arrays.copyOf(var6, var6.length));

                //  String hexColor = String.format("#%06X", 0xFFFFFF, color);
                switch (var10000) {
                    case "#F44336":
                        MyApplication.instance().getSessionManager().SaveColor(PrimaryColor.RED_500, AccentColor.BLUE_A700);
                        break;
                    case "#E91E63":
                        MyApplication.instance().getSessionManager().SaveColor(PrimaryColor.PINK_500, AccentColor.LIGHT_BLUE_A700);
                        break;

                    case "#9C27B0": {
                        MyApplication.instance().getSessionManager().SaveColor(PrimaryColor.PURPLE_500, AccentColor.TEAL_A700);
                        break;
                    }
                    case "#673AB7": {
                        MyApplication.instance().getSessionManager().SaveColor(PrimaryColor.DEEP_PURPLE_500, AccentColor.GREEN_A700);
                        break;
                    }
                    case "#3F51B5": {
                        MyApplication.instance().getSessionManager().SaveColor(PrimaryColor.INDIGO_500, AccentColor.RED_A700);
                        break;
                    }
                    case "#2196F3": {
                        MyApplication.instance().getSessionManager().SaveColor(PrimaryColor.BLUE_500, AccentColor.RED_A400);
                        break;
                    }
                    case "#03A9F4": {
                        MyApplication.instance().getSessionManager().SaveColor(PrimaryColor.LIGHT_BLUE_500, AccentColor.DEEP_ORANGE_A400);
                        break;
                    }
                    case "#00BCD4": {
                        MyApplication.instance().getSessionManager().SaveColor(PrimaryColor.CYAN_500, AccentColor.DEEP_ORANGE_A400);
                        break;
                    }
                    case "#009688": {
                        MyApplication.instance().getSessionManager().SaveColor(PrimaryColor.TEAL_500, AccentColor.RED_A400);
                        break;
                    }
                    case "#4CAF50": {
                        MyApplication.instance().getSessionManager().SaveColor(PrimaryColor.GREEN_500, AccentColor.DEEP_PURPLE_A400);
                        break;
                    }
                    case "#FF5722": {
                        MyApplication.instance().getSessionManager().SaveColor(PrimaryColor.DEEP_ORANGE_500, AccentColor.INDIGO_A700);
                        break;
                    }
                    case "#795548": {
                        MyApplication.instance().getSessionManager().SaveColor(PrimaryColor.BROWN_500, AccentColor.BLUE_GREY_A200);
                        break;
                    }
                    case "#607D8B": {
                        MyApplication.instance().getSessionManager().SaveColor(PrimaryColor.BLUE_GREY_500, AccentColor.DEEP_ORANGE_A400);
                        break;
                    }
                }
                SetTheme();

            }

            @Override
            public void onCancel() {

            }
        });
        colorPicker.show();

    }

    public void showChangeLangDialog() {
        AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_changepass, null);
        dialogBuilder.setView(dialogView);

        AppCompatEditText etPassword = dialogView.findViewById(R.id.edt_forgot_pass);
        AppCompatButton btnSend = dialogView.findViewById(R.id.btn_forgot_password);
        AppCompatEditText etConfirmPassword = dialogView.findViewById(R.id.edt_forgot_cpass);

        btnSend.setOnClickListener(view -> {
            String pass = etPassword.getText().toString();
            String cpass = etConfirmPassword.getText().toString();
            if (TextUtils.isEmpty(pass)) {
                etPassword.setError("enter Password");
                return;
            }
            if (TextUtils.isEmpty(cpass)) {
                etConfirmPassword.setError("enter Password");
                return;
            }
            if (!pass.equals(cpass)) {
                Toast.makeText(this, "Password not match", Toast.LENGTH_LONG).show();
                return;
            }
            FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
            auth.updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    task.isComplete();
                    Toast.makeText(SettingActivity.this, "Send", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SettingActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
            dialogBuilder.dismiss();
        });

        dialogBuilder.show();

    }

}
