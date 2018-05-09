package utechandroid.com.radio.ui.SearchContactDirectory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.prudenttechno.radioNotification.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utechandroid.com.radio.data.api.model.TableItem;
import utechandroid.com.radio.events.Contactevent;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class SearchContactDetailActivity extends ColorsAppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_contact_name)
    TextView txtContactName;
    @BindView(R.id.txt_contact_residence_address)
    TextView txtContactResidenceAddress;
    @BindView(R.id.map_contact_residence_address)
    Button mapContactResidenceAddress;
    @BindView(R.id.txt_contact_residence_no)
    TextView txtContactResidenceNo;
    @BindView(R.id.txt_contact_office_no)
    TextView txtContactOfficeNo;
    @BindView(R.id.txt_contact_mobile_no)
    TextView txtContactMobileNo;
    @BindView(R.id.txt_contact_email)
    TextView txtContactEmail;
    @BindView(R.id.txt_contact_occupation)
    TextView txtContactOccupation;
    @BindView(R.id.add_to_contact)
    Button addToContact;

    private TableItem tableItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        addToContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertContact(txtContactName.getText().toString(), txtContactMobileNo.getText().toString(),
                        txtContactEmail.getText().toString(), txtContactOccupation.getText().toString(),
                        txtContactResidenceAddress.getText().toString(), txtContactOfficeNo.getText().toString(),
                        txtContactResidenceNo.getText().toString()

                );
            }
        });

    }

    @OnClick(R.id.map_contact_residence_address)
    public void onViewClicked() {
        String map = "http://maps.google.co.in/maps?q=" + tableItem.getAddress();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        startActivity(intent);
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void OnRadioEvent(Contactevent contactevent) {
        tableItem = contactevent.getTableItem();
        txtContactName.setText(tableItem.getPartyName());
        txtContactEmail.setText(tableItem.getEmail());
        txtContactMobileNo.setText(tableItem.getMobile());
        txtContactOccupation.setText(tableItem.getOccupation());
        txtContactOfficeNo.setText(tableItem.getOffice());
        txtContactResidenceAddress.setText(tableItem.getAddress());
        txtContactResidenceNo.setText(tableItem.getResidence());
    }

    public void insertContact(String name, String phone, String email, String Occupation, String address, String officeNo, String homeNo) {
        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
        intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, Occupation);
        intent.putExtra(ContactsContract.Intents.Insert.POSTAL, address);
        intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, officeNo);
        intent.putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE, homeNo);
        intent.putExtra("finishActivityOnSaveCompleted", true);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
}
