package utechandroid.com.radio.ui.CreateChannel;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.farbod.labelledspinner.LabelledSpinner;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.prudenttechno.radioNotification.R;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import utechandroid.com.radio.events.RadioEvent;
import utechandroid.com.radio.firestore.helper.auth.FireStoreAuthHelper;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.ChannelData;
import utechandroid.com.radio.firestore.model.RadioData;
import utechandroid.com.radio.firestore.model.SubCategory;
import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.util.GenerateRandomColor;
import utechandroid.com.radio.util.fullScreenFragmentDialog.FullScreenDialogContent;
import utechandroid.com.radio.util.fullScreenFragmentDialog.FullScreenDialogController;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateChannelFragment extends Fragment implements FullScreenDialogContent {

    @BindView(R.id.edit_channel_radio_name)
    EditText editChannelRadioName;
    @BindView(R.id.txtlyt_create_channel_name)
    TextInputLayout txtlytCreateChannelName;
    @BindView(R.id.edt_create_channel_description)
    EditText edtCreateChannelDescription;
    @BindView(R.id.txtlyt_create_channel_description)
    TextInputLayout txtlytCreateChannelDescription;
    @BindView(R.id.chk_create_channel_private)
    CheckBox chkCreateChannelPrivate;
    @BindView(R.id.spinner_channel_category_add)
    LabelledSpinner spinnerChannelCategoryAdd;
    @BindView(R.id.layout_create_channel)
    LinearLayout layoutCreateChannel;
    @BindView(R.id.progressBar_create_channel)
    ProgressBar progressBarCreateChannel;
    Unbinder unbinder;

    private FirebaseFirestore db;
    public static final String RADIOCATEGORY = "RADIO_CATEGORY";
    public static final String RADIOID = "RADIO_ID";
    private String category;
    private FullScreenDialogController dialogController;
    private String mRadioCategory, mRadioId;

    public CreateChannelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_channel, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        spinnerChannelCategoryAdd.setLabelText("Select Category");
        //  GetListCategory();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void OnRadioEvent(RadioEvent radioEvent) {
        final RadioData radio = radioEvent.getmRadio();
        mRadioId = radio.getRadioId();
        mRadioCategory = radio.getRadioCategoryId();

        GetListCategory();
    }

    private void GetListCategory() {
        ProgressShow();
        db.collection(SubCategory.FIELD_COLLECTION)
                .document(mRadioCategory)
                .collection("SubCategories")
                .get()
                .addOnCompleteListener(task -> {
                    ProgressHide();
                    if (task.isSuccessful()) {
                        Log.e("lidtCa", "");
                        final List<String> categoryList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            SubCategory category = document.toObject(SubCategory.class);
                            categoryList.add(category.getName());
                        }
                        spinnerChannelCategoryAdd.setItemsArray(categoryList);
                        spinnerChannelCategoryAdd.setOnItemChosenListener(new LabelledSpinner.OnItemChosenListener() {
                            @Override
                            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                                category = adapterView.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

                            }
                        });

                    } else {
                        Log.e("datafield", "Error getting documents: ", task.getException());
                    }
                });
    }

    @Override
    public void onDialogCreated(FullScreenDialogController dialogController) {
        this.dialogController = dialogController;
    }

    @Override
    public boolean onConfirmClick(FullScreenDialogController dialogController) {
        return true;
    }

    @Override
    public boolean onDiscardClick(FullScreenDialogController dialogController) {
        return false;
    }

    @Override
    public boolean onExtraActionClick(MenuItem actionItem, FullScreenDialogController dialogController) {
        if (validateForm()) {
            Boolean flag = false;
            if (chkCreateChannelPrivate.isChecked()) {
                flag = true;
            }
            String channelName = editChannelRadioName.getText().toString();
            String channelDesc = edtCreateChannelDescription.getText().toString();
            CreateRadio(channelName, channelDesc, flag, category);
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void ProgressShow() {
        if (progressBarCreateChannel != null || layoutCreateChannel != null) {
            progressBarCreateChannel.setVisibility(View.VISIBLE);
            layoutCreateChannel.setVisibility(View.INVISIBLE);
        }
    }

    public void ProgressHide() {
        if (progressBarCreateChannel != null || layoutCreateChannel != null) {
            progressBarCreateChannel.setVisibility(View.GONE);
            layoutCreateChannel.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = editChannelRadioName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            txtlytCreateChannelName.setError("Channel Name can't be empty.");
            valid = false;
        }

        String desc = edtCreateChannelDescription.getText().toString();
        if (TextUtils.isEmpty(desc)) {
            txtlytCreateChannelDescription.setError("Channel Description can't be empty.");
            valid = false;
        }

        return valid;
    }

    private void CreateRadio(String radioName, String radioDesc, Boolean flag, String radioCategory) {
        ProgressShow();

        WriteBatch batch = db.batch();

        final DocumentReference channelRef = new FireStoreHelper(getActivity())
                .GetChannelDocument();
        String channelId = channelRef.getId();
        ChannelData channel = new ChannelData();
        channel.setChannelId(channelId);
        channel.setChannelSubCategoryId(radioCategory);
        channel.setChannelRadioId(mRadioId);
        channel.setChannelName(radioName);
        channel.setChannelPrivate(flag);
        channel.setChannelDescription(radioDesc);
        channel.setChannelCreatedDate(new Date());
        channel.setChannelAdminId(new FireStoreAuthHelper().GetUserId());
        channel.setChannelColor(new GenerateRandomColor(getContext()).getColor("400"));
        batch.set(channelRef, channel);

        DocumentReference userRef = new FireStoreHelper(getActivity()).GetCurrentUserDocument();
        Map<String, Object> docData = new HashMap<>();
        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put(channelId, true);
        docData.put(User.FIELD_CREATED_COLLECTION, nestedData);
        batch.set(userRef, docData, SetOptions.merge());

        DocumentReference radioRef = new FireStoreHelper(getActivity()).GetRadioByIdDocument(mRadioId);
        Map<String, Object> radioData = new HashMap<>();
        Map<String, Object> radionestedData = new HashMap<>();
        radionestedData.put(channelId, true);
        radioData.put(ChannelData.FIELD_COLLECTION, radionestedData);
        batch.set(radioRef, radioData, SetOptions.merge());

        DocumentReference subcategoryRef = new FireStoreHelper(getActivity()).GetSubCategoriesDocument(mRadioCategory, radioCategory);
        Map<String, Object> subcategoryData = new HashMap<>();
        Map<String, Object> subcategorynestedData = new HashMap<>();
        subcategorynestedData.put(channelId, true);
        subcategoryData.put(ChannelData.FIELD_COLLECTION, subcategorynestedData);
        batch.set(subcategoryRef, subcategoryData, SetOptions.merge());

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Channel Successfully Created", Toast.LENGTH_SHORT).show();
                dialogController.discard();
            } else {
                Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
