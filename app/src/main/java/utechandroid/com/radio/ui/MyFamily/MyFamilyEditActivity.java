package utechandroid.com.radio.ui.MyFamily;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.prudenttechno.radioNotification.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utechandroid.com.radio.data.api.apihelper.ApiConfig;
import utechandroid.com.radio.data.api.apihelper.AppConfig;
import utechandroid.com.radio.data.api.model.PhoneNoSearchRequest;
import utechandroid.com.radio.data.api.model.SaveUpdateContactRequest;
import utechandroid.com.radio.data.api.model.SaveUpdateContactResponse;
import utechandroid.com.radio.data.api.model.TableEditResponse;
import utechandroid.com.radio.data.pref.SessionManager;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class MyFamilyEditActivity extends ColorsAppCompatActivity {

    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_address)
    EditText etAddress;
    @BindView(R.id.et_area)
    EditText etArea;
    @BindView(R.id.et_residence_no)
    EditText etResidenceNo;
    @BindView(R.id.et_office_no)
    EditText etOfficeNo;
    @BindView(R.id.et_mobile_no)
    EditText etMobileNo;
    @BindView(R.id.et_occupation)
    EditText etOccupation;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.et_house_no)
    EditText etHouseNo;
    @BindView(R.id.et_city)
    EditText etCity;
    @BindView(R.id.et_state)
    EditText etState;
    @BindView(R.id.et_country)
    EditText etCountry;
    @BindView(R.id.et_pincode)
    EditText etPincode;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_relation)
    EditText etRelation;
    //  private String partyname;

    private ProgressDialog dialog;

    private String address;
    private int id;
    private String occupation;
    private String Name;
    private String mobile;
    private String office;
    private String residence;
    private String houseNo, city, countryCode, pincode, state;
    private String email;
    private String area;
    private int partyId;
    private String relation;
    private SessionManager sessionManager;
    private String id_no;
    private String party_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_family_edit);
        sessionManager = new SessionManager(this);
        ButterKnife.bind(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading");
        if (getIntent() != null) {
            //  partyname = getIntent().getStringExtra("party_name");
            id = getIntent().getIntExtra("party_id", 0);
            party_id = String.valueOf(id);
            String number = String.valueOf(id);
            LoadContacts(number, "EditId");
        } else {
            Toast.makeText(this, "no data", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_save)
    public void onViewClicked() {
        SaveContactDetail();
    }

    private void ShowProgress() {
        dialog.show();

    }

    private void HideProgress() {
        dialog.dismiss();
    }

    public void LoadContacts(String s, String id) {
        ShowProgress();
        ApiConfig getResponse = AppConfig.ApiClient().create(ApiConfig.class);
        Call<TableEditResponse> call = getResponse.doEditInformation(new PhoneNoSearchRequest(s, id));
        call.enqueue(new Callback<TableEditResponse>() {
            @Override
            public void onResponse(Call<TableEditResponse> call, Response<TableEditResponse> response) {
                HideProgress();
                TableEditResponse serverResponse = response.body();
                if (response.isSuccessful()) {
                    if (serverResponse.getTable().size() > 0) {
                        partyId = serverResponse.getTable().get(0).getId();
                        id_no = String.valueOf(partyId);
                        relation = serverResponse.getTable().get(0).getRelation();
                        address = serverResponse.getTable().get(0).getAddress();
                        houseNo = serverResponse.getTable().get(0).getHouseNo();
                        city = serverResponse.getTable().get(0).getCity();
                        state = serverResponse.getTable().get(0).getState();
                        countryCode = serverResponse.getTable().get(0).getCountryCode();
                        pincode = serverResponse.getTable().get(0).getPincode();
                        relation = serverResponse.getTable().get(0).getRelation();
                        Name = serverResponse.getTable().get(0).getPartyName();
                        occupation = serverResponse.getTable().get(0).getOccupation();
                        mobile = serverResponse.getTable().get(0).getMPhoneNo();
                        residence = serverResponse.getTable().get(0).getRPhoneNo();
                        office = serverResponse.getTable().get(0).getOPhoneNo();
                        area = serverResponse.getTable().get(0).getArea();
                        email = serverResponse.getTable().get(0).getEmail();

                        //


                        etName.setText(Name);
                        etAddress.setText(address);
                        etHouseNo.setText(houseNo);
                        etCity.setText(city);
                        etState.setText(state);
                        etCountry.setText(countryCode);
                        etPincode.setText(pincode);
                        etMobileNo.setText(mobile);
                        etOccupation.setText(occupation);
                        etOfficeNo.setText(office);
                        etResidenceNo.setText(residence);
                        etEmail.setText(email);
                        etArea.setText(area);
                        etRelation.setText(relation);
                        Log.e("Address", address);
                    } else {
                        Toast.makeText(getApplicationContext(), "" +
                                "No Data Available...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TableEditResponse> call, Throwable t) {
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

    public void SaveContactDetail() {
        Name = etName.getText().toString();
        relation = etRelation.getText().toString();
        houseNo = etHouseNo.getText().toString();
        address = etAddress.getText().toString();
        area = etArea.getText().toString();
        city = etCity.getText().toString();
        state = etState.getText().toString();
        countryCode = etCountry.getText().toString();
        pincode = etPincode.getText().toString();
        residence = etResidenceNo.getText().toString();
        office = etOfficeNo.getText().toString();
        email = etEmail.getText().toString();
        occupation = etOccupation.getText().toString();

        ShowProgress();
        ApiConfig getResponse = AppConfig.ApiClient().create(ApiConfig.class);
        Call<SaveUpdateContactResponse> call = getResponse.saveUpdate(new SaveUpdateContactRequest(party_id, Name, relation, houseNo, address,
                area, city, state, countryCode, pincode,
                residence, office, mobile, email, occupation, sessionManager.GetUserEmail()));
        call.enqueue(new Callback<SaveUpdateContactResponse>() {
            @Override
            public void onResponse(Call<SaveUpdateContactResponse> call, Response<SaveUpdateContactResponse> response) {
                HideProgress();
                SaveUpdateContactResponse serverResponse = response.body();
                if (response.isSuccessful()) {
                    if (serverResponse.getTable().size() > 0) {
                        Toast.makeText(MyFamilyEditActivity.this, "" + serverResponse.getTable().get(0).getMsg(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "" +
                                "No Data Available...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SaveUpdateContactResponse> call, Throwable t) {
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
}
