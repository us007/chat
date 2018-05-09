package utechandroid.com.radio.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.prudenttechno.radioNotification.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import utechandroid.com.radio.data.api.model.TableItem;
import utechandroid.com.radio.events.Contactevent;
import utechandroid.com.radio.ui.SearchContactDirectory.SearchContactDetailActivity;
import utechandroid.com.radio.ui.SearchContactDirectory.SearchContactFamilyActivity;

/**
 * Created by Utsav Shah on 20-Nov-17.
 */

public class SearchContactAdapter extends RecyclerView.Adapter<SearchContactAdapter.RadiosDataViewHolder> implements Filterable {

    private Context context;
    private List<TableItem> tableItemList, filterList;
    private Boolean flag;

    public SearchContactAdapter(Context context, List<TableItem> whatsappArrayList,Boolean flag) {
        this.context = context;
        this.tableItemList = whatsappArrayList;
        this.filterList = whatsappArrayList;
        this.flag = flag;
    }

    @Override
    public RadiosDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_search_contact, parent, false);
        return new RadiosDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RadiosDataViewHolder holder, int position) {
        TableItem tableItem = filterList.get(position);

        holder.txtContactName.setText(tableItem.getPartyName()+"utsav");
        holder.txtContactMobileNo.setText(tableItem.getMobile());
        holder.txtContactOfficeNo.setText(tableItem.getOffice());
        holder.txtContactResidenceNo.setText(tableItem.getResidence());

        if (flag){
            holder.imageFamily.setVisibility(View.GONE);
        }else {
            holder.imageFamily.setVisibility(View.VISIBLE);
        }
        holder.lytSearchItem.setOnClickListener(view -> {
            EventBus.getDefault().postSticky(new Contactevent(tableItem));
            Intent i = new Intent(context, SearchContactDetailActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        });
        holder.imageFamily.setOnClickListener(view -> {
            Intent i = new Intent(context, SearchContactFamilyActivity.class);
            i.putExtra("id",tableItem.getId());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return (null != filterList ? filterList.size() : 0);
    }

    static class RadiosDataViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_contact_name)
        TextView txtContactName;
        @BindView(R.id.txt_contact_residence_no)
        TextView txtContactResidenceNo;
        @BindView(R.id.txt_contact_office_no)
        TextView txtContactOfficeNo;
        @BindView(R.id.txt_contact_mobile_no)
        TextView txtContactMobileNo;
        @BindView(R.id.lyt_search_item)
        LinearLayout lytSearchItem;
        @BindView(R.id.image_family)
        ImageButton imageFamily;

        public RadiosDataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filterList = tableItemList;
                } else {
                    List<TableItem> filteredList = new ArrayList<>();
                    for (TableItem row : tableItemList) {
                        if (row.getPartyName().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getAddress().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getOccupation().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    filterList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterList = (List<TableItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}