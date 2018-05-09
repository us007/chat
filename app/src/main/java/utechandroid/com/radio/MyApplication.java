package utechandroid.com.radio;

import android.app.Application;
import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.messaging.FirebaseMessaging;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import utechandroid.com.radio.data.pref.SessionManager;
import utechandroid.com.radio.util.Colors.Colors;
import utechandroid.com.radio.util.Colors.NightMode;

/**
 * Created by Dharmik Patel on 30-Nov-17.
 */

public class MyApplication extends Application {
    private SessionManager sessionManager;
    private static MyApplication mCurrentInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        mCurrentInstance = this;

        //topic for notification

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);

        if (getSessionManager().getMode()) {
            Colors.setTheme(getSessionManager().getPrimary_color(), getSessionManager().getAccent_color(), NightMode.DAY);
        } else {
            Colors.setTheme(getSessionManager().getPrimary_color(), getSessionManager().getAccent_color(), NightMode.NIGHT);
        }
    }

    public final SessionManager getSessionManager() {
        if (this.sessionManager == null) {
            this.sessionManager = new SessionManager((Context) this);
        }
        return sessionManager;


    }

    public static MyApplication instance() {
        return mCurrentInstance;
    }

    public static Context context() {
        return mCurrentInstance.getApplicationContext();
    }
}
