package utechandroid.com.radio.data.db.helper;

import android.content.Context;

import java.util.List;

import io.realm.Realm;
import utechandroid.com.radio.data.db.model.Notification.ChannelNotification;

/**
 * Created by Utsav Shah on 30-Oct-17.
 */

public class RealmHelper {

    private Context mContext;
    private Realm mRealm;

    public RealmHelper(Context context) {
        mContext = context;
        mRealm = Realm.getDefaultInstance();
    }

    public void SaveChannel(ChannelNotification channelNotification) {
        ChannelNotification object = mRealm.where(ChannelNotification.class)
                .equalTo("channelId", channelNotification.getChannelId())
                .findFirst();
        if (object == null) {
            mRealm.beginTransaction();
            ChannelNotification realmUser = mRealm.copyToRealm(channelNotification);
            mRealm.commitTransaction();
        } else {
            //handle object already existing
        }
       /* if (GetQueAns().size()<12){
            mRealm.beginTransaction();
            Questions realmUser = mRealm.copyToRealm(questions);
            mRealm.commitTransaction();
        }*/
    }

    public void ClearAll(){
        mRealm.executeTransaction(realm -> realm.delete(ChannelNotification.class));
    }

    public void UpdateNotificationChannel(Boolean flag,String channelId) {
        ChannelNotification object = mRealm.where(ChannelNotification.class)
                .equalTo("channelId", channelId)
                .findFirst();
        if (object != null) {
            mRealm.beginTransaction();
            object.setNotification(flag);
            mRealm.commitTransaction();
        }
    }

    public List<ChannelNotification> GetSubscribeChannels() {
        return mRealm.where(ChannelNotification.class).findAll();
    }

    public List<ChannelNotification> GetSubscribeNotificationChannels() {
        return mRealm.where(ChannelNotification.class)
                .equalTo("Notification", true)
                .findAll();
    }

    public Boolean CheckChannelNotification(String channelId){
        if (GetSubscribeChannels().size()>0){
            ChannelNotification channelNotification = mRealm.where(ChannelNotification.class)
                    .equalTo("channelId", channelId)
                    .findFirst();
            if (channelNotification != null) {
                return channelNotification.getNotification();
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    public int GetChannelNotificationId(String channelId){
            ChannelNotification channelNotification = mRealm.where(ChannelNotification.class)
                    .equalTo("channelId", channelId)
                    .findFirst();
            if (channelNotification != null){
                return channelNotification.getId();
            }else {
                return -22;
            }
    }
}
