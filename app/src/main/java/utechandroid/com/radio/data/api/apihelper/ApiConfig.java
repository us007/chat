package utechandroid.com.radio.data.api.apihelper;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import utechandroid.com.radio.data.api.model.EditPhoneContactRequest;
import utechandroid.com.radio.data.api.model.EditPhoneContactResponse;
import utechandroid.com.radio.data.api.model.Notification.request.NotificationGetDataRequest;
import utechandroid.com.radio.data.api.model.Notification.response.NotificationGetDataResponse;
import utechandroid.com.radio.data.api.model.PhoneNoSearchRequest;
import utechandroid.com.radio.data.api.model.PhoneNoSearchResponse;
import utechandroid.com.radio.data.api.model.SaveUpdateContactRequest;
import utechandroid.com.radio.data.api.model.SaveUpdateContactResponse;
import utechandroid.com.radio.data.api.model.TableEditResponse;

/**
 * Created by Utsav Shah on 20-Nov-17.
 */

public interface ApiConfig {
    // Login
    @POST("api/Search")
    Call<PhoneNoSearchResponse> doSearch(@Body PhoneNoSearchRequest body);

    @POST("api/Search")
    Call<EditPhoneContactResponse> doEdit(@Body EditPhoneContactRequest body);

    @POST("api/Search")
    Call<TableEditResponse> doEditInformation(@Body PhoneNoSearchRequest body);

    @POST("api/SavePhoneDirectory")
    Call<SaveUpdateContactResponse> saveUpdate(@Body SaveUpdateContactRequest body);

    @POST("api/GetRadioNotificationData")
    Call<NotificationGetDataResponse> getNotificationData(@Body NotificationGetDataRequest body);

}


