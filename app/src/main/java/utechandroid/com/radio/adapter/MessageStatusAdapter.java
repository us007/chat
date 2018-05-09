package utechandroid.com.radio.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.prudenttechno.radioNotification.R;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.User;

/**
 * Created by Utsav Shah on 28-Nov-17.
 */
public class MessageStatusAdapter extends StatelessSection {

    private List<String> allData;
    private String title;

    public MessageStatusAdapter(String title, List<String> data) {
        super(R.layout.lyt_message_status_header, R.layout.list_user_message_status);
        this.allData = data;
        this.title = title;
    }

    @Override
    public int getContentItemsTotal() {
        return allData.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder iHolder = (ItemViewHolder) holder;

        DocumentReference docRef = new FireStoreHelper().GetUserByDocument(allData.get(position));
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    final User user = document.toObject(User.class);

                    iHolder.itemTitle.setText(user.getUserName());

                    if (!user.getUserPhotoUrl().isEmpty()){
                        Glide.with(iHolder.itemImage.getContext())
                                .load(user.getUserPhotoUrl())
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .crossFade()
                                .fitCenter()
                                .placeholder(R.drawable.placeholder)
                                .into(iHolder.itemImage);
                    }
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        SectionViewHolder hHolder = (SectionViewHolder) holder;
        hHolder.sectionTitle.setText(title);
        switch (title) {
            case "Read by":
                hHolder.sectionTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_read_message, 0);
                break;
            case "Delivered to":
                hHolder.sectionTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_unread_message, 0);
                break;
            default:
                hHolder.sectionTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sent_message, 0);
                break;
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        final TextView sectionTitle;

        public SectionViewHolder(View itemView) {
            super(itemView);
            sectionTitle = (TextView) itemView.findViewById(R.id.txt_message_status_header);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        final TextView itemTitle;
        final CircleImageView itemImage;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemTitle = (TextView) itemView.findViewById(R.id.tv_user);
            itemImage = (CircleImageView) itemView.findViewById(R.id.profile_user);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Toast.makeText(v.getContext(), itemTitle.getText(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}