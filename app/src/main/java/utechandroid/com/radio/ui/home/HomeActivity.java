package utechandroid.com.radio.ui.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.prudenttechno.radioNotification.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utechandroid.com.radio.adapter.HomeTabAdapter;
import utechandroid.com.radio.adapter.notificationAdapter.ShowNotificationDataAdapter;
import utechandroid.com.radio.data.api.apihelper.ApiConfig;
import utechandroid.com.radio.data.api.apihelper.AppConfigForNotification;
import utechandroid.com.radio.data.api.model.Notification.request.NotificationGetDataRequest;
import utechandroid.com.radio.data.api.model.Notification.response.NotificationGetDataResponse;
import utechandroid.com.radio.data.pref.SessionManager;
import utechandroid.com.radio.service.ServiceUtils;
import utechandroid.com.radio.ui.MyFamily.MyFamilyShowDetailActivity;
import utechandroid.com.radio.ui.SearchContactDirectory.SearchContactActivity;
import utechandroid.com.radio.ui.joinChannel.JoinChannelBottomSheet;
import utechandroid.com.radio.ui.myProfile.EditProfileActivity;
import utechandroid.com.radio.ui.privacyPolicy.PrivacyPolicyActivity;
import utechandroid.com.radio.ui.setting.SettingActivity;
import utechandroid.com.radio.ui.signin.SigninActivity;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;
import utechandroid.com.radio.util.ItemDecoration.PaddingItemDecoration;
import utechandroid.com.radio.util.fullScreenFragmentDialog.FullScreenDialogFragment;

