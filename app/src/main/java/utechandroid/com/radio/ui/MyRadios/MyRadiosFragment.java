package utechandroid.com.radio.ui.MyRadios;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.prudenttechno.radioNotification.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import utechandroid.com.radio.adapter.MyRadiosAdapter;
import utechandroid.com.radio.events.RadioEvent;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.RadioData;
import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.ui.channels.myChannels.MyChannelsActivity;
import utechandroid.com.radio.util.ItemDecoration.DividerItemDecoration;

public class MyRadiosFragment extends Fragment implements MyRadiosAdapter.OnMyRadioSelectedListener {

    @BindView(R.id.recyclerView_my_radio)
    RecyclerView recyclerViewMyRadio;
    Unbinder unbinder;
    @BindView(R.id.pg_radios)
    ProgressBar pgRadios;
    @BindView(R.id.view_empty)
    LinearLayout viewEmpty;
    private FirebaseFirestore db;
    private MyRadiosAdapter myRadiosAdapter;
    private ListenerRegistration registration;

    public MyRadiosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_radios, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        SetUpRecyclerView();
        LoadMyRadios();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void SetUpRecyclerView() {
        recyclerViewMyRadio.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewMyRadio.setLayoutManager(layoutManager);
        recyclerViewMyRadio.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
    }

    private void LoadMyRadios() {
        recyclerViewMyRadio.setVisibility(View.GONE);
        pgRadios.setVisibility(View.GONE);
        viewEmpty.setVisibility(View.VISIBLE);

        DocumentReference documentReference = new FireStoreHelper().GetCurrentUserDocument();
        registration = documentReference.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                // Log.w(TAG, "Listen failed.", e);
                return;
            }

            String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                    ? "Local" : "Server";

            if (snapshot != null && snapshot.exists()) {
                User user = snapshot.toObject(User.class);
                List<String> stringList = new ArrayList<>(user.getRadios().keySet());
                myRadiosAdapter = new MyRadiosAdapter(stringList, MyRadiosFragment.this);
                recyclerViewMyRadio.setAdapter(myRadiosAdapter);
            }

            if (myRadiosAdapter.getItemCount() == 0) {
                viewEmpty.setVisibility(View.VISIBLE);
                recyclerViewMyRadio.setVisibility(View.GONE);
                pgRadios.setVisibility(View.GONE);
            } else {
                viewEmpty.setVisibility(View.GONE);
                recyclerViewMyRadio.setVisibility(View.VISIBLE);
                pgRadios.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRadioSelected(RadioData radio, CircleImageView imageView) {
        EventBus.getDefault().postSticky(new RadioEvent(radio));
        Intent intent = new Intent(getContext(), MyChannelsActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LoadMyRadios();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registration != null) {
            registration.remove();
        }
    }

    @Override
    public void showShimmer() {
        pgRadios.setVisibility(View.VISIBLE);
        recyclerViewMyRadio.setVisibility(View.INVISIBLE);
        //recyclerViewChannels.hideShimmerAdapter();
    }

    @Override
    public void hideShimmer() {
        pgRadios.setVisibility(View.GONE);
        recyclerViewMyRadio.setVisibility(View.VISIBLE);
        //recyclerViewChannels.showShimmerAdapter();
    }

}
