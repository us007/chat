package utechandroid.com.radio.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.prudenttechno.radioNotification.R;

import java.util.List;

import utechandroid.com.radio.data.api.model.EditPhoneContactResponse;
import utechandroid.com.radio.ui.MyFamily.MyFamilyEditActivity;

/**
 * Created by AFF41 on 1/20/2018.
 */

public class EditContactDetailAdapter extends RecyclerView.Adapter<EditContactDetailAdapter.EditContactDetailViewHolder> {
    private Context context;
    private List<EditPhoneContactResponse.Table> tableItemList;

    public EditContactDetailAdapter(Context context, List<EditPhoneContactResponse.Table> tableItemList) {
        this.context = context;
        this.tableItemList = tableItemList;
    }

    @Override
    public EditContactDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_contact_edit, parent, false);
        return new EditContactDetailViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return tableItemList.size();
    }

    @Override
    public void onBindViewHolder(EditContactDetailViewHolder holder, int position) {
        EditPhoneContactResponse.Table tableItem = tableItemList.get(position);
        holder.tvEditName.setText(tableItem.getPartyName());
        holder.tvEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MyFamilyEditActivity.class);
                //intent.putExtra("party_name",tableItem.getPartyName());
                intent.putExtra("party_id",tableItem.getId());
                context.startActivity(intent);
            }
        });
    }

    public static class EditContactDetailViewHolder extends RecyclerView.ViewHolder {
        TextView tvEditName;

        public EditContactDetailViewHolder(View itemView) {
            super(itemView);
            tvEditName = itemView.findViewById(R.id.tv_contact_name);
        }
    }
}
