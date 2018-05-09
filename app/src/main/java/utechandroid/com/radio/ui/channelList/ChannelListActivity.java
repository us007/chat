package utechandroid.com.radio.ui.channelList;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prudenttechno.radioNotification.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utechandroid.com.radio.adapter.ChannelListAdapter;
import utechandroid.com.radio.events.ChannelEvent;
import utechandroid.com.radio.firestore.model.ChannelData;
import utechandroid.com.radio.firestore.model.SubCategory;
import utechandroid.com.radio.ui.channelpublic.ChannelPublicActivity;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;
import utechandroid.com.radio.util.ItemDecoration.GridSpacingItemDecoration;

public class ChannelListActivity extends ColorsAppCompatActivity implements ChannelListAdapter.OnChannelSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar_channel_list)
    ProgressBar progressBarChannelList;
    @BindView(R.id.recyclerView_channel_list)
    RecyclerView recyclerViewChannelList;

    private ChannelListAdapter channelListAdapter;
    private FirebaseFirestore db;
    private String mRadioId;
    private List<String> stringList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);
        ButterKnife.bind(this);

        db = FirebaseFirestore.getInstance();
        FirebaseFirestore.setLoggingEnabled(true);

        mRadioId = GetIntentValue();

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(mRadioId);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SetUpRecyclerView();

    }

    private String GetIntentValue() {
        return getIntent().getExtras().getString("name", "");
    }

    private String GetCategoryIntentValue() {
        return getIntent().getExtras().getString("category", "");
    }

    private void SetUpRecyclerView() {
        recyclerViewChannelList.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerViewChannelList.setLayoutManager(layoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.paddingItemDecorationDefault);
        recyclerViewChannelList.addItemDecoration(new GridSpacingItemDecoration(3, spacingInPixels, true, 0));
        LoadMyChannels();
    }

    @Override
    public void onChannelSelected(ChannelData channel) {
        EventBus.getDefault().postSticky(new ChannelEvent(channel));
        Intent intent = new Intent(getApplicationContext(), ChannelPublicActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void LoadMyChannels() {
        ProgressBarShow();
        DocumentReference documentReference =
                db.collection(SubCategory.FIELD_COLLECTION)
                        .document(GetCategoryIntentValue())
                        .collection("SubCategories")
                        .document(GetIntentValue());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    SubCategory subCategory = document.toObject(SubCategory.class);
                    stringList = new ArrayList<>(subCategory.getChannels().keySet());
                    channelListAdapter = new ChannelListAdapter(stringList, this);
                    recyclerViewChannelList.setAdapter(channelListAdapter);
                } else {
                    //Log.d(TAG, "No such document");
                }
                ProgressBarHide();
            } else {
                // Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void ProgressBarShow() {
        progressBarChannelList.setVisibility(View.VISIBLE);
        recyclerViewChannelList.setVisibility(View.INVISIBLE);
    }

    private void ProgressBarHide() {
        progressBarChannelList.setVisibility(View.GONE);
        recyclerViewChannelList.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
