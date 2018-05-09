package utechandroid.com.radio.ui.joinChannel;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.prudenttechno.radioNotification.R;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import utechandroid.com.radio.firestore.helper.auth.FireStoreAuthHelper;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.Channel;
import utechandroid.com.radio.firestore.model.ChannelData;
import utechandroid.com.radio.firestore.model.MembersData;
import utechandroid.com.radio.firestore.model.User;

/**
 * Created by Dharmik Patel on 15-Nov-17.
 */
public class JoinChannelBottomSheet extends BottomSheetDialogFragment {

    @BindView(R.id.txt_channel_join_name)
    TextView txtChannelJoinName;
    @BindView(R.id.txt_channel_join_category)
    TextView txtChannelJoinCategory;
    @BindView(R.id.btn_channel_join)
    Button btnChannelJoin;
    @BindView(R.id.lyt_channel_join_details)
    LinearLayout lytChannelJoinDetails;
    @BindView(R.id.progressBar_channel_join)
    ProgressBar progressBarChannelJoin;
    Unbinder unbinder;

    private FirebaseFirestore db;
    private String id;
    private ChannelData channel;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.content_join_channel, null);
        dialog.setContentView(contentView);
        unbinder = ButterKnife.bind(this, contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        Bundle mArgs = getArguments();
        id = mArgs.getString("id");

        LoadChannel(id);

        return rootView;
    }

    private void LoadChannel(String id) {
        lytChannelJoinDetails.setVisibility(View.INVISIBLE);
        progressBarChannelJoin.setVisibility(View.VISIBLE);
        DocumentReference docRef = new FireStoreHelper(getActivity()).GetChannelByIdDocument(id);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            channel = documentSnapshot.toObject(ChannelData.class);
            txtChannelJoinName.setText("Channel Name : " + channel.getChannelName());
            txtChannelJoinCategory.setText("Channel Category : " + channel.getChannelSubCategoryId());
            if (channel.getChannelAdminId().equals(new FireStoreAuthHelper().GetUserId())) {
                //btnChannelJoin.setVisibility(View.GONE);
                dismiss();
                Toast.makeText(getActivity(), "You are Admin for this channel,So you can't join this channel. ", Toast.LENGTH_SHORT).show();
            }
            lytChannelJoinDetails.setVisibility(View.VISIBLE);
            progressBarChannelJoin.setVisibility(View.INVISIBLE);
        });
    }

    @OnClick(R.id.btn_channel_join)
    public void onViewClicked() {
        CheckSub();
    }

    private void CheckSub() {
        lytChannelJoinDetails.setVisibility(View.INVISIBLE);
        progressBarChannelJoin.setVisibility(View.VISIBLE);
        new FireStoreHelper().GetMemberCollection(id)
                .whereEqualTo(MembersData.FIELD_MEMBER_USER_ID,
                        new FireStoreAuthHelper().GetUserId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> snapshots = task.getResult().getDocuments();
                        if (snapshots.size() > 0) {
                            dismiss();
                       /*     lytChannelJoinDetails.setVisibility(View.VISIBLE);
                            progressBarChannelJoin.setVisibility(View.INVISIBLE);*/
                            Toast.makeText(getActivity(), "You already join this channel", Toast.LENGTH_SHORT).show();
                        } else {
                            Subscribe();
                        }
                    }
                });

    }

    private void Subscribe() {
        WriteBatch batch = db.batch();
        if (channel.getChannelPrivate()) {
            final DocumentReference memberRef = new FireStoreHelper(getActivity())
                    .GetMemberDocument(id);
            String memberId = memberRef.getId();
            MembersData members = new MembersData();
            members.setMemberId(memberId);
            members.setMemberChannelId(id);
            members.setMemberJoinedDate(new Date());
            members.setMemberRequestDate(new Date());
            members.setMemberRejectedDate(new Date());
            members.setMemberStatus(1);
            members.setMemberUserId(new FireStoreAuthHelper().GetUserId());
            batch.set(memberRef, members);

            DocumentReference userRef = new FireStoreHelper(getActivity()).GetCurrentUserDocument();
            Map<String, Object> docData = new HashMap<>();
            Map<String, Integer> nestedData = new HashMap<>();
            nestedData.put(id, 1);
            docData.put(User.FIELD_SUBSCRIBE_COLLECTION, nestedData);
            batch.set(userRef, docData, SetOptions.merge());

            DocumentReference radioRef = db.collection(Channel.FIELD_COLLECTION).document(id);
            Map<String, Object> radioData = new HashMap<>();
            Map<String, Integer> radionestedData = new HashMap<>();
            radionestedData.put(new FireStoreAuthHelper().GetUserId(), 1);
            radioData.put(MembersData.FIELD_COLLECTION, radionestedData);
            batch.set(radioRef, radioData, SetOptions.merge());
        } else {
            final DocumentReference memberRef = new FireStoreHelper(getActivity()).GetMemberDocument(id);
            String memberId = memberRef.getId();
            MembersData members = new MembersData();
            members.setMemberId(memberId);
            members.setMemberChannelId(id);
            members.setMemberJoinedDate(new Date());
            members.setMemberRequestDate(new Date());
            members.setMemberRejectedDate(new Date());
            members.setMemberStatus(2);
            members.setMemberUserId(new FireStoreAuthHelper().GetUserId());
            batch.set(memberRef, members);

            DocumentReference userRef = new FireStoreHelper(getActivity()).GetCurrentUserDocument();
            Map<String, Object> docData = new HashMap<>();
            Map<String, Integer> nestedData = new HashMap<>();
            nestedData.put(id, 2);
            docData.put(User.FIELD_SUBSCRIBE_COLLECTION, nestedData);
            batch.set(userRef, docData, SetOptions.merge());

            DocumentReference radioRef = db.collection(Channel.FIELD_COLLECTION).document(id);
            Map<String, Object> radioData = new HashMap<>();
            Map<String, Integer> radionestedData = new HashMap<>();
            radionestedData.put(new FireStoreAuthHelper().GetUserId(), 2);
            radioData.put(MembersData.FIELD_COLLECTION, radionestedData);
            batch.set(radioRef, radioData, SetOptions.merge());

            DocumentReference channelPermissionRef = db.collection(Channel.FIELD_COLLECTION).document(id);
            Map<String, Object> channelPermissionData = new HashMap<>();
            Map<String, Boolean> channelPermissionnestedData = new HashMap<>();
            channelPermissionnestedData.put(new FireStoreAuthHelper().GetUserId(), true);
            channelPermissionData.put(ChannelData.FIELD_COLLECTION_MEMBERS_WRITE_MESSAGE, channelPermissionnestedData);
            batch.set(channelPermissionRef, channelPermissionData, SetOptions.merge());
        }

        batch.commit().addOnCompleteListener(task -> {
            dismiss();
            /*lytChannelJoinDetails.setVisibility(View.VISIBLE);
            progressBarChannelJoin.setVisibility(View.INVISIBLE);*/
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Subscribe Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
