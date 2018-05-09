package utechandroid.com.radio.ui.CreateRadio;


import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.farbod.labelledspinner.LabelledSpinner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.prudenttechno.radioNotification.R;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import utechandroid.com.radio.firestore.helper.auth.FireStoreAuthHelper;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.Category;
import utechandroid.com.radio.firestore.model.Radio;
import utechandroid.com.radio.firestore.model.RadioData;
import utechandroid.com.radio.util.GenerateRandomColor;
import utechandroid.com.radio.util.fullScreenFragmentDialog.FullScreenDialogContent;
import utechandroid.com.radio.util.fullScreenFragmentDialog.FullScreenDialogController;


public class CreateRadioFragment extends Fragment implements FullScreenDialogContent {

    @BindView(R.id.edit_create_radio_name)
    EditText editCreateRadioName;
    @BindView(R.id.txtlyt_create_radio_name)
    TextInputLayout txtlytCreateRadioName;
    @BindView(R.id.edt_create_radio_description)
    EditText edtCreateRadioDescription;
    @BindView(R.id.txtlyt_create_radio_description)
    TextInputLayout txtlytCreateRadioDescription;
    @BindView(R.id.spinner_channel_category_add)
    LabelledSpinner spinnerChannelCategoryAdd;
    @BindView(R.id.progressBar_create_radio)
    ProgressBar progressBarCreateRadio;
    @BindView(R.id.layout_create_radio)
    LinearLayout layoutCreateRadio;

    Unbinder unbinder;
    private FirebaseFirestore db;
    public static final String EXTRA_NAME = "EXTRA_NAME";
    private String category;
    private FullScreenDialogController dialogController;

    public CreateRadioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_radio, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        spinnerChannelCategoryAdd.setLabelText("Select Category");
        GetListCategory();
    }

    private void GetListCategory() {
        ProgressShow();
        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ProgressHide();
                        if (task.isSuccessful()) {
                            final List<String> categoryList = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Category category = document.toObject(Category.class);
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
            String radioName = editCreateRadioName.getText().toString();
            String radioDesc = edtCreateRadioDescription.getText().toString();
            CreateRadio(radioName, radioDesc, category);
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void ProgressShow() {
        if (progressBarCreateRadio != null || layoutCreateRadio != null) {
            progressBarCreateRadio.setVisibility(View.VISIBLE);
            layoutCreateRadio.setVisibility(View.INVISIBLE);
        }
    }

    public void ProgressHide() {
        if (progressBarCreateRadio != null || layoutCreateRadio != null) {
            progressBarCreateRadio.setVisibility(View.GONE);
            layoutCreateRadio.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = editCreateRadioName.getText().toString();
        if (TextUtils.isEmpty(email)) {
            txtlytCreateRadioName.setError("Radio Name can't be empty.");
            valid = false;
        }

        String password = edtCreateRadioDescription.getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtlytCreateRadioDescription.setError("Radio Name can't be empty.");
            valid = false;
        }

        return valid;
    }

    private void CreateRadio(String radioName, String radioDesc, String radioCategory) {
        ProgressShow();

        WriteBatch batch = db.batch();

        final DocumentReference radioRef = new FireStoreHelper(getActivity()).GetRadioDocument();
        String radioId = radioRef.getId();
        RadioData radio = new RadioData();
        radio.setRadioId(radioId);
        radio.setRadioName(radioName);
        radio.setRadioDescription(radioDesc);
        radio.setRadioCategoryId(radioCategory);
        radio.setRadioCreatedDate(new Date());
        radio.setRadioUserId(new FireStoreAuthHelper().GetUserId());
        radio.setRadioColor(new GenerateRandomColor(getContext()).getColor("400"));
        batch.set(radioRef, radio);

        Map<String, Object> docData = new HashMap<>();
        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put(radioId, true);
        docData.put(Radio.FIELD_COLLECTION, nestedData);

        DocumentReference userRef = new FireStoreHelper(getActivity()).GetCurrentUserDocument();
        batch.set(userRef, docData, SetOptions.merge());

        DocumentReference subcategoryRef = new FireStoreHelper(getActivity()).GetCategoriesDocument(radioCategory);
        Map<String, Object> subcategoryData = new HashMap<>();
        Map<String, Object> subcategorynestedData = new HashMap<>();
        subcategorynestedData.put(radioId, true);
        subcategoryData.put(RadioData.FIELD_COLLECTION, subcategorynestedData);
        batch.set(subcategoryRef, subcategoryData, SetOptions.merge());

        batch.commit().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Radio Successfully Created", Toast.LENGTH_SHORT).show();
                dialogController.discard();
            } else {
                Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
