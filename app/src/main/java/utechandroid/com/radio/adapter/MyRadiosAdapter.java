package utechandroid.com.radio.adapter;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import de.hdodenhof.circleimageview.CircleImageView;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.RadioData;

/**
 * Created by Utsav Shah on 14-Oct-17.
 */

public class MyRadiosAdapter extends RecyclerView.Adapter<MyRadiosAdapter.ViewHolder> {

    public interface OnMyRadioSelectedListener {

        void onRadioSelected(RadioData radio, CircleImageView imageView);

        void hideShimmer();

        void showShimmer();
    }

    private List<String> list;
    private OnMyRadioSelectedListener mListener;

    public MyRadiosAdapter(List<String> list, OnMyRadioSelectedListener listener) {
        mListener = listener;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.list_myradio, parent, false));
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

        @BindView(R.id.img_myRadio)
        CircleImageView imgMyRadio;
        @BindView(R.id.img_txt_myRadio)
        TextView imgTxtMyRadio;
        @BindView(R.id.icon_front)
        RelativeLayout iconFront;
        @BindView(R.id.txt_myRadio_name)
        TextView txtMyRadioName;
        @BindView(R.id.txt_myRadio_description)
        TextView imgMyRadioDescription;
        @BindView(R.id.lyt_my_radio_list)
        LinearLayout lytMyRadioList;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final String s,
                         final OnMyRadioSelectedListener listener) {
            listener.showShimmer();

            DocumentReference docRef = new FireStoreHelper().GetRadioByIdDocument(s);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final RadioData radio = document.toObject(RadioData.class);
                        Resources resources = itemView.getResources();

                        txtMyRadioName.setText(radio.getRadioName());
                        imgMyRadioDescription.setText(radio.getRadioDescription());
                        String url = radio.getRadioPhotoUrl();

                        if (url.isEmpty()) {
                            int color = radio.getRadioColor();
                            String letter = (radio.getRadioName().substring(0, 1).toUpperCase());
                            imgMyRadio.setImageResource(R.drawable.bg_circle);
                            imgMyRadio.setColorFilter(color);
                            imgTxtMyRadio.setText(letter);
                            imgTxtMyRadio.setVisibility(View.VISIBLE);
                        } else {
                            Glide.with(imgMyRadio.getContext())
                                    .load(url)
                                    .animate(R.anim.fade_in)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .fitCenter()
                                    .placeholder(R.drawable.placeholder)
                                    .into(imgMyRadio);
                            imgTxtMyRadio.setVisibility(View.GONE);
                        }

                        listener.hideShimmer();

                        itemView.setOnClickListener(view -> {
                            listener.onRadioSelected(radio, imgMyRadio);
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