public class HomeActivity extends ColorsAppCompatActivity implements HomeView,
        FullScreenDialogFragment.OnDiscardListener {

    // @BindView(R.id.toolbar)
    Toolbar toolbar;
    //  @BindView(R.id.tabs_home)
    TabLayout tabsHome;
    //  @BindView(R.id.viewPager_home)
    ViewPager viewPagerHome;
    //  @BindView(R.id.fab_home_create_channel)
    FloatingActionButton fabHomeCreateChannel;
    //@BindView(R.id.navigation_view)
    NavigationView navigationView;
    // @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    private HomePresenter homePresenter;
    private Boolean doubleBackToExitPressedOnce = false;
    private FullScreenDialogFragment dialogFragment;
    private SessionManager sessionManager;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    private ProgressDialog dialog;
    int[] iconIntArray = {R.drawable.ic_playlist_add, R.drawable.ic_add_white};
 //   private ShowNotificationDataAdapter showNotificationDataAdapter;
 WebView webView;
    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        SetTransition();

        setContentView(R.layout.activity_home);


        // ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        tabsHome = findViewById(R.id.tabs_home);
        viewPagerHome = findViewById(R.id.viewPager_home);
        fabHomeCreateChannel = findViewById(R.id.fab_home_create_channel);
        recyclerView = findViewById(R.id.rv_show_data);
        drawerLayout = findViewById(R.id.drawer_layout);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading");
        sessionManager = new SessionManager(getApplicationContext());
        AlertDialog dialogBuilder = new AlertDialog.Builder(this,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.advertise_custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        ImageView imageView = dialogView.findViewById(R.id.btn_cancel);
         webView=dialogView.findViewById(R.id.web_ad);
        // WebSettings webSettings = webView.getSettings();
        //  webSettings.setJavaScriptEnabled(true);

        webView.loadUrl("http://27.109.20.118/RadioAds/ads.jpg");
        webView.reload();

        imageView.setOnClickListener(v -> dialogBuilder.dismiss());
        dialogBuilder.show();

        checkAndroidVersion();

        //  initJoinBottomSheet();
        InitPresenter();
        onAttach();



        initNavigationDrawer();
      //  showChangeLangDialog();
        //   initViewPager();
//
//        if (savedInstanceState != null) {
//            dialogFragment =
//                    (FullScreenDialogFragment) getSupportFragmentManager().findFragmentByTag("dialog");
//            if (dialogFragment != null) {
//                dialogFragment.setOnDiscardListener(this);
//            }
//        }
//        fabHomeCreateChannel.setOnClickListener(v -> {
//            int tab_position = tabsHome.getSelectedTabPosition();
//            if (tab_position == 0) {
//                startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
//            }
//            if (tab_position == 1) {
//                final Bundle args = new Bundle();
//                args.putString(CreateRadioFragment.EXTRA_NAME, "");
//
//                dialogFragment = new FullScreenDialogFragment.Builder(HomeActivity.this)
//                        .setTitle("Create Radio")
//                        .setExtraActions(R.menu.menu_fullscreen_dialog)
//                        .setOnDiscardListener(HomeActivity.this)
//                        .setContent(CreateRadioFragment.class, args)
//                        .build();
//
//                dialogFragment.show(getSupportFragmentManager(), "dialog");
//            }
//        });
        SetUpRecyclerView();
        loadBase();
    }
    private void SetUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.paddingItemDecorationDefault);
        // int padding = (int) getResources().getDimension(R.dimen.paddingItemDecorationDefault);
        //    int paddingEdge = (int) getResources().getDimension(R.dimen.paddingItemDecorationDefault);
        recyclerView.addItemDecoration(new PaddingItemDecoration(spacingInPixels));

    }

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }
    }

    public void showChangeLangDialog() {
        AlertDialog dialogBuilder = new AlertDialog.Builder(this,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.advertise_custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        ImageView imageView = dialogView.findViewById(R.id.btn_cancel);
        WebView webView=dialogView.findViewById(R.id.web_ad);
       // WebSettings webSettings = webView.getSettings();
      //  webSettings.setJavaScriptEnabled(true);

        webView.loadUrl("http://27.109.20.118/RadioAds/ads.jpg");
        webView.reload();

        imageView.setOnClickListener(v -> dialogBuilder.dismiss());
        dialogBuilder.show();

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.CAMERA) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.WRITE_CONTACTS) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.WRITE_CONTACTS) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.READ_CONTACTS)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Please Grant Permissions for media files",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        v -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(
                                        new String[]{Manifest.permission
                                                .READ_EXTERNAL_STORAGE,
                                                Manifest.permission
                                                        .WRITE_EXTERNAL_STORAGE
                                                , Manifest.permission.CAMERA, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS},
                                        PERMISSIONS_MULTIPLE_REQUEST);
                            }
                        }).show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission
                                    .READ_EXTERNAL_STORAGE,
                                    Manifest.permission
                                            .WRITE_EXTERNAL_STORAGE
                                    , Manifest.permission.CAMERA, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_MULTIPLE_REQUEST);
                }
            }
        } else {
            // write your logic code if permission already granted
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalFile = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean writeContact = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean readContact = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    if (cameraPermission && readExternalFile && writeExternalFile && writeContact && readContact) {
                        // write your logic here
                    } else {
                        Snackbar.make(this.findViewById(android.R.id.content),
                                "Please Grant Permissions to upload profile photo",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                v -> {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(
                                                new String[]{Manifest.permission
                                                        .READ_EXTERNAL_STORAGE,
                                                        Manifest.permission
                                                                .WRITE_EXTERNAL_STORAGE
                                                        , Manifest.permission.CAMERA, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS},
                                                PERMISSIONS_MULTIPLE_REQUEST);
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

    private void initJoinBottomSheet() {
        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();
        if (appLinkData != null) {

            if (new SessionManager(getApplicationContext()).isLoggedIn()) {
                String id = appLinkData.getQueryParameter("id");
                Bundle args = new Bundle();
                args.putString("id", id);
                JoinChannelBottomSheet bottomSheetDialogFragment = new JoinChannelBottomSheet();
                bottomSheetDialogFragment.setArguments(args);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
            } else {
                Toast.makeText(getApplicationContext(), "You need to Login before Join Any channel", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                startActivity(intent);
            }

        }
    }

    private void initViewPager() {
        HomeTabAdapter homeTabAdapter = new HomeTabAdapter(getSupportFragmentManager());
        viewPagerHome.setAdapter(homeTabAdapter);
        tabsHome.setupWithViewPager(viewPagerHome);

        tabsHome.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerHome.setCurrentItem(tab.getPosition());
                animateFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void initNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.menu_my_profile:
                    startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
                    drawerLayout.closeDrawers();
                    break;
                case R.id.menu_phone_directory:
                    startActivity(new Intent(getApplicationContext(), SearchContactActivity.class));
                    drawerLayout.closeDrawers();
                    break;
                case R.id.menu_myfamily_directory:
                    startActivity(new Intent(getApplicationContext(), MyFamilyShowDetailActivity.class));
                    drawerLayout.closeDrawers();
                    break;
                case R.id.menu_privacy_policy:
                    startActivity(new Intent(getApplicationContext(), PrivacyPolicyActivity.class));
                    drawerLayout.closeDrawers();
                    break;
                case R.id.menu_setting:
                    startActivity(new Intent(this, SettingActivity.class));
                    drawerLayout.closeDrawers();
                    break;
                case R.id.menu_sign_out:
                    LoadDialogSignOut();
                    drawerLayout.closeDrawers();
                    break;

            }
            return true;
        });

        View header = navigationView.getHeaderView(0);
        TextView tv_name = header.findViewById(R.id.my_profile_name_txt);
        TextView tv_email = header.findViewById(R.id.my_profile_email_txt);
        ImageView img_profile = header.findViewById(R.id.my_profile_image);

        tv_name.setText(new SessionManager(getApplicationContext()).GetUserName());
        tv_email.setText(new SessionManager(getApplicationContext()).GetUserEmail());

        Glide.with(this)
                .load(new SessionManager(getApplicationContext()).GetUserProfileUrl())
                .asBitmap()
                .placeholder(R.drawable.profile_image)
                .centerCrop()
                .into(new BitmapImageViewTarget(img_profile) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        img_profile.setImageDrawable(circularBitmapDrawable);
                    }
                });

        /*Glide.with(this)
                .load(new SessionManager(getApplicationContext()).GetUserProfileUrl())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .transform(new CircleTransform(this))
                .centerCrop()
                .into(img_profile);*/

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    protected void animateFab(final int position) {
        fabHomeCreateChannel.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                fabHomeCreateChannel.setImageDrawable(getResources().getDrawable(iconIntArray[position], null));

                // Scale up animation
                ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                fabHomeCreateChannel.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fabHomeCreateChannel.startAnimation(shrink);
    }

    private void SetTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition exit_transition =
                    TransitionInflater.from(this).
                            inflateTransition(R.transition.main_exit);
            Transition reenter_transition =
                    TransitionInflater.from(this).
                            inflateTransition(R.transition.main_reenter);
            w.setExitTransition(exit_transition);
            w.setReenterTransition(reenter_transition);
        }
    }

    private void InitPresenter() {
        homePresenter = new HomePresenter();
    }

    @Override
    public void onDiscard() {
        // Toast.makeText(MainActivity.this, R.string.dialog_discarded, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach() {
        homePresenter.onAttach(this);
    }

    @Override
    public void onDetach() {
        homePresenter.onDetach();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ServiceUtils.startServiceFriendChat(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        onDetach();
        //    ServiceUtils.startServiceFriendChat(getApplicationContext());
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();


        initNavigationDrawer();
        webView.reload();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (dialogFragment != null && dialogFragment.isAdded()) {
            dialogFragment.onBackPressed();
        } else {
            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                startActivity(intent);
                finish();
                System.exit(0);
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
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
            case R.id.menu_item_new_quote:
                LoadDialogSignOut();
                break;
            case R.id.menu_item_search:
                startActivity(new Intent(getApplicationContext(), SearchContactActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
//
//    @OnClick(R.id.fab_home_create_channel)
//    public void onViewClicked() {
//        int tab_position = tabsHome.getSelectedTabPosition();
//        if (tab_position == 0) {
//            startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
//        }
//        if (tab_position == 1) {
//            final Bundle args = new Bundle();
//            args.putString(CreateRadioFragment.EXTRA_NAME, "");
//
//            dialogFragment = new FullScreenDialogFragment.Builder(this)
//                    .setTitle("Create Radio")
//                    .setExtraActions(R.menu.menu_fullscreen_dialog)
//                    .setOnDiscardListener(this)
//                    .setContent(CreateRadioFragment.class, args)
//                    .build();
//
//            dialogFragment.show(getSupportFragmentManager(), "dialog");
//        }
//    }

    private void LoadDialogSignOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Signout");
        builder.setMessage("Are you sure do want to Logout");
        builder.setNegativeButton("NO",
                (dialog, which) -> {
                });
        builder.setPositiveButton("YES",
                (dialog, which) -> sessionManager.logoutUser());
        builder.show();
    }

    private void loadBase() {
        ShowProgress();
        ApiConfig getResponse = AppConfigForNotification.ApiClientForNotification().create(ApiConfig.class);
        Call<NotificationGetDataResponse> call = getResponse.getNotificationData(new NotificationGetDataRequest("", "", "", "", "", "", "1", "radio007"));
        call.enqueue(new Callback<NotificationGetDataResponse>() {
            @Override
            public void onResponse(Call<NotificationGetDataResponse> call, Response<NotificationGetDataResponse> response) {
                HideProgress();
                NotificationGetDataResponse serverResponse = response.body();
                if (response.isSuccessful()) {
                    if (serverResponse.getTable().size() > 0) {
                        SetAdapter(serverResponse.getTable1());
                        Log.e("test", "" + serverResponse.getTable());

                    } else {
                        Toast.makeText(getApplicationContext(), "" +
                                "No Data Available...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<NotificationGetDataResponse> call, Throwable t) {
                HideProgress();
                if (t instanceof ConnectException) {
                    Toast.makeText(getApplicationContext(), "Internet Error...", Toast.LENGTH_SHORT).show();
                } else if (t instanceof SocketTimeoutException) {
                    Toast.makeText(getApplicationContext(), "Timeout Error...", Toast.LENGTH_SHORT).show();
                } else {
                    // dashboardView.Error("Internet Error", "Please, Check your Internet Connection!");
                }
            }
        });
    }

    private void ShowProgress() {
        dialog.show();
    }

    private void HideProgress() {
        dialog.dismiss();
    }

    private void SetAdapter(List<NotificationGetDataResponse.Table1> tableItemList) {
        ShowNotificationDataAdapter editContactDetailAdapter = new ShowNotificationDataAdapter(this, tableItemList);
        recyclerView.setAdapter(editContactDetailAdapter);
    }
}
