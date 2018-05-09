package utechandroid.com.radio.adapter.categoryAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.prudenttechno.radioNotification.R;

import java.util.ArrayList;
import java.util.List;

import utechandroid.com.radio.firestore.model.SubCategory;
import utechandroid.com.radio.ui.channelList.ChannelListActivity;

/**
 * Created by Utsav Shah on 18-Oct-17.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.SubTitleViewHolder> implements Filterable {

    private Context context;
    private List<SubCategory> subCategoryList;

    private List<SubCategory> contactListFiltered;
    private ContactsAdapterListenerNew listener;

    public CategoryAdapter(Context context, List<SubCategory> groups, ContactsAdapterListenerNew listener) {
        this.context = context;
        this.subCategoryList = groups;
        this.listener = listener;
        this.contactListFiltered = groups;
    }

    @Override
    public SubTitleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_subtitle, parent, false);
        return new SubTitleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubTitleViewHolder holder, int position) {

        int channelsSize = contactListFiltered.get(position).getChannels().entrySet().size();
        String count = channelsSize == 0 ? "" : "(" + channelsSize + ")";
        holder.subTitleTextView.setText(contactListFiltered.get(position).getName() + " " + count);

        holder.subTitleTextView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChannelListActivity.class);
            intent.putExtra("name", subCategoryList.get(position).getName());
            intent.putExtra("category", subCategoryList.get(position).getCategories());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = subCategoryList;
                } else {
                    List<SubCategory> filteredList = new ArrayList<>();
                    for (SubCategory row : subCategoryList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<SubCategory>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }

    public interface ContactsAdapterListenerNew {
        void onContactSelected(SubCategory contact);
    }

    class SubTitleViewHolder extends RecyclerView.ViewHolder {

        public TextView subTitleTextView;

        public SubTitleViewHolder(View itemView) {
            super(itemView);
            subTitleTextView = (TextView) itemView.findViewById(R.id.subtitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

}
