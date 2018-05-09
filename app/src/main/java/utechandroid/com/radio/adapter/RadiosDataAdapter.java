package utechandroid.com.radio.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.prudenttechno.radioNotification.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import utechandroid.com.radio.model.Radio;

/**
 * Created by Utsav Shah on 9/28/2017.
 */

public class RadiosDataAdapter extends RecyclerView.Adapter<RadiosDataAdapter.RadiosDataViewHolder> {
    private Context context;
    private ArrayList<Radio> whatsappArrayList = new ArrayList<>();

    public RadiosDataAdapter(Context context, ArrayList<Radio> whatsappArrayList) {
        this.context = context;
        this.whatsappArrayList = whatsappArrayList;
    }

    @Override
    public RadiosDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_radios, parent, false);
        return new RadiosDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RadiosDataViewHolder holder, int position) {
        Radio whatsapp = whatsappArrayList.get(position);
        holder.tvName.setText(whatsapp.getName());
        holder.tvMessage.setText(whatsapp.getMessage());
        holder.tvMessageCounter.setText(whatsapp.getMessagecounter());

        Picasso.with(context).load(whatsapp.getProfileUrl()).fit().into(holder.ivprofile);
    }

    @Override
    public int getItemCount() {
        return whatsappArrayList.size();
    }

    public static class RadiosDataViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView ivprofile;
        private AppCompatTextView tvName, tvMessage, tvTime, tvMessageCounter;

        public RadiosDataViewHolder(View itemView) {
            super(itemView);
            ivprofile = (CircleImageView) itemView.findViewById(R.id.profile_image);
            tvName = (AppCompatTextView) itemView.findViewById(R.id.tv_name);
            tvMessage = (AppCompatTextView) itemView.findViewById(R.id.tv_message);
            tvMessageCounter = (AppCompatTextView) itemView.findViewById(R.id.tv_message_counter);
        }
    }
}
