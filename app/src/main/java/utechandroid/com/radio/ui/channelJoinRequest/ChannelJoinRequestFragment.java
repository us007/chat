package utechandroid.com.radio.ui.channelJoinRequest;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.prudenttechno.radioNotification.R;


import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import utechandroid.com.radio.adapter.ChannelJoinRequestAdapter;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.Channel;
import utechandroid.com.radio.firestore.model.ChannelData;
import utechandroid.com.radio.firestore.model.MembersData;
import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.util.ItemDecoration.DividerItemDecoration;

public class ChannelJoinRequestFragment extends Fragment implements ChannelJoinRequestAdapter.OnMyChannelSelectedListener {

    @BindView(R.id.recyclerView_channel_join_request)
    RecyclerView recyclerViewChannelJoinRequest;
    Unbinder unbinder;
    @BindView(R.id.view_empty_request)
    LinearLayout viewEmptyRequest;
    private FirebaseFirestore db;
    private ChannelJoinRequestAdapter channelJoinRequestAdapter;
    private String channelId;

    public ChannelJoinRequestFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_join_request, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle mArgs = getArguments();
        channelId = mArgs.getString("id");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
        recyclerViewChannelJoinRequest.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewChannelJoinRequest.setLayoutManager(layoutManager);
        recyclerViewChannelJoinRequest.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
    }

    private void LoadMyRadios() {
        Query mQuery = new FireStoreHelper().GetMemberCollection(channelId)
                .whereEqualTo(MembersData.FIELD_MEMBER_STATUS, 1);

        channelJoinRequestAdapter = new ChannelJoinRequestAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    viewEmptyRequest.setVisibility(View.VISIBLE);
                    recyclerViewChannelJoinRequest.setVisibility(View.GONE);
                } else {
                    viewEmptyRequest.setVisibility(View.GONE);
                    recyclerViewChannelJoinRequest.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors

            }
        };
        recyclerViewChannelJoinRequest.setAdapter(channelJoinRequestAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (channelJoinRequestAdapter != null) {
            channelJoinRequestAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (channelJoinRequestAdapter != null) {
            channelJoinRequestAdapter.stopListening();
        }
    }

    @Override
    public void onAccept(String memberId, String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Join Request");
        builder.setMessage("Allow/Reject member for channel");
        builder.setPositiveButton("ACCEPT",
                (dialog, which) -> {
                    Accept(memberId, userId);
                });
        builder.setNegativeButton("REJECT",
                (dialog, which) -> {
                    Reject(memberId, userId);
                });
        builder.show();
    }

    private void Accept(String memberId, String userId) {
        WriteBatch batch = db.batch();

        final DocumentReference memberRef = new FireStoreHelper(getActivity()).GetMemberDocument(channelId, memberId);
        batch.update(memberRef, MembersData.FIELD_MEMBER_STATUS, 2);
        batch.update(memberRef, MembersData.FIELD_MEMBER_JOINED_DATE, FieldValue.serverTimestamp());

        DocumentReference userRef = new FireStoreHelper(getActivity()).GetUserByDocument(userId);
        Map<String, Object> updates = new HashMap<>();
        updates.put(User.FIELD_SUBSCRIBE_COLLECTION + "." + channelId, 2);
        batch.update(userRef, updates);

        DocumentReference radioRef = db.collection(Channel.FIELD_COLLECTION).document(channelId);
        Map<String, Object> radioUpdates = new HashMap<>();
        radioUpdates.put(MembersData.FIELD_COLLECTION + "." + userId, 2);
        batch.update(radioRef, radioUpdates);

        DocumentReference channelPermissionRef = db.collection(Channel.FIELD_COLLECTION).document(channelId);
        Map<String, Object> channelPermissionData = new HashMap<>();
        Map<String, Boolean> channelPermissionnestedData = new HashMap<>();
        channelPermissionnestedData.put(userId, false);
        channelPermissionData.put(ChannelData.FIELD_COLLECTION_MEMBERS_WRITE_MESSAGE, channelPermissionnestedData);
        batch.set(channelPermissionRef, channelPermissionData, SetOptions.merge());

        batch.commit().addOnCompleteListener(task -> {
            //ProgressHide();
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Accept Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("errorrrr", "" + task.getException());
                //  Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Reject(String memberId, String userId) {
        WriteBatch batch = db.batch();

        DocumentReference userRef = new FireStoreHelper(getActivity()).GetUserByDocument(userId);
        Map<String, Object> updates = new HashMap<>();
        updates.put(User.FIELD_SUBSCRIBE_COLLECTION + "." + channelId, 3);
        batch.update(userRef, updates);

        DocumentReference radioRef = db.collection(Channel.FIELD_COLLECTION).document(channelId);
        Map<String, Object> radioUpdates = new HashMap<>();
        radioUpdates.put(MembersData.FIELD_COLLECTION + "." + userId, 3);
        batch.update(radioRef, radioUpdates);

        final DocumentReference memberRef = new FireStoreHelper(getActivity()).GetMemberDocument(channelId, memberId);
        batch.delete(memberRef);

        batch.commit().addOnCompleteListener(task -> {
            //ProgressHide();
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Reject Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("errorrrr", "" + task.getException());
                //  Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
