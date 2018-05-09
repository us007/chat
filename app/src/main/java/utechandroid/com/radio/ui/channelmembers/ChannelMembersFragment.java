package utechandroid.com.radio.ui.channelmembers;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.prudenttechno.radioNotification.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import utechandroid.com.radio.adapter.ChannelMemberAdapter;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.MembersData;
import utechandroid.com.radio.ui.userprofile.inChannel.UserProfileChannelActivity;
import utechandroid.com.radio.util.ItemDecoration.DividerItemDecoration;

public class ChannelMembersFragment extends Fragment implements ChannelMemberAdapter.OnMyChannelSelectedListener {

    @BindView(R.id.recyclerView_channel_members)
    RecyclerView recyclerViewChannelMembers;
    Unbinder unbinder;
    @BindView(R.id.view_empty_members)
    LinearLayout viewEmptyMembers;

    private String channelId;
    private ChannelMemberAdapter channelMemberAdapter;

    public ChannelMembersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_channel_members, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle mArgs = getArguments();
        channelId = mArgs.getString("id");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetUpRecyclerView();
        LoadMyRadios();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void SetUpRecyclerView() {
        recyclerViewChannelMembers.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewChannelMembers.setLayoutManager(layoutManager);
        recyclerViewChannelMembers.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
    }

    private void LoadMyRadios() {
        Query mQuery = new FireStoreHelper().GetMemberCollection(channelId)
                .whereEqualTo(MembersData.FIELD_MEMBER_STATUS, 2);

        channelMemberAdapter = new ChannelMemberAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    viewEmptyMembers.setVisibility(View.VISIBLE);
                    recyclerViewChannelMembers.setVisibility(View.GONE);
                } else {
                    viewEmptyMembers.setVisibility(View.GONE);
                    recyclerViewChannelMembers.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors

            }
        };
        recyclerViewChannelMembers.setAdapter(channelMemberAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (channelMemberAdapter != null) {
            channelMemberAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (channelMemberAdapter != null) {
            channelMemberAdapter.stopListening();
        }
    }

    @Override
    public void onClick(String memberId, String userId) {
        Intent intent = new Intent(getContext(), UserProfileChannelActivity.class);
        intent.putExtra("channelId", memberId);
        intent.putExtra("userId", userId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }
}
