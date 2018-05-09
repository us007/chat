package utechandroid.com.radio.adapter;

import android.content.res.Resources;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.prudenttechno.radioNotification.R;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.ChannelData;

/**
 * Created by Utsav Shah on 16-Oct-17.
 */

public class MyChannelsAdapter extends RecyclerView.Adapter<MyChannelsAdapter.ViewHolder> {

    public interface OnMyChannelSelectedListener {

        void onChannelSelected(ChannelData radio, ImageView imageView);

    }

    private OnMyChannelSelectedListener mListener;
    private List<String> list;

    public MyChannelsAdapter(List<String> list, OnMyChannelSelectedListener listener) {
        mListener = listener;
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final String s,
                         final OnMyChannelSelectedListener listener) {
            DocumentReference docRef = new FireStoreHelper().GetChannelByIdDocument(s);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final ChannelData radio = document.toObject(ChannelData.class);
                        Resources resources = itemView.getResources();

                        txtMyChannelName.setText(radio.getChannelName());
                        txtMyChannelDescription.setText(radio.getChannelDescription());
                        String url = radio.getChannelPhotoUrl();

                        if (url.isEmpty()) {
                            int color = radio.getChannelColor();
                            String letter = (radio.getChannelName().substring(0, 1).toUpperCase());
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

                        itemView.setOnClickListener(view -> {
                            if (listener != null) {
                                listener.onChannelSelected(radio, imgMyChannel);
                            }
                        });
                    } else {
                        //  Log.d(TAG, "No such document");
                    }
                } else {
                    // Log.d(TAG, "get failed with ", task.getException());
                }
            });
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
}
