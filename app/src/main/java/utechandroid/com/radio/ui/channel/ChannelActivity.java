package utechandroid.com.radio.ui.channel;

import android.animation.Animator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.RelativeLayout;


import com.prudenttechno.radioNotification.R;

import java.util.ArrayList;

import utechandroid.com.radio.adapter.RadiosDataAdapter;
import utechandroid.com.radio.model.Radio;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class ChannelActivity extends ColorsAppCompatActivity {
    private FloatingActionButton fab;
    private RelativeLayout layoutMain;
    private RelativeLayout layoutButtons;
    private RelativeLayout layoutContent;
    private boolean isOpen = false;
    private RecyclerView rvMyChannel;
    private ArrayList<Radio> whatsappArrayList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);


        layoutMain = (RelativeLayout) findViewById(R.id.layoutMain);
        layoutButtons = (RelativeLayout) findViewById(R.id.layoutButtons);
        layoutContent = (RelativeLayout) findViewById(R.id.layoutContent);
        rvMyChannel = (RecyclerView) findViewById(R.id.rv_mychannel);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMenu();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvMyChannel.setLayoutManager(layoutManager);
        createDummyData();
        RadiosDataAdapter radiosDataAdapter = new RadiosDataAdapter(this, whatsappArrayList);
        rvMyChannel.setAdapter(radiosDataAdapter);
        rvMyChannel.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown()) {
                    fab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    private void viewMenu() {

        if (!isOpen) {

            int x = layoutContent.getRight();
            int y = layoutContent.getBottom();

            int startRadius = 0;
            int endRadius = (int) Math.hypot(layoutMain.getWidth(), layoutMain.getHeight());

            fab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null)));
            fab.setImageResource(R.drawable.ic_action_close);

            Animator anim = ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius, endRadius);

            layoutButtons.setVisibility(View.VISIBLE);
            anim.start();

            isOpen = true;

        } else {

            int x = layoutButtons.getRight();
            int y = layoutButtons.getBottom();

            int startRadius = Math.max(layoutContent.getWidth(), layoutContent.getHeight());
            int endRadius = 0;

            fab.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null)));
            fab.setImageResource(R.drawable.ic_action_add);

            Animator anim = ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius, endRadius);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    layoutButtons.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            anim.start();

            isOpen = false;
        }
    }

    private void createDummyData() {
        for (int i = 0; i < 20; i++) {

            String[] u = new String[i];

            Radio whatsapp = new Radio();
            whatsapp.setName("utsav shah");
            whatsapp.setMessage("hello 123 testing");
            whatsapp.setMessagecounter("" + i);
            whatsapp.setProfileUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTd8VBYEGBNRlLpWTSGXIWweogfUwQuSoZzZtMHXsI5sCyBqVJz3w");
            whatsappArrayList.add(whatsapp);

        }
    }

}
