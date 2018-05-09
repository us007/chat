package utechandroid.com.radio.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.prudenttechno.radioNotification.R;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utechandroid.com.radio.firestore.helper.auth.FireStoreAuthHelper;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.Message;
import utechandroid.com.radio.firestore.model.MessageReadData;
import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.ui.home.HomeActivity;

/**
 * Created by Dharmik Patel on 27-Nov-17.
 */

public class ChannelChatService extends Service {

    private static String TAG = "ChannelChatService";
    // Binder given to clients
    public final IBinder mBinder = new LocalBinder();

    private FirebaseFirestore db;
    private ListenerRegistration userListener, messageListner, messageReadListner;

    public ChannelChatService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        db = FirebaseFirestore.getInstance();

        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        if (user1 != null) {
            DocumentReference documentReference = new FireStoreHelper().GetCurrentUserDocument();
            userListener = documentReference.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    // Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot.exists()) {
                    User user = snapshot.toObject(User.class);
                    if (user.getSubscribeChannels().entrySet().size() > 0) {
                        List<String> set = new ArrayList<>();
                        for (Map.Entry<String, Integer> f : user.getSubscribeChannels().entrySet()) {
                            String key = f.getKey();
                            Integer value2 = f.getValue();
                            if (value2 == 2) {
                                set.add(key);
                            }
                        }
                        LoadMessages(set);
                    }
                }
            });
        }


    }

    private void LoadMessages(List<String> stringList) {
        for (int i = 0; i < stringList.size(); i++) {
            Query query = db.collection(Message.FIELD_COLLECTION)
                    .document(stringList.get(i))
                    .collection(Message.FIELD_COLLECTION);

            messageListner = query.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    return;
                }
                if (snapshot != null && !snapshot.isEmpty()) {
                    for (DocumentChange change : snapshot.getDocumentChanges()) {
                        switch (change.getType()) {
                            case ADDED:
                                DocumentSnapshot doc = change.getDocument();
                                final Message messageList = doc.toObject(Message.class);
                                UnReadMessages(messageList.getMessageId(), messageList.getMessageChannel());
                                break;
                            case MODIFIED:

                                break;
                            case REMOVED:

                                break;
                        }
                    }
                }
            });
        }
    }

    private void UnReadMessages(String MessageId, String ChannelId) {
        DocumentReference documentReference = new FireStoreHelper().GetMessageReadDocument(MessageId, ChannelId);
        messageReadListner = documentReference.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }
            if (snapshot.exists()) {
                MessageReadData user = snapshot.toObject(MessageReadData.class);
                if (user.getMembers().entrySet().size() > 0) {
                    for (Map.Entry<String, Integer> f : user.getMembers().entrySet()) {
                        String key = f.getKey();
                        if (key.equals(new FireStoreAuthHelper().GetUserId())) {
                            Integer value = f.getValue();
                            if (value == 1) {
                               /* createNotify("New Message",
                                        user.getMessage().getMessage(),
                                        Integer.parseInt(user.getMessage().getMessageId().replaceAll("[\\D]", "")));*/

                                WriteBatch batch = db.batch();

                                DocumentReference radioRef = snapshot.getReference();
                                Map<String,Object> radioUpdates = new HashMap<>();
                                radioUpdates.put("members" + "." +new FireStoreAuthHelper().GetUserId(), 2);
                                batch.update(radioRef,radioUpdates);

                                batch.commit();
                            }
                        }
                    }
                }
            }
        });
    }

    public void stopNotify(String id) {
        //mapMark.put(id, false);
    }

    public void createNotify(String name, String content, int id) {
        Intent activityIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                .setContentTitle(name)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_radio);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
        notificationManager.notify(id,
                notificationBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "OnStartService");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "OnBindService");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (userListener != null) {
            userListener.remove();
        }
        if (messageListner != null) {
            messageListner.remove();
        }
        if (messageReadListner != null) {
            messageReadListner.remove();
        }
        Log.d(TAG, "OnDestroyService");
    }

    public class LocalBinder extends Binder {
        public ChannelChatService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ChannelChatService.this;
        }
    }
}
