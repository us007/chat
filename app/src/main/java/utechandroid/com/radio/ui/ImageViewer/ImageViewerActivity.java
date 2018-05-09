package utechandroid.com.radio.ui.ImageViewer;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.prudenttechno.radioNotification.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class ImageViewerActivity extends ColorsAppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imageView)
    SubsamplingScaleImageView imageView;

    String uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTransition();
        setContentView(R.layout.activity_image_viewer);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        if (getIntent() != null) {
            uri = getIntent().getExtras().getString("uri");
            if (uri != null) {
                imageView.setImage(ImageSource.uri(uri));
            }
        }

    }

    private void SetTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();

            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(Color.BLACK);

            w.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition return_transition =
                    TransitionInflater.from(this).
                            inflateTransition(R.transition.change_image_transform);
            Transition enter_transition =
                    TransitionInflater.from(this).
                            inflateTransition(R.transition.change_image_transform);
            w.setSharedElementEnterTransition(enter_transition);
            w.setSharedElementExitTransition(return_transition);
         /*   w.setReturnTransition(new Fade());
            w.setEnterTransition(new Fade());*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_imageviewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                Uri uridata = Uri.parse(uri);
                share.putExtra(Intent.EXTRA_STREAM, uridata);
                startActivity(Intent.createChooser(share, "Share Image!"));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
