package utechandroid.com.radio.ui.categories;

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

import utechandroid.com.radio.ui.SubCategoryActivity;

/**
 * Created by Dharmik Patel on 24-Nov-17.
 */

public class SectionRecyclerViewAdapter extends RecyclerView.Adapter<SectionRecyclerViewAdapter.SectionViewHolder> implements Filterable {

    private ContactsAdapterListener listener;


    class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView sectionLabel;
        //  private RecyclerView itemRecyclerView;

        public SectionViewHolder(View itemView) {
            super(itemView);
            sectionLabel = (TextView) itemView.findViewById(R.id.section_label);
            //  itemRecyclerView = (RecyclerView) itemView.findViewById(R.id.item_recycler_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    private Context context;
    private List<SectionCategories> sectionModelArrayList;
    private List<SectionCategories> contactListFiltered;

    public SectionRecyclerViewAdapter(Context context, List<SectionCategories> sectionModelArrayList, ContactsAdapterListener listener) {
        this.context = context;
        this.sectionModelArrayList = sectionModelArrayList;
        this.listener = listener;
        this.contactListFiltered = sectionModelArrayList;
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_custom_row_layout, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        final SectionCategories sectionModel = contactListFiltered.get(position);
        holder.sectionLabel.setText(sectionModel.getSectionLabel());
        holder.sectionLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onContactSelected(contactListFiltered.get(position));
                Intent intent = new Intent(context, SubCategoryActivity.class);
                intent.putExtra("subcategory", sectionModel.getSectionLabel());
                context.startActivity(intent);
                // Toast.makeText(context, ""+sectionModel.getSectionLabel(), Toast.LENGTH_SHORT).show();
            }
        });
        //   holder.itemRecyclerView.setHasFixedSize(true);
        //   holder.itemRecyclerView.setNestedScrollingEnabled(false);
        //  LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        //    holder.itemRecyclerView.setLayoutManager(linearLayoutManager1);
        //  CategoryAdapter adapter = new CategoryAdapter(context, sectionModel.getItemArrayList());
        //    holder.itemRecyclerView.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        // return sectionModelArrayList == null ? 0 : sectionModelArrayList.size();
        return contactListFiltered.size();
    }

    public interface ContactsAdapterListener {
        void onContactSelected(SectionCategories contact);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = sectionModelArrayList;
                } else {
                    List<SectionCategories> filteredList = new ArrayList<>();
                    for (SectionCategories row : sectionModelArrayList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getSectionLabel().toLowerCase().contains(charString.toLowerCase()) || row.getSectionLabel().contains(charSequence)) {
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
                contactListFiltered = (ArrayList<SectionCategories>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }
}
