package utechandroid.com.radio.ui.MyFamily;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.prudenttechno.radioNotification.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utechandroid.com.radio.adapter.EditContactDetailAdapter;
import utechandroid.com.radio.data.api.apihelper.ApiConfig;
import utechandroid.com.radio.data.api.apihelper.AppConfig;
import utechandroid.com.radio.data.api.model.EditPhoneContactRequest;
import utechandroid.com.radio.data.api.model.EditPhoneContactResponse;
import utechandroid.com.radio.data.pref.SessionManager;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;
import utechandroid.com.radio.util.ItemDecoration.DividerItemDecoration;

public class MyFamilyShowDetailActivity extends ColorsAppCompatActivity {

    private RecyclerView rvEditContact;
    private ProgressBar progressSearch;
    private SessionManager sessionManager;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_family_show_detail);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        sessionManager = new SessionManager(this);
        rvEditContact = findViewById(R.id.rv_edit_contact);
        progressSearch = findViewById(R.id.progress_search);
        SetRecyclerView();

        LoadContacts(sessionManager.GetUserEmail());
    }

    private void SetRecyclerView() {
        rvEditContact.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        rvEditContact.setLayoutManager(linearLayoutManager);
        rvEditContact.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));
    }


    private void ShowProgress() {

        progressSearch.setVisibility(View.VISIBLE);
    }

    private void HideProgress() {
        progressSearch.setVisibility(View.INVISIBLE);
    }

    public void LoadContacts(String s) {
        ShowProgress();
        ApiConfig getResponse = AppConfig.ApiClient().create(ApiConfig.class);
        Call<EditPhoneContactResponse> call = getResponse.doEdit(new EditPhoneContactRequest(s, "Email"));
        call.enqueue(new Callback<EditPhoneContactResponse>() {
            @Override
            public void onResponse(Call<EditPhoneContactResponse> call, Response<EditPhoneContactResponse> response) {
                HideProgress();
                EditPhoneContactResponse serverResponse = response.body();
                if (response.isSuccessful()) {
                    if (serverResponse.getTable().size() > 0) {
                        Log.e("test", "" + serverResponse.getTable());
                        SetAdapter(serverResponse.getTable());
                    } else {
                        Toast.makeText(getApplicationContext(), "" +
                                "No Data Available...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<EditPhoneContactResponse> call, Throwable t) {
                HideProgress();
                if (t instanceof ConnectException) {
                    Toast.makeText(getApplicationContext(), "Internet Error...", Toast.LENGTH_SHORT).show();
                } else if (t instanceof SocketTimeoutException) {
                    Toast.makeText(getApplicationContext(), "Timeout Error...", Toast.LENGTH_SHORT).show();
                } else {
                    // dashboardView.Error("Internet Error", "Please, Check your Internet Connection!");
                }
            }
        });
    }

    private void SetAdapter(List<EditPhoneContactResponse.Table> tableItemList) {
        EditContactDetailAdapter editContactDetailAdapter = new EditContactDetailAdapter(this, tableItemList);
        rvEditContact.setAdapter(editContactDetailAdapter);
    }
}
