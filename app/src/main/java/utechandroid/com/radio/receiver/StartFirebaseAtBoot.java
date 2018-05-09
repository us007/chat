package utechandroid.com.radio.receiver;

/**
 * Created by Utsav Shah on 30-Nov-17.
 */

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import utechandroid.com.radio.service.ServiceUtils;
import utechandroid.com.radio.service.UserService.UserService;

/**
 * Start the service when the device boots.
 *
 * @author vikrum
 *
 */
public class StartFirebaseAtBoot extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        ServiceUtils.startServiceFriendChat(context);
    }
}
