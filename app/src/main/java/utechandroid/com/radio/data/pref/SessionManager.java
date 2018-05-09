package utechandroid.com.radio.data.pref;

/**
 * Created by Utsav Shah on 08-Dec-17.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import java.util.HashMap;
import utechandroid.com.radio.data.db.helper.RealmHelper;
import utechandroid.com.radio.service.ServiceUtils;
import utechandroid.com.radio.ui.signin.SigninActivity;
import utechandroid.com.radio.util.Colors.AccentColor;
import utechandroid.com.radio.util.Colors.NightMode;
import utechandroid.com.radio.util.Colors.PrimaryColor;

public class SessionManager {
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    PrimaryColor defaultPrimary = PrimaryColor.INDIGO_500;
    AccentColor defaultAccent = AccentColor.INDIGO_A200;
    NightMode Mode = NightMode.DAY;
    // SharedPref file name
    private static final String PREF_NAME = "RadioBroadCast";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PROFILE_URL = "profile_url";

    public static final String KEY_PRIMARY_COLOR = "primary_color_theme";
    public static final String KEY_ACCENT_COLOR = "accent_color_theme";
    public static final String KEY_DAY_MODE = "day_mode";
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String id, String name, String email, String profile) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PROFILE_URL, profile);
        editor.commit();
    }

    public void createLoginSession(String id, String email) {
        editor.putString(KEY_ID, id);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public void SaveName(String name) {
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public void SaveProfile(String profile) {
        editor.putString(KEY_PROFILE_URL, profile);
        editor.commit();
    }

    public void SaveUserData(String name, String email) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
//    public void checkLogin() {
//        if (!this.isLoggedIn()) {
//            Intent i = new Intent(_context, SignActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            _context.startActivity(i);
//        }
//
//    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        return user;
    }

    public String GetUserId() {
        return pref.getString(KEY_ID, null);
    }

    public String GetUserName() {
        return pref.getString(KEY_NAME, null);
    }

    public String GetUserEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String GetUserProfileUrl() {
        return pref.getString(KEY_PROFILE_URL, null);
    }
    public final PrimaryColor getPrimary_color() {
        PrimaryColor var10000 = PrimaryColor.findById(this.pref.getString(KEY_PRIMARY_COLOR, this.defaultPrimary.getId()));
        return var10000;
    }

    public final AccentColor getAccent_color() {
        AccentColor var10000 = AccentColor.findById(this.pref.getString(KEY_ACCENT_COLOR, this.defaultAccent.getId()));

        return var10000;
    }

    public  boolean getMode() {
        return this.pref.getBoolean(KEY_DAY_MODE, true);
    }
    public void SaveColor(PrimaryColor primaryColor,AccentColor accentColor){
        editor.putString(KEY_PRIMARY_COLOR, primaryColor.getId());
        editor.putString(KEY_ACCENT_COLOR, accentColor.getId());
        editor.commit();

    }
    public void SaveMode(Boolean Daymode) {
        editor.putBoolean(KEY_DAY_MODE, Daymode);
        editor.commit();
    }
    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        ServiceUtils.stopServiceFriendChat(_context, false);
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new RealmHelper(_context).ClearAll();
        FirebaseAuth.getInstance().signOut();

        FirebaseMessaging.getInstance().unsubscribeFromTopic("radio007");

        Intent intent = new Intent(_context, SigninActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) _context);
            _context.startActivity(intent, options.toBundle());
        } else {*/
        _context.startActivity(intent);
        //  }
    }

    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
