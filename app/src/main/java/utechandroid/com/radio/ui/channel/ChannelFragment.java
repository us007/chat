package utechandroid.com.radio.ui.channel;


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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.prudenttechno.radioNotification.R;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import utechandroid.com.radio.adapter.SubscribeChannelAdapter;
import utechandroid.com.radio.events.ChannelEvent;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.ChannelData;
import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.ui.message.MessageActivity;
import utechandroid.com.radio.util.ItemDecoration.PaddingItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChannelFragment extends Fragment implements SubscribeChannelAdapter.OnChannelSelectedListener {

    Unbinder unbinder;
    @BindView(R.id.recyclerView_channels)
    RecyclerView recyclerViewChannels;
    @BindView(R.id.pg_channels)
    ProgressBar pgChannels;
    @BindView(R.id.view_empty)
    LinearLayout viewEmpty;

    private FirebaseFirestore db;
    private SubscribeChannelAdapter subscribeChannelAdapter;
    private ListenerRegistration listenerRegistration;

    public ChannelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_channel, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();


        SetUpRecyclerView();
        LoadChannels();

    }

    private void SetUpRecyclerView() {
        recyclerViewChannels.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewChannels.setLayoutManager(layoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.paddingItemDecorationDefault);
        // int padding = (int) getResources().getDimension(R.dimen.paddingItemDecorationDefault);
        //    int paddingEdge = (int) getResources().getDimension(R.dimen.paddingItemDecorationDefault);
        recyclerViewChannels.addItemDecoration(new PaddingItemDecoration(spacingInPixels));

    }

    private void LoadChannels() {
        DocumentReference documentReference = new FireStoreHelper().GetCurrentUserDocument();
        listenerRegistration = documentReference.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                // Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                User user = snapshot.toObject(User.class);
                if (user.getSubscribeChannels().entrySet().size() > 0) {
                    List<String> set = new ArrayList<>();
                    for (Map.Entry<String, Integer> f : user.getSubscribeChannels().entrySet()) {
                        String key = f.getKey();
                        Integer value2 = f.getValue();
                        if (value2 == 2) {
                            set.add(key);
                        }
                    }
                    if (set.size() > 0) {
                        subscribeChannelAdapter = new SubscribeChannelAdapter(set, ChannelFragment.this);
                        recyclerViewChannels.setAdapter(subscribeChannelAdapter);

                        viewEmpty.setVisibility(View.GONE);
                        recyclerViewChannels.setVisibility(View.VISIBLE);
                        pgChannels.setVisibility(View.GONE);
                    } else {
                        recyclerViewChannels.setVisibility(View.GONE);
                        pgChannels.setVisibility(View.GONE);
                        viewEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerViewChannels.setVisibility(View.GONE);
                    pgChannels.setVisibility(View.GONE);
                    viewEmpty.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        LoadChannels();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            //  subscribeChannelAdapter.stopListening();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onChannelSelected(ChannelData channel, ImageView imageView) {
        EventBus.getDefault().postSticky(new ChannelEvent(channel));
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void showShimmer() {
        pgChannels.setVisibility(View.VISIBLE);
        recyclerViewChannels.setVisibility(View.INVISIBLE);
        //recyclerViewChannels.hideShimmerAdapter();
    }

    @Override
    public void hideShimmer() {
        pgChannels.setVisibility(View.GONE);
        recyclerViewChannels.setVisibility(View.VISIBLE);
        //recyclerViewChannels.showShimmerAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
