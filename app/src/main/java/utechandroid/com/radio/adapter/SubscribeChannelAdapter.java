package utechandroid.com.radio.adapter;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.prudenttechno.radioNotification.R;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.ChannelData;
import utechandroid.com.radio.firestore.model.Message;

/**
 * Created by Utsav Shah on 16-Oct-17.
 */

public class SubscribeChannelAdapter extends RecyclerView.Adapter<SubscribeChannelAdapter.ViewHolder> {

    public interface OnChannelSelectedListener {

        void onChannelSelected(ChannelData members, ImageView imageView);

        void hideShimmer();

        void showShimmer();

    }

    private OnChannelSelectedListener mListener;
    private List<String> list;

    public SubscribeChannelAdapter(List<String> list, OnChannelSelectedListener listener) {
        mListener = listener;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.my_channels_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(list.get(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_myChannel)
        ImageView imgMyChannel;
        @BindView(R.id.img_txt_myChannel)
        TextView imgTxtMyChannel;
        @BindView(R.id.icon_front)
        RelativeLayout iconFront;
        @BindView(R.id.txt_myChannel_name)
        TextView txtMyChannelName;
        @BindView(R.id.txt_myChannel_description)
        TextView txtMyChannelDescription;
        @BindView(R.id.lyt_my_channel_list)
        LinearLayout lytMyChannelList;
        @BindView(R.id.cardView_my_channel_list)
        CardView cardViewMyChannelList;
        @BindView(R.id.img_channels_badge)
        ImageView imgChannelsBadge;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final String s,
                         final OnChannelSelectedListener listener) {

            listener.showShimmer();

            DocumentReference docRef = new FireStoreHelper().GetChannelByIdDocument(s);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final ChannelData channel = document.toObject(ChannelData.class);
                        txtMyChannelName.setText(channel.getChannelName());
                        txtMyChannelDescription.setText(channel.getChannelDescription());
                        String url = channel.getChannelPhotoUrl();
                        if (url.isEmpty()) {
                            int color = channel.getChannelColor();
                            String letter = (channel.getChannelName().substring(0, 1).toUpperCase());
                            imgMyChannel.setImageResource(R.drawable.br_rect);
                            imgMyChannel.setColorFilter(color);
                            imgTxtMyChannel.setText(letter);
                            imgTxtMyChannel.setVisibility(View.VISIBLE);
                        } else {
                            Glide.with(imgMyChannel.getContext())
                                    .load(url)
                                    .animate(R.anim.fade_in)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .fitCenter()
                                    .placeholder(R.drawable.placeholder)
                                    .into(imgMyChannel);
                            imgTxtMyChannel.setVisibility(View.GONE);
                        }

                        Query query = FirebaseFirestore.getInstance().collection(Message.FIELD_COLLECTION)
                                .document(channel.getChannelId())
                                .collection(Message.FIELD_COLLECTION);
                        query.addSnapshotListener((snapshot, e) -> {
                            if (e != null) {
                                return;
                            }
                            if (snapshot != null && !snapshot.isEmpty()) {
                                for (DocumentChange change : snapshot.getDocumentChanges()) {
                                    switch (change.getType()) {
                                        case ADDED:
                                            DocumentSnapshot doc = change.getDocument();
                                            final Message messageList = doc.toObject(Message.class);
                                            break;
                                        case MODIFIED:

                                            break;
                                        case REMOVED:

                                            break;
                                    }
                                }
                            }
                        });



                        /*Query query = FirebaseFirestore.getInstance().collection(MessageReadData.FIELD_COLLECTION)
                                .document(channel.getChannelId())
                                .collection(MessageReadData.FIELD_COLLECTION);

                        ListenerRegistration messageReadListner = query.addSnapshotListener((snapshot, e) -> {
                            if (e != null) {
                                return;
                            }
                            if (snapshot != null && !snapshot.isEmpty()) {
                                for (DocumentChange change : snapshot.getDocumentChanges()) {
                                    switch (change.getType()) {
                                        case ADDED:
                                            DocumentSnapshot doc = change.getDocument();
                                            MessageReadData user = doc.toObject(MessageReadData.class);

                                            if (user.getMembers().entrySet().size() > 0) {
                                                int count = 0;
                                                for (Map.Entry<String, Integer> f : user.getMembers().entrySet()) {
                                                    String key = f.getKey();
                                                    if (key.equals(new FireStoreAuthHelper().GetUserId())) {
                                                        Integer value = f.getValue();
                                                        if (value == 1 || value == 2) {
                                                            count++;
                                                        }
                                                    }
                                                }
                                                if (count == 0) {
                                                    imgChannelsBadge.setVisibility(View.INVISIBLE);
                                                } else {
                                                    final BadgeDrawable drawable =
                                                            new BadgeDrawable.Builder()
                                                                    .type(BadgeDrawable.TYPE_NUMBER)
                                                                    .number(count)
                                                                    .padding(2, 2, 2, 2, 2)
                                                                    .badgeColor(Color.RED)
                                                                    .build();
                                                    imgChannelsBadge.setImageDrawable(drawable);
                                                }

                                            }
                                            break;
                                        case MODIFIED:

                                            break;
                                        case REMOVED:

                                            break;
                                    }
                                }
                            }
                        });*/

                        itemView.setOnClickListener(view -> {
                            listener.onChannelSelected(channel, imgMyChannel);
                        });
                    }
                }
            });
            listener.hideShimmer();
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

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
