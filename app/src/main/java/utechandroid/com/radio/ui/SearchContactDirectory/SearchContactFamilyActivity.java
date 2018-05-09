package utechandroid.com.radio.ui.SearchContactDirectory;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.prudenttechno.radioNotification.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utechandroid.com.radio.adapter.SearchContactAdapter;
import utechandroid.com.radio.data.api.apihelper.ApiConfig;
import utechandroid.com.radio.data.api.apihelper.AppConfig;
import utechandroid.com.radio.data.api.model.PhoneNoSearchRequest;
import utechandroid.com.radio.data.api.model.PhoneNoSearchResponse;
import utechandroid.com.radio.data.api.model.TableItem;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;
import utechandroid.com.radio.util.ItemDecoration.DividerItemDecoration;

public class SearchContactFamilyActivity extends ColorsAppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView_search)
    RecyclerView recyclerViewSearch;
    @BindView(R.id.progress_search)
    ProgressBar progressSearch;
    @BindView(R.id.edt_search_contact_filter)
    EditText edtSearchContactFilter;
    @BindView(R.id.cardView_filter)
    CardView cardViewFilter;
    @BindView(R.id.cardView_recyclerview)
    CardView cardViewRecyclerview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact_family);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        SetRecyclerView();

        String id = getIntent().getExtras().getString("id");
        LoadContacts(id);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                /*if (mDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }*/
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void SetAdapter(List<TableItem> tableItemList) {
        SearchContactAdapter searchContactAdapter = new SearchContactAdapter(this, tableItemList, true);
        recyclerViewSearch.setAdapter(searchContactAdapter);

        edtSearchContactFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchContactAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void SetRecyclerView() {
        recyclerViewSearch.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewSearch.setLayoutManager(linearLayoutManager);
        recyclerViewSearch.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));
    }

    private void ShowProgress() {
        cardViewFilter.setVisibility(View.INVISIBLE);
        cardViewRecyclerview.setVisibility(View.INVISIBLE);
        progressSearch.setVisibility(View.VISIBLE);
    }

    private void HideProgress() {
        progressSearch.setVisibility(View.INVISIBLE);
        cardViewFilter.setVisibility(View.VISIBLE);
        cardViewRecyclerview.setVisibility(View.VISIBLE);
    }

    public void LoadContacts(String s) {
        ShowProgress();
        ApiConfig getResponse = AppConfig.ApiClient().create(ApiConfig.class);
        Call<PhoneNoSearchResponse> call = getResponse.doSearch(new PhoneNoSearchRequest(s, "Family"));
        call.enqueue(new Callback<PhoneNoSearchResponse>() {
            @Override
            public void onResponse(Call<PhoneNoSearchResponse> call, Response<PhoneNoSearchResponse> response) {
                HideProgress();
                PhoneNoSearchResponse serverResponse = response.body();
                if (response.isSuccessful()) {
                    if (serverResponse.getTable().size() > 0) {
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
            public void onFailure(Call<PhoneNoSearchResponse> call, Throwable t) {
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

    @Override
    protected void onPause() {
        super.onPause();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.onBackPressed();
    }

}
