package utechandroid.com.radio.service.UserService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
import java.util.concurrent.atomic.AtomicInteger;


import utechandroid.com.radio.data.db.helper.RealmHelper;
import utechandroid.com.radio.data.db.model.Notification.ChannelNotification;
import utechandroid.com.radio.firestore.helper.auth.FireStoreAuthHelper;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.ChannelData;
import utechandroid.com.radio.firestore.model.Message;
import utechandroid.com.radio.firestore.model.MessageReadData;
import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.ui.home.HomeActivity;

/**
 * Created by Dharmik Patel on 30-Nov-17.
 */

public class UserService extends Service {

    private static String TAG = "UserService";
    public final IBinder mBinder = new LocalBinder();

    private FirebaseFirestore db;
    private ListenerRegistration userListener, messageListener, messageReadListener;
    private final int NOTIFICATION_ID = 237;
    NotificationManager nManager;

    public UserService() {
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
                        SaveChannels(set);
                    }
                    if (user.getCreatedChannels().entrySet().size() > 0) {
                        List<String> set = new ArrayList<>();
                        for (Map.Entry<String, Object> f : user.getCreatedChannels().entrySet()) {
                            String key = f.getKey();
                            set.add(key);
                        }
                        SaveChannels(set);
                    }
                }
            });
        }
    }

    private final static AtomicInteger c = new AtomicInteger(0);

    public static int getID() {
        return c.incrementAndGet();
    }

    private void SaveChannels(List<String> stringList) {
        for (int i = 0; i < stringList.size(); i++) {
            new RealmHelper(getApplicationContext()).SaveChannel(new ChannelNotification(getID(), stringList.get(i), true));
        }
        LoadNotificationMessages(stringList);
    }

    private void LoadNotificationMessages(List<String> stringList) {
        for (int i = 0; i < stringList.size(); i++) {
            Query query = db.collection(Message.FIELD_COLLECTION)
                    .document(stringList.get(i))
                    .collection(Message.FIELD_COLLECTION);

            messageListener = query.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    return;
                }
                if (snapshot != null && !snapshot.isEmpty()) {
                    for (DocumentChange change : snapshot.getDocumentChanges()) {
                        switch (change.getType()) {
                            case ADDED:
                                DocumentSnapshot doc = change.getDocument();
                                final Message messageList = doc.toObject(Message.class);
                                UnReadMessages(messageList.getMessageChannel());
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

    private void UnReadMessages(String ChannelId) {
        CollectionReference collectionReference = new FireStoreHelper().GetMessageReadCollection(ChannelId);
        collectionReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> messageList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            MessageReadData user = document.toObject(MessageReadData.class);
                            if (new RealmHelper(getApplicationContext()).CheckChannelNotification(user.getMessage().getMessageChannel())) {
                                if (user.getMembers().entrySet().size() > 0) {
                                    for (Map.Entry<String, Integer> f : user.getMembers().entrySet()) {
                                        String key = f.getKey();
                                        if (key.equals(new FireStoreAuthHelper().GetUserId())) {
                                            Integer value = f.getValue();
                                            if (value == 1 || value == 2) {
                                                if (user.getMessage().getMessageType()==1){
                                                    final Map<String, Object> data = user.getMessage().getData();
                                                    messageList.add(data.get(Message.FIELD_MESSAGE_DATA_MESSAGE).toString());
                                                }else if (user.getMessage().getMessageType()==2){
                                                    messageList.add("Image");
                                                }else if (user.getMessage().getMessageType()==5){
                                                    messageList.add("Location");
                                                }else if (user.getMessage().getMessageType()==3){
                                                    messageList.add("Document");
                                                }

                                                WriteBatch batch = db.batch();
                                                DocumentReference radioRef = document.getReference();
                                                Map<String, Object> radioUpdates = new HashMap<>();
                                                radioUpdates.put("members" + "." + new FireStoreAuthHelper().GetUserId(), 2);
                                                batch.update(radioRef, radioUpdates);
                                                batch.commit();
                                            }
                                        } else {
                                            // nManager.cancel(new RealmHelper(getApplicationContext()).GetChannelNotificationId(ChannelId));
                                        }
                                    }
                                }
                            }
                        }
                        createNotify(messageList, new RealmHelper(getApplicationContext()).GetChannelNotificationId(ChannelId), ChannelId);
                    }
                });
    }

    public void createNotify(List<String> messageList, int cId, String channelId) {
        if (messageList.size() > 0) {
            DocumentReference docRef = new FireStoreHelper(this).GetChannelByIdDocument(channelId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                ChannelData channel = documentSnapshot.toObject(ChannelData.class);

                String channelName = channel.getChannelName();

                Intent activityIntent = new Intent(this, HomeActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_radio);

                nManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(this);
                String title = messageList.size() == 1 ? messageList.size() + " New Message" : messageList.size() + " New Messages";
                builder.setVibrate(new long[]{1000, 1000})
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setGroup(channelId)
                        .setLargeIcon(bitmap)
                        .setContentIntent(pendingIntent)
                        .setContentTitle(title)
                        .setContentText(channelName)
                        .setGroupSummary(true)
                        .setSmallIcon(R.mipmap.ic_launcher_radio)
                        .setAutoCancel(true);
                if (messageList.size() > 0) {
                    for (int i = 0; i < messageList.size(); i++) {
                        inboxStyle.addLine(messageList.get(i));
                    }
                }
                builder.setStyle(inboxStyle);
                nManager.notify(channelId, cId, builder.build());

            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        if (messageListener != null) {
            messageListener.remove();
        }
        if (messageReadListener != null) {
            messageReadListener.remove();
        }

        Log.d(TAG, "OnDestroyService");
    }

    public class LocalBinder extends Binder {
        public UserService getService() {
            // Return this instance of LocalService so clients can call public methods
            return UserService.this;
        }
    }
}