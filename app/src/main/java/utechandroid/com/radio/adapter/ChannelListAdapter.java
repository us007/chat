package utechandroid.com.radio.adapter;

import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.prudenttechno.radioNotification.R;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.ChannelData;

/**
 * Created by Utsav Shah on 23-Oct-17.
 */

public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ViewHolder> {

    private List<String> list;

    public interface OnChannelSelectedListener {
        void onChannelSelected(ChannelData channel);
    }

    private OnChannelSelectedListener mListener;

    public ChannelListAdapter(List<String> list, OnChannelSelectedListener listener) {
        mListener = listener;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.channel_list_subscribe, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(list.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_channel_list)
        ImageView imgChannelList;
        @BindView(R.id.img_txt_myRadio)
        TextView imgTxtMyRadio;
        @BindView(R.id.text_channel_list_name)
        TextView textChannelListName;
        @BindView(R.id.cardView_channel_list)
        CardView cardViewChannelList;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final String string,
                         final OnChannelSelectedListener listener) {

            DocumentReference docRef = new FireStoreHelper().GetChannelByIdDocument(string);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final ChannelData channel = document.toObject(ChannelData.class);
                        Resources resources = itemView.getResources();

                        textChannelListName.setText(channel.getChannelName());
                        String url = channel.getChannelPhotoUrl();
                        if (url.isEmpty()) {
                            int color = channel.getChannelColor();
                            String letter = (channel.getChannelName().substring(0, 1).toUpperCase());
                            imgChannelList.setImageResource(R.drawable.br_rect);
                            imgChannelList.setColorFilter(color);
                            imgTxtMyRadio.setText(letter);
                            imgTxtMyRadio.setVisibility(View.VISIBLE);
                        } else {
                            Glide.with(imgChannelList.getContext())
                                    .load(url)
                                    .animate(R.anim.fade_in)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .fitCenter()
                                    .placeholder(R.drawable.placeholder)
                                    .into(imgChannelList);
                            imgTxtMyRadio.setVisibility(View.GONE);
                        }
                        itemView.setOnClickListener(view -> {
                            if (listener != null) {
                                listener.onChannelSelected(channel);
                            }
                        });
                        // Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                    } else {
                        //  Log.d(TAG, "No such document");
                    }
                } else {
                    // Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }

    }
}
