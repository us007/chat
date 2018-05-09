package utechandroid.com.radio.adapter;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.prudenttechno.radioNotification.R;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.MembersData;
import utechandroid.com.radio.firestore.model.User;

/**
 * Created by Utsav Shah on 05-Dec-17.
 */
public class ChannelMemberAdapter extends FirestoreAdapter<ChannelMemberAdapter.ViewHolder> {

    public interface OnMyChannelSelectedListener {
        void onClick(String memberId, String userId);
    }

    private OnMyChannelSelectedListener mListener;

    public ChannelMemberAdapter(Query query, OnMyChannelSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.lyt_channel_join_request_user, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_image)
        CircleImageView profileImage;
        @BindView(R.id.tv_user_request_channel_join)
        AppCompatTextView tvUserRequestChannelJoin;
        @BindView(R.id.tv_date_request_channel_join)
        AppCompatTextView tvDateRequestChannelJoin;
        @BindView(R.id.btn_channel_join_request_accept)
        TextView btnChannelJoinRequestAccept;
        @BindView(R.id.shimmer_view_container)
        ShimmerFrameLayout shimmerViewContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnMyChannelSelectedListener listener) {
            MembersData membersData = snapshot.toObject(MembersData.class);
            btnChannelJoinRequestAccept.setVisibility(View.GONE);
            shimmerViewContainer.startShimmerAnimation();
            DocumentReference docRef = new FireStoreHelper().GetUserByDocument(membersData.getMemberUserId());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final User user = document.toObject(User.class);
                        Resources resources = itemView.getResources();
                        tvUserRequestChannelJoin.setText(user.getUserName());
                        String url = user.getUserPhotoUrl();

                        if (membersData.getMemberJoinedDate() != null) {
                            tvDateRequestChannelJoin.setText("Joined on " + GetDateName(membersData.getMemberJoinedDate().toString()));
                        }

                        if (url.isEmpty()) {
                           /* int color = radio.getChannelColor();
                            String letter = (radio.getChannelName().substring(0, 1).toUpperCase());
                            imgMyChannel.setImageResource(R.drawable.br_rect);
                            imgMyChannel.setColorFilter(color);
                            imgTxtMyChannel.setText(letter);
                            imgTxtMyChannel.setVisibility(View.VISIBLE);*/
                        } else {
                            Glide.with(profileImage.getContext())
                                    .load(url)
                                    .animate(R.anim.fade_in)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .fitCenter()
                                    .placeholder(R.drawable.placeholder)
                                    .into(profileImage);
                        }

                        itemView.setOnClickListener(view -> {
                            if (listener != null) {
                                listener.onClick(membersData.getMemberChannelId(), user.getUserId());
                            }
                        });
                    }
                }
            });

            shimmerViewContainer.stopShimmerAnimation();
        }

        public int getColor(String typeColor) {
            int returnColor = Color.GRAY;
            int arrayId = itemView.getResources().getIdentifier("mdcolor_" + typeColor, "array", itemView.getContext().getPackageName());

            if (arrayId != 0) {
                TypedArray colors = itemView.getResources().obtainTypedArray(arrayId);
                int index = (int) (Math.random() * colors.length());
                returnColor = colors.getColor(index, Color.GRAY);
                colors.recycle();
            }
            return returnColor;
        }

        private String GetDateName(String date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d hh:mm:ss z yyyy", Locale.getDefault());
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM yyyy");
            try {
                Date dat1e = dateFormat.parse(date);
                return dateFormat2.format(dat1e);
            } catch (ParseException e) {
                return e.toString();
            }
        }
    }
}

