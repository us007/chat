package utechandroid.com.radio.ui.messageStatus;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;
import com.prudenttechno.radioNotification.R;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import utechandroid.com.radio.adapter.MessageStatusAdapter;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.MessageReadData;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class MessageStatusActivity extends ColorsAppCompatActivity {

    @BindView(R.id.messageText)
    TextView messageText;
    @BindView(R.id.messageTime)
    TextView messageTime;
    @BindView(R.id.bubble)
    RelativeLayout bubble;
    @BindView(R.id.layout_chat_message)
    RelativeLayout layoutChatMessage;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView_message_status)
    RecyclerView recyclerViewMessageStatus;

    private ListenerRegistration listenerRegistration;

    String messageId, channelId;
    SectionedRecyclerViewAdapter messageStatusAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_status);
        ButterKnife.bind(this);

        initToolbar();

        recyclerViewMessageStatus.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewMessageStatus.setLayoutManager(layoutManager);
        //recyclerViewMessageStatus.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        if (getIntent() != null) {
            messageId = getIntent().getExtras().getString("messageId");
            channelId = getIntent().getExtras().getString("channelId");
            DocumentReference documentReference = new FireStoreHelper().GetMessageReadDocument(messageId, channelId);
            listenerRegistration = documentReference.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    return;
                }
                if (snapshot.exists()) {
                    MessageReadData user = snapshot.toObject(MessageReadData.class);
                    //  messageText.setText(user.getMessage().getMessage());
                    messageTime.setText(GetTime(user.getMessage().getMessageDate().toString()));
                    if (user.getMembers().entrySet().size() > 0) {
                        List<String> sentbylist = new ArrayList<>();
                        List<String> readbylist = new ArrayList<>();
                        List<String> unreadbylist = new ArrayList<>();
                        for (Map.Entry<String, Integer> f : user.getMembers().entrySet()) {
                            String key = f.getKey();
                            Integer value = f.getValue();
                            if (value == 1) {
                                  /* Sent */
                                sentbylist.add(key);
                            }
                            if (value == 2) {
                                  /* Unread */
                                unreadbylist.add(key);
                            }
                            if (value == 3) {
                                  /* Read */
                                readbylist.add(key);
                            }
                        }

                        messageStatusAdapter = new SectionedRecyclerViewAdapter();

                        if (unreadbylist.size() > 0) {
                            MessageStatusAdapter secondsection = new MessageStatusAdapter("Delivered to", unreadbylist);
                            messageStatusAdapter.addSection(secondsection);
                        }

                        if (readbylist.size() > 0) {
                            MessageStatusAdapter thirdSection = new MessageStatusAdapter("Read by", readbylist);
                            messageStatusAdapter.addSection(thirdSection);
                        }
                        if (sentbylist.size() > 0) {
                            MessageStatusAdapter firstSection = new MessageStatusAdapter("UnDelivered to", sentbylist);
                            messageStatusAdapter.addSection(firstSection);
                        }
                        recyclerViewMessageStatus.setAdapter(messageStatusAdapter);
                    }
                }
            });
        }
    }

    private void initToolbar() {
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private static String GetTime(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d hh:mm:ss z yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("h:mm a");
        try {
            Date date = dateFormat.parse(time);
            return dateFormat2.format(date);
        } catch (ParseException e) {
            return e.toString();
        }
    }
}
