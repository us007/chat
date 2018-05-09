package utechandroid.com.radio.adapter.notificationAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.prudenttechno.radioNotification.R;

import java.util.List;

import utechandroid.com.radio.data.api.model.Notification.response.NotificationGetDataResponse;
import utechandroid.com.radio.ui.notificationDetails.NotificationDetailsActivity;

/**
 * Created by AFF41 on 13-Mar-18.
 */

public class ShowNotificationDataAdapter extends RecyclerView.Adapter<ShowNotificationDataAdapter.ShowNotificationDataViewHolder> {
    private Context context;
    private List<NotificationGetDataResponse.Table1> table1ArrayList;

    public ShowNotificationDataAdapter(Context context, List<NotificationGetDataResponse.Table1> table1ArrayList) {
        this.context = context;
        this.table1ArrayList = table1ArrayList;
    }

    @NonNull
    @Override
    public ShowNotificationDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_notification_data, parent, false);
        return new ShowNotificationDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowNotificationDataViewHolder holder, int position) {
        NotificationGetDataResponse.Table1 table1 = table1ArrayList.get(position);
        holder.title.setText(table1.getTitle());
        holder.body.setText(table1.getBody());
        Glide.with(context)
                .load(table1.getImageUrl())
                .animate(R.anim.fade_in)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .placeholder(R.drawable.placeholder)
                .into(holder.iv_photo);
        holder.notification.setOnClickListener(v -> {
            NotificationGetDataResponse.Table1 table11 = new NotificationGetDataResponse.Table1();
            table11.setBody(table1.getBody());
            table11.setImageUrl(table1.getImageUrl());
            table11.setTitle(table1.getTitle());
            table11.setAddress(table1.getAddress());
            Intent intent = new Intent(context, NotificationDetailsActivity.class);
            intent.putExtra("notification_data", table11);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return table1ArrayList.size();
    }

    public static class ShowNotificationDataViewHolder extends RecyclerView.ViewHolder {
        CardView notification;
        ImageView iv_photo;
        TextView title, body;

        public ShowNotificationDataViewHolder(View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.cardView_my_notification_list);
            iv_photo = itemView.findViewById(R.id.img_notification);
            title = itemView.findViewById(R.id.txt_notification_name);
            body = itemView.findViewById(R.id.txt_notification_description);
        }
    }
}
