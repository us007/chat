package utechandroid.com.radio.ui.message;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;

import com.prudenttechno.radioNotification.R;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.webianks.library.PopupBubble;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import utechandroid.com.radio.chatModels.ChatImage;
import utechandroid.com.radio.chatModels.ChatLocation;
import utechandroid.com.radio.chatModels.ChatMessage;
import utechandroid.com.radio.chatModels.ChatText;
import utechandroid.com.radio.chatModels.ChatUser;
import utechandroid.com.radio.chatModels.chatPdf;
import utechandroid.com.radio.data.db.helper.RealmHelper;
import utechandroid.com.radio.data.pref.SessionManager;
import utechandroid.com.radio.events.ChannelEvent;
import utechandroid.com.radio.firestore.helper.auth.FireStoreAuthHelper;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.Channel;
import utechandroid.com.radio.firestore.model.ChannelData;
import utechandroid.com.radio.firestore.model.MembersData;
import utechandroid.com.radio.firestore.model.Message;
import utechandroid.com.radio.firestore.model.MessageReadData;
import utechandroid.com.radio.firestore.model.User;
import utechandroid.com.radio.ui.ImageViewer.ImageViewerActivity;
import utechandroid.com.radio.ui.messageStatus.MessageStatusActivity;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;
import utechandroid.com.radio.util.ProgressBar.MaterialProgressBar;
import utechandroid.com.radio.util.RoundRectCornerImageView;
import utechandroid.com.radio.util.chatkit.messages.MessageInput;
import utechandroid.com.radio.util.chatkit.messages.MessagesList;
import utechandroid.com.radio.util.chatkit.messages.MessagesListAdapter;

import static utechandroid.com.radio.ui.home.HomeActivity.PERMISSIONS_MULTIPLE_REQUEST;

public class MessageActivity extends ColorsAppCompatActivity implements MessagesListAdapter.MessageAdapterListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.messagesList)
    MessagesList messagesList;
    @BindView(R.id.progress_message)
    MaterialProgressBar progressMessage;
    @BindView(R.id.popup_new_message)
    PopupBubble popupNewMessage;
    @BindView(R.id.input)
    MessageInput input;
    @BindView(R.id.txt_bottom_layout)
    TextView txtBottomLayout;

    private FirebaseFirestore db;
    private ChannelData channel;
    protected MessagesListAdapter messagesAdapter;

    private List<ChatMessage> chatMessages = new ArrayList<>();
    ListenerRegistration registration, messageReadListner, permissionListner;
    private int messages = 0;
    int firstVisibleItem = 0;

    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private Parcelable recyclerViewState;
    private ProgressDialog progressDialog;
    private ImagePicker imagePicker = new ImagePicker();
    private ArrayList<String> docPaths = new ArrayList<>();
    private static final String TAG = "MessageActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTransition();
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);

        SetProgressDialog();

        db = FirebaseFirestore.getInstance();

        actionModeCallback = new ActionModeCallback();
        messagesAdapter = new MessagesListAdapter(this, new FireStoreAuthHelper().GetUserId(), chatMessages, this);
        messagesList.setAdapter(messagesAdapter);
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(messagesAdapter);
        messagesList.addItemDecoration(headersDecor);
        messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        popupNewMessage.setRecyclerView(messagesList);

        messagesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                firstVisibleItem = messagesList.GetLayoutManager().findFirstVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    popupNewMessage.hide();
                }
            }
        });

        initToolbar();
        GetChannelData();

        popupNewMessage.setPopupBubbleListener(context -> {
            messagesList.smoothScrollToPosition(0);
            messages = 0;
        });

        input.setInputListener(input -> {
            if (isConnected(getApplicationContext())) {
                SendTextMessage(input.toString());
            } else {
                Toast.makeText(getApplicationContext(), "Check your internet connection!!!", Toast.LENGTH_LONG).show();
            }
            return true;
        });

        int notiId = new RealmHelper(getApplicationContext()).GetChannelNotificationId(channel.getChannelId());

        if (-22 == notiId) {

        } else {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(channel.getChannelId(), notiId);
        }

        input.setAttachmentsListener(() -> {
            AddAttachment();
        });


        imagePicker.setTitle("Choose Picture");
        imagePicker.setCropImage(true);
    }

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            int PLACE_PICKER_REQUEST = 1;
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Please Grant Permissions for media files",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(
                                            new String[]{Manifest.permission
                                                    .ACCESS_FINE_LOCATION},
                                            123);
                                }
                            }
                        }).show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission
                                    .ACCESS_FINE_LOCATION},
                            123);
                }
            }
        } else {
            int PLACE_PICKER_REQUEST = 1;
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraPermission) {
                        int PLACE_PICKER_REQUEST = 1;
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        try {
                            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Snackbar.make(this.findViewById(android.R.id.content),
                                "Please Grant Permissions to access Location",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(
                                                    new String[]{Manifest.permission
                                                            .ACCESS_FINE_LOCATION},
                                                    123);
                                        }
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

    private void LoadLocationPickup() {
        checkAndroidVersion();
    }

    private void AddAttachment() {
        final CharSequence[] list = {"Send Image", "Send Location", "Send Document File"};
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this)
                .setTitle("Choose a Attachment")
                .setSingleChoiceItems(list, -1, (dialog, which) -> {
                    if (list[which].equals("Send Image")) {
                        startChooser();
                    } else if (list[which].equals("Send Location")) {
                        LoadLocationPickup();
                    } else if (list[which].equals("Send Document File")) {
                        selectPdfFile();
                    }
                    dialog.dismiss();
                });
        builder2.show();
    }
    public static final void recreateActivityCompat(final Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            a.recreate();
        } else {
            final Intent intent = a.getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            a.finish();
            a.overridePendingTransition(0, 0);
            a.startActivity(intent);
            a.overridePendingTransition(0, 0);
        }
    }
    private void selectPdfFile() {
        String[] pdfType = new String[]{".pdf"};
        String[] docType = new String[]{".docx", ".doc"};
        String[] xlsxType = new String[]{".xls", ".xlsx"};
        // String pdfs = {".pdf",".doc",".docx"};
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(docPaths)

                .addFileSupport("Pdf", pdfType)
                .addFileSupport("Document", docType)
                .addFileSupport("Excel", xlsxType)

                .setActivityTheme(R.style.AppTheme)
                .enableDocSupport(false)
                .pickFile(this);
        /*Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, 1212);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                SendLocationMessage(place.getAddress().toString(), place.getLatLng());
                LoadChats();
            }
        } else if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
            if (resultCode == Activity.RESULT_OK && data != null) {

                docPaths = new ArrayList<>();
                docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                if (docPaths.size() > 0) {
                    Log.d("Chat message", "" + docPaths);
                    Uri testing = data(docPaths.get(0));
                    File file = new File(String.valueOf(testing));
                    File f = new File(testing.getPath());
                    long size = f.length();

                    Log.e("length ", "" + size);
                    String filename = file.getName();
                    String extension = filename.substring(filename.lastIndexOf("."));
                    String exfile = extension.substring(1);
                    Log.e("FIle ex", " " + exfile);
                    if (size < 5000000) {
                        SendPdfMessage("", docPaths.get(0), filename);
                    } else {
                        Toast.makeText(this, "Maximum file allowed 5mb", Toast.LENGTH_SHORT).show();
                    }


                }
            }

        }
      /*  else if (requestCode == 1212) {
            if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                String displayName = null;
                Toast.makeText(this, ""+uriString, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, ""+path, Toast.LENGTH_SHORT).show();
                Log.d("uri  ",""+uri);
SendPdfMessage("",uriString);
         Log.d("PDF file",path);
//                if (uriString.startsWith("content://")) {
//                    Cursor cursor = null;
//                    try {
//                        cursor = this.getContentResolver().query(uri, null, null, null, null);
//                        if (cursor != null && cursor.moveToFirst()) {
//                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                          //  Toast.makeText(this, ""+displayName, Toast.LENGTH_SHORT).show();
//                           // SendPdfMessage("",uriString);
//
//                        }
//                    } finally {
//                        cursor.close();
//                    }
//                } else if (uriString.startsWith("file://")) {
//                    displayName = myFile.getName();
//
//                }
            }
        }*/
        else {
            super.onActivityResult(requestCode, resultCode, data);
            imagePicker.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    private void startChooser() {
        imagePicker.startChooser(this, new ImagePicker.Callback() {
            @Override
            public void onPickImage(Uri imageUri) {

            }

            @Override
            public void onCropImage(Uri imageUri) {
                SendImageMessage("", imageUri.toString());
                //  imgProfile.setImageURI(imageUri);
            }

            @Override
            public void cropConfig(CropImage.ActivityBuilder builder) {
                builder.setGuidelines(CropImageView.Guidelines.OFF)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setRequestedSize(640, 640)
                        .setAspectRatio(5, 5);
            }

            // 用户拒绝授权回调
            @Override
            public void onPermissionDenied(int requestCode, String[] permissions,
                                           int[] grantResults) {
            }
        });
    }

    private void SetTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            w.setReturnTransition(new Fade());
            w.setEnterTransition(new Fade());
        }
    }

    private void LoadChats() {
        Query query = db.collection(Message.FIELD_COLLECTION)
                .document(channel.getChannelId())
                .collection(Message.FIELD_COLLECTION)
                .orderBy(Message.FIELD_MESSAGE_DATE,
                        Query.Direction.ASCENDING);

        registration = query.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }

            if (snapshot != null && !snapshot.isEmpty()) {
                for (DocumentChange change : snapshot.getDocumentChanges()) {
                    switch (change.getType()) {
                        case ADDED:
                            DocumentSnapshot doc = change.getDocument();
                            final Message messageList = doc.toObject(Message.class);
                            final Map<String, Object> data = messageList.getData();

                            if (messageList.getMessageType() == 1) {

                                ChatUser chatUser = new ChatUser(messageList.getMessageSender(), messageList.getMessageSenderName());

                                ChatText chatText = new ChatText();
                                chatText.setText(data.get(Message.FIELD_MESSAGE_DATA_MESSAGE).toString());

                                ChatMessage chatMessage = new ChatMessage();
                                chatMessage.setType(messageList.getMessageType());
                                chatMessage.setCreatedAt(messageList.getMessageDate());
                                chatMessage.setId(messageList.getMessageId());
                                chatMessage.setType(messageList.getMessageType());
                                chatMessage.setChatText(chatText);
                                chatMessage.setUser(chatUser);

                                if (messagesAdapter.GetMessageList().size() > 0) {
                                    if (!messagesAdapter.GetMessageList().contains(chatMessage)) {
                                        messagesAdapter.addToStart(chatMessage, true);
                                        if (firstVisibleItem > 10) {
                                            if (!new FireStoreAuthHelper().GetUserId().equals(chatUser.getId())) {
                                                messages++;
                                                popupNewMessage.updateText(messages == 1 ? messages + " New Message" : messages + " New Messages");
                                                popupNewMessage.show();
                                            }
                                        }
                                    }
                                } else {
                                    messagesAdapter.addToStart(chatMessage, true);
                                }
                            } else if (messageList.getMessageType() == 2) {

                                ChatUser chatUser = new ChatUser(messageList.getMessageSender(), messageList.getMessageSenderName());

                                ChatImage chatImage = new ChatImage();
                                chatImage.setText(data.get(Message.FIELD_MESSAGE_DATA_MESSAGE).toString());
                                chatImage.setStorageSize(Long.parseLong(data.get(Message.FIELD_MESSAGE_DATA_MESSAGE_SIZE).toString()));
                                chatImage.setStoragePath(data.get(Message.FIELD_MESSAGE_DATA_MESSAGE_IMAGE_URL).toString());
                                chatImage.setLocalPath(data.get(Message.FIELD_MESSAGE_DATA_MESSAGE_LOCAL_URL).toString());

                                ChatMessage chatMessage = new ChatMessage();
                                chatMessage.setType(messageList.getMessageType());
                                chatMessage.setCreatedAt(messageList.getMessageDate());
                                chatMessage.setId(messageList.getMessageId());
                                chatMessage.setType(messageList.getMessageType());
                                chatMessage.setChatImage(chatImage);
                                chatMessage.setUser(chatUser);

                                if (messagesAdapter.GetMessageList().size() > 0) {
                                    if (!messagesAdapter.GetMessageList().contains(chatMessage)) {
                                        messagesAdapter.addToStart(chatMessage, true);
                                        if (firstVisibleItem > 10) {
                                            if (!new FireStoreAuthHelper().GetUserId().equals(chatUser.getId())) {
                                                messages++;
                                                popupNewMessage.updateText(messages == 1 ? messages + " New Message" : messages + " New Messages");
                                                popupNewMessage.show();
                                            }
                                        }
                                    }
                                } else {
                                    messagesAdapter.addToStart(chatMessage, true);
                                }
                            } else if (messageList.getMessageType() == 3) {

                                ChatUser chatUser = new ChatUser(messageList.getMessageSender(), messageList.getMessageSenderName());
                                chatPdf pdf = new chatPdf();

                                pdf.setText(data.get(Message.FIELD_MESSAGE_DATA_MESSAGE).toString());
                                pdf.setStorageSize(Long.parseLong(data.get(Message.FIELD_MESSAGE_DATA_MESSAGE_SIZE).toString()));
                                pdf.setStoragePath(data.get(Message.FIELD_MESSAGE_DATA_MESSAGE_IMAGE_URL).toString());
                                pdf.setLocalPath(data.get(Message.FIELD_MESSAGE_DATA_MESSAGE_LOCAL_URL).toString());
                                pdf.setFilename(data.get(Message.FIELD_MESSAGE_FILE_NAME).toString());


                                ChatMessage chatMessage = new ChatMessage();
                                chatMessage.setType(messageList.getMessageType());
                                chatMessage.setCreatedAt(messageList.getMessageDate());
                                chatMessage.setId(messageList.getMessageId());
                                chatMessage.setType(messageList.getMessageType());
                                chatMessage.setChatPdf(pdf);
                                chatMessage.setUser(chatUser);

                                if (messagesAdapter.GetMessageList().size() > 0) {
                                    if (!messagesAdapter.GetMessageList().contains(chatMessage)) {
                                        messagesAdapter.addToStart(chatMessage, true);
                                        if (firstVisibleItem > 10) {
                                            if (!new FireStoreAuthHelper().GetUserId().equals(chatUser.getId())) {
                                                messages++;
                                                popupNewMessage.updateText(messages == 1 ? messages + " New Message" : messages + " New Messages");
                                                popupNewMessage.show();
                                            }
                                        }
                                    }
                                } else {
                                    messagesAdapter.addToStart(chatMessage, true);
                                }
                            } else if (messageList.getMessageType() == 5) {

                                ChatUser chatUser = new ChatUser(messageList.getMessageSender(), messageList.getMessageSenderName());

                                ChatLocation chatLocation = new ChatLocation();
                                chatLocation.setPlace(data.get(Message.FIELD_MESSAGE_DATA_MESSAGE_PLACE).toString());
                                chatLocation.setLatitude(Double.parseDouble(data.get(Message.FIELD_MESSAGE_DATA_MESSAGE_LATITUDE).toString()));
                                chatLocation.setLongitude(Double.parseDouble(data.get(Message.FIELD_MESSAGE_DATA_MESSAGE_LONGITUDE).toString()));

                                ChatMessage chatMessage = new ChatMessage();
                                chatMessage.setType(messageList.getMessageType());
                                chatMessage.setCreatedAt(messageList.getMessageDate());
                                chatMessage.setId(messageList.getMessageId());
                                chatMessage.setType(messageList.getMessageType());
                                chatMessage.setChatLocation(chatLocation);
                                chatMessage.setUser(chatUser);

                                if (messagesAdapter.GetMessageList().size() > 0) {
                                    if (!messagesAdapter.GetMessageList().contains(chatMessage)) {
                                        messagesAdapter.addToStart(chatMessage, true);
                                        if (firstVisibleItem > 10) {
                                            if (!new FireStoreAuthHelper().GetUserId().equals(chatUser.getId())) {
                                                messages++;
                                                popupNewMessage.updateText(messages == 1 ? messages + " New Message" : messages + " New Messages");
                                                popupNewMessage.show();
                                            }
                                        }
                                    }
                                } else {
                                    messagesAdapter.addToStart(chatMessage, true);
                                }
                            }

                            break;
                    }
                }
            }
        });
    }

    private void SaveReadMessages() {
        Query query = db.collection(MessageReadData.FIELD_COLLECTION)
                .document(channel.getChannelId())
                .collection(MessageReadData.FIELD_COLLECTION);

        messageReadListner = query.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                for (DocumentChange change : snapshot.getDocumentChanges()) {
                    switch (change.getType()) {
                        case ADDED:
                            DocumentSnapshot doc = change.getDocument();
                            MessageReadData user = doc.toObject(MessageReadData.class);
                            if (user.getMembers().entrySet().size() > 0) {
                                for (Map.Entry<String, Integer> f : user.getMembers().entrySet()) {
                                    String key = f.getKey();
                                    Integer value = f.getValue();
                                    if (key.equals(new FireStoreAuthHelper().GetUserId())) {
                                        if (value == 1 || value == 2) {
                                            WriteBatch batch = db.batch();
                                            DocumentReference radioRef = doc.getReference();
                                            Map<String, Object> radioUpdates = new HashMap<>();

                                            radioUpdates.put("members" + "." + new FireStoreAuthHelper().GetUserId(), 3);
                                            batch.update(radioRef, radioUpdates);

                                            batch.commit();
                                        }
                                    }
                                }
                            }
                            break;
                        case MODIFIED:

                            break;
                        case REMOVED:

                            break;
                    }
                }
            }
        });
    }

    private void SendLocationMessage(final String place, LatLng latLng) {

        double lat = latLng.latitude;
        double lng = latLng.longitude;

        WriteBatch batch = db.batch();

        DocumentReference docRef = new FireStoreHelper().GetChannelByIdDocument(channel.getChannelId());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            final ChannelData channelData = documentSnapshot.toObject(ChannelData.class);
            List<String> set = new ArrayList<>();
            for (Map.Entry<String, Integer> f : channelData.getMembers().entrySet()) {
                String key = f.getKey();
                Integer value = f.getValue();
                if (value == 2) {
                    set.add(key);
                }
            }

            if (!channel.getChannelAdminId().equals(new FireStoreAuthHelper().GetUserId())) {
                set.add(channel.getChannelAdminId());
            }

            Map<String, Object> messageData = new HashMap<>();
            messageData.put(Message.FIELD_MESSAGE_DATA_MESSAGE_PLACE, place);
            messageData.put(Message.FIELD_MESSAGE_DATA_MESSAGE_LATITUDE, lat);
            messageData.put(Message.FIELD_MESSAGE_DATA_MESSAGE_LONGITUDE, lng);

            final DocumentReference messageRef = new FireStoreHelper(this)
                    .GetMessageDocument(channel.getChannelId());
            String messageId = messageRef.getId();
            Message message = new Message();
            message.setMessageId(messageId);
            message.setMessageType(5);
            message.setData(messageData);
            message.setMessageChannel(channel.getChannelId());
            message.setMessageDate(new Date());
            message.setMessageSender(new SessionManager(getApplicationContext()).GetUserId());
            message.setMessageSenderName(new SessionManager(getApplicationContext()).GetUserName());
            batch.set(messageRef, message);

            DocumentReference userRef = new FireStoreHelper(this)
                    .GetMessageReadDocument(messageId, channel.getChannelId());
            Map<String, Integer> nestedData = new HashMap<>();
            for (int i = 0; i < set.size(); i++) {
                if (!set.get(i).equals(new FireStoreAuthHelper().GetUserId())) {
                    nestedData.put(set.get(i), 1);
                }
            }
            MessageReadData messageReadData1 = new MessageReadData();
            messageReadData1.setMessage(message);
            messageReadData1.setMembers(nestedData);
            batch.set(userRef, messageReadData1, SetOptions.merge());

            batch.commit().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
               /* Toast.makeText(getActivity(), "Channel Successfully Created", Toast.LENGTH_SHORT).show();
                dialogController.discard();*/
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void SendTextMessage(final String message1) {
        WriteBatch batch = db.batch();

        DocumentReference docRef = new FireStoreHelper().GetChannelByIdDocument(channel.getChannelId());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            final ChannelData channelData = documentSnapshot.toObject(ChannelData.class);
            List<String> set = new ArrayList<>();
            for (Map.Entry<String, Integer> f : channelData.getMembers().entrySet()) {
                String key = f.getKey();
                Integer value = f.getValue();
                if (value == 2) {
                    set.add(key);
                }
            }

            if (!channel.getChannelAdminId().equals(new FireStoreAuthHelper().GetUserId())) {
                set.add(channel.getChannelAdminId());
            }

            Map<String, Object> messageData = new HashMap<>();
            messageData.put(Message.FIELD_MESSAGE_DATA_MESSAGE, message1);

            final DocumentReference messageRef = new FireStoreHelper(this)
                    .GetMessageDocument(channel.getChannelId());
            String messageId = messageRef.getId();
            Message message = new Message();
            message.setMessageId(messageId);
            message.setMessageType(1);
            message.setData(messageData);
            message.setMessageChannel(channel.getChannelId());
            message.setMessageDate(new Date());
            message.setMessageSender(new SessionManager(getApplicationContext()).GetUserId());
            message.setMessageSenderName(new SessionManager(getApplicationContext()).GetUserName());
            batch.set(messageRef, message);

            DocumentReference userRef = new FireStoreHelper(this)
                    .GetMessageReadDocument(messageId, channel.getChannelId());
            Map<String, Integer> nestedData = new HashMap<>();
            for (int i = 0; i < set.size(); i++) {
                if (!set.get(i).equals(new FireStoreAuthHelper().GetUserId())) {
                    nestedData.put(set.get(i), 1);
                }
            }
            MessageReadData messageReadData1 = new MessageReadData();
            messageReadData1.setMessage(message);
            messageReadData1.setMembers(nestedData);
            batch.set(userRef, messageReadData1, SetOptions.merge());

            batch.commit().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
               /* Toast.makeText(getActivity(), "Channel Successfully Created", Toast.LENGTH_SHORT).show();
                dialogController.discard();*/
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void SendImageMessage(final String message1, final String localPath) {
        WriteBatch batch = db.batch();

        DocumentReference docRef = new FireStoreHelper().GetChannelByIdDocument(channel.getChannelId());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            final ChannelData channelData = documentSnapshot.toObject(ChannelData.class);
            List<String> set = new ArrayList<>();
            for (Map.Entry<String, Integer> f : channelData.getMembers().entrySet()) {
                String key = f.getKey();
                Integer value = f.getValue();
                if (value == 2) {
                    set.add(key);
                }
            }

            if (!channel.getChannelAdminId().equals(new FireStoreAuthHelper().GetUserId())) {
                set.add(channel.getChannelAdminId());
            }

            Map<String, Object> messageData = new HashMap<>();
            messageData.put(Message.FIELD_MESSAGE_DATA_MESSAGE, message1);
            messageData.put(Message.FIELD_MESSAGE_DATA_MESSAGE_IMAGE_URL, "");
            messageData.put(Message.FIELD_MESSAGE_DATA_MESSAGE_LOCAL_URL, localPath);
            messageData.put(Message.FIELD_MESSAGE_DATA_MESSAGE_SIZE, 0);

            final DocumentReference messageRef = new FireStoreHelper(this)
                    .GetMessageDocument(channel.getChannelId());
            String messageId = messageRef.getId();
            Message message = new Message();
            message.setMessageId(messageId);
            message.setMessageType(2);
            message.setData(messageData);
            message.setMessageChannel(channel.getChannelId());
            message.setMessageDate(new Date());
            message.setMessageSender(new SessionManager(getApplicationContext()).GetUserId());
            message.setMessageSenderName(new SessionManager(getApplicationContext()).GetUserName());
            batch.set(messageRef, message);

            DocumentReference userRef = new FireStoreHelper(this)
                    .GetMessageReadDocument(messageId, channel.getChannelId());
            Map<String, Integer> nestedData = new HashMap<>();
            for (int i = 0; i < set.size(); i++) {
                if (!set.get(i).equals(new FireStoreAuthHelper().GetUserId())) {
                    nestedData.put(set.get(i), 1);
                }
            }
            MessageReadData messageReadData1 = new MessageReadData();
            messageReadData1.setMessage(message);
            messageReadData1.setMembers(nestedData);
            batch.set(userRef, messageReadData1, SetOptions.merge());

            batch.commit().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
               /* Toast.makeText(getActivity(), "Channel Successfully Created", Toast.LENGTH_SHORT).show();
                dialogController.discard();*/
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void SendPdfMessage(final String message1, final String localPath, final String filename) {
        WriteBatch batch = db.batch();

        DocumentReference docRef = new FireStoreHelper().GetChannelByIdDocument(channel.getChannelId());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            final ChannelData channelData = documentSnapshot.toObject(ChannelData.class);
            List<String> set = new ArrayList<>();
            for (Map.Entry<String, Integer> f : channelData.getMembers().entrySet()) {
                String key = f.getKey();
                Integer value = f.getValue();
                if (value == 2) {
                    set.add(key);
                }
            }

            if (!channel.getChannelAdminId().equals(new FireStoreAuthHelper().GetUserId())) {
                set.add(channel.getChannelAdminId());
            }

            Map<String, Object> messageData = new HashMap<>();
            messageData.put(Message.FIELD_MESSAGE_FILE_NAME, filename);

            messageData.put(Message.FIELD_MESSAGE_DATA_MESSAGE, message1);
            messageData.put(Message.FIELD_MESSAGE_DATA_MESSAGE_IMAGE_URL, "");
            messageData.put(Message.FIELD_MESSAGE_DATA_MESSAGE_LOCAL_URL, localPath);
            messageData.put(Message.FIELD_MESSAGE_DATA_MESSAGE_SIZE, 0);

            final DocumentReference messageRef = new FireStoreHelper(this)
                    .GetMessageDocument(channel.getChannelId());
            String messageId = messageRef.getId();
            Message message = new Message();
            message.setMessageId(messageId);
            message.setMessageType(3);
            message.setData(messageData);
            message.setMessageChannel(channel.getChannelId());
            message.setMessageDate(new Date());
            message.setMessageSender(new SessionManager(getApplicationContext()).GetUserId());
            message.setMessageSenderName(new SessionManager(getApplicationContext()).GetUserName());
            batch.set(messageRef, message);

            DocumentReference userRef = new FireStoreHelper(this)
                    .GetMessageReadDocument(messageId, channel.getChannelId());
            Map<String, Integer> nestedData = new HashMap<>();
            for (int i = 0; i < set.size(); i++) {
                if (!set.get(i).equals(new FireStoreAuthHelper().GetUserId())) {
                    nestedData.put(set.get(i), 1);
                }
            }
            MessageReadData messageReadData1 = new MessageReadData();
            messageReadData1.setMessage(message);
            messageReadData1.setMembers(nestedData);
            batch.set(userRef, messageReadData1, SetOptions.merge());

            batch.commit().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
               /* Toast.makeText(getActivity(), "Channel Successfully Created", Toast.LENGTH_SHORT).show();
                dialogController.discard();*/
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void GetChannelData() {
        EventBus eventbus = EventBus.getDefault();
        ChannelEvent channelEvent = eventbus.getStickyEvent(ChannelEvent.class);
        if (channelEvent != null) {
            channel = channelEvent.getmChannel();
            getSupportActionBar().setTitle(channel.getChannelName());
            eventbus.removeStickyEvent(channelEvent);

            if (channel.getChannelAdminId().equals(new FireStoreAuthHelper().GetUserId())) {
                input.setVisibility(View.VISIBLE);
                txtBottomLayout.setVisibility(View.GONE);
            } else {
                CheckPermission();
            }

            messagesAdapter.setChannelId(channel.getChannelId());
        }
    }

    private void CheckPermission() {
        DocumentReference documentReference = new FireStoreHelper()
                .GetChannelByIdDocument(channel.getChannelId());
        permissionListner = documentReference.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }

            if (snapshot.exists()) {
                ChannelData channelData = snapshot.toObject(ChannelData.class);
                if (channelData.getMessageWritePermissions().entrySet().size() > 0) {
                    for (Map.Entry<String, Boolean> f : channelData.getMessageWritePermissions().entrySet()) {
                        String key = f.getKey();
                        if (key.equals(new FireStoreAuthHelper().GetUserId())) {
                            Boolean value2 = f.getValue();
                            if (value2) {
                                input.setVisibility(View.VISIBLE);
                                txtBottomLayout.setVisibility(View.GONE);
                            } else {
                                input.setVisibility(View.GONE);
                                txtBottomLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    input.setVisibility(View.GONE);
                    txtBottomLayout.setVisibility(View.VISIBLE);
                    txtBottomLayout.setText("Please, Add Atleast One Member in Channel to Send Message...");
                }
            }
        });
    }

    private void initToolbar() {
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void OnRadioEvent(ChannelEvent channelEvent) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        SaveReadMessages();
        LoadChats();
    }

    @Override
    protected void onPause() {
        if (registration != null) {
            registration.remove();
        }
        if (messageReadListner != null) {
            messageReadListner.remove();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        if (messageReadListner != null) {
            messageReadListner.remove();
        }
        if (registration != null) {
            registration.remove();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (channel.getChannelAdminId().equals(new FireStoreAuthHelper().GetUserId())) {
            // menu.findItem(R.id.menu_view_details).setVisible(true);
            menu.findItem(R.id.menu_unsubscribe).setVisible(false);
        } else {
            // menu.findItem(R.id.menu_view_details).setVisible(false);
            menu.findItem(R.id.menu_unsubscribe).setVisible(true);
        }
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
                String shareBody = "http://radiobroadcast.ooo/channelJoin?id=" + channel.getChannelId();
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Please, Join Channel through below...");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share"));
                break;
            /*case R.id.menu_view_details:
                Intent intent = new Intent(getApplicationContext(), ChannelDetailActivity.class);
                intent.putExtra("id", channel.getChannelId());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                break;*/
            case R.id.menu_notification:
                ShowNotificationDialog();
                break;
            case R.id.menu_unsubscribe:
                ShowUnSubscribeDialog();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowNotificationDialog() {
        if (new RealmHelper(getApplicationContext()).CheckChannelNotification(channel.getChannelId())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Notification Off");
            builder.setMessage("Tap on TURN OFF button for stop receiving Notification for this Channel!!!");
            builder.setPositiveButton("TURN OFF",
                    (dialog, which) -> {
                        new RealmHelper(getApplicationContext()).UpdateNotificationChannel(false, channel.getChannelId());
                    });
            builder.show();
        } else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Notification On");
            builder1.setMessage("Tap on TURN On button for start receiving Notification for this Channel!!!");
            builder1.setPositiveButton("TURN ON",
                    (dialog, which) -> {
                        new RealmHelper(getApplicationContext()).UpdateNotificationChannel(true, channel.getChannelId());
                    });
            builder1.show();
        }
    }

    private void ShowUnSubscribeDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("UnSubscribe");
        builder1.setMessage("Are you sure do you want UnSubscribe Channel?");
        builder1.setPositiveButton("Yes",
                (dialog, which) -> {
                    CheckSub();
                });
        builder1.setNegativeButton("No",
                (dialog, which) -> {

                });
        builder1.show();
    }

    private void CheckSub() {
        progressDialog.setTitle("Loading...");
        ProgressShow();
        new FireStoreHelper().GetMemberCollection(channel.getChannelId())
                .whereEqualTo(MembersData.FIELD_MEMBER_USER_ID,
                        new FireStoreAuthHelper().GetUserId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> snapshots = task.getResult().getDocuments();
                            if (snapshots.size() > 0) {
                                MembersData membersDataList = snapshots.get(0).toObject(MembersData.class);
                                UnSubscribe(membersDataList);
                            } else {
                                ProgressHide();
                            }
                        }
                    }
                });

    }

    private void UnSubscribe(MembersData membersData) {
        progressDialog.setTitle("UnSubscribing");

        String memberId = membersData.getMemberId();
        WriteBatch batch = db.batch();

        DocumentReference userRef = new FireStoreHelper(this).GetCurrentUserDocument();
        Map<String, Object> updates = new HashMap<>();
        updates.put(User.FIELD_SUBSCRIBE_COLLECTION + "." + channel.getChannelId(), FieldValue.delete());
        batch.update(userRef, updates);

        DocumentReference radioRef = db.collection(Channel.FIELD_COLLECTION).document(channel.getChannelId());
        Map<String, Object> radioUpdates = new HashMap<>();
        radioUpdates.put(MembersData.FIELD_COLLECTION + "." + new FireStoreAuthHelper().GetUserId(), FieldValue.delete());
        batch.update(radioRef, radioUpdates);

        DocumentReference channelPermissionRef = db.collection(Channel.FIELD_COLLECTION).document(channel.getChannelId());
        Map<String, Object> channelPermissionData = new HashMap<>();
        channelPermissionData.put(ChannelData.FIELD_COLLECTION_MEMBERS_WRITE_MESSAGE + "." + new FireStoreAuthHelper().GetUserId(), FieldValue.delete());
        batch.update(channelPermissionRef, channelPermissionData);

        final DocumentReference memberRef = new FireStoreHelper(this).GetMemberDocument(channel.getChannelId(), memberId);
        batch.delete(memberRef);

        batch.commit().addOnCompleteListener(task -> {
            ProgressHide();
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "UnSubscribe Successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }

    private void SetProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
    }

    private void ProgressShow() {
        progressDialog.show();
    }

    private void ProgressHide() {
        progressDialog.dismiss();
    }

    @Override
    public void onRowLongClicked(int position) {
        enableActionMode(position);
    }

    @Override
    public void onImageClick(RoundRectCornerImageView imageView, String uri) {
        Intent intent = new Intent(this, ImageViewerActivity.class);
        intent.putExtra("uri", uri);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this,
                        imageView,
                        "imageTransition");
        startActivity(intent, options.toBundle());
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        messagesAdapter.toggleSelection(position);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int count = messagesAdapter.getSelectedItemCount();
                actionMode.setTitle(String.valueOf(count));
                actionMode.invalidate();
                if (count == 0) {
                    actionMode.finish();
                } else if (count == 1) {
                    ChatMessage chatMessage = messagesAdapter.getMessageData(position);
                    if (chatMessage.getUser().getId().equals(new FireStoreAuthHelper().GetUserId())) {
                        Menu menu = actionMode.getMenu();
                        menu.findItem(R.id.action_info_message).setVisible(true);
                        menu.findItem(R.id.action_copy_message).setVisible(true);
                        menu.findItem(R.id.action_delete_message).setVisible(false);

                    } else if (channel.getChannelAdminId().equals(new FireStoreAuthHelper().GetUserId())) {
                        Menu menu = actionMode.getMenu();
                        menu.findItem(R.id.action_delete_message).setVisible(true);
                        menu.findItem(R.id.action_info_message).setVisible(true);
                        menu.findItem(R.id.action_copy_message).setVisible(true);

                    } else {
                        Menu menu = actionMode.getMenu();
                        menu.findItem(R.id.action_info_message).setVisible(false);
                        menu.findItem(R.id.action_copy_message).setVisible(true);
                        menu.findItem(R.id.action_delete_message).setVisible(false);
                    }
                } else {
                    Menu menu = actionMode.getMenu();
                    menu.findItem(R.id.action_copy_message).setVisible(false);
                    menu.findItem(R.id.action_info_message).setVisible(false);
                    menu.findItem(R.id.action_delete_message).setVisible(false);
                }
            }
        }, 100);
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_chat_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_info_message:
                    // delete all the selected messages
                    MessageInfo();
                    mode.finish();
                    return true;
                case R.id.action_copy_message:
                    // copy all the selected messages
                    CopyText();
                    mode.finish();
                    return true;
                case R.id.action_delete_message:
                    deleteText();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            messagesAdapter.clearSelections();
            actionMode = null;
        }

    }

    private void deleteText() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Delete For EveryOne")
                .setMessage("Are you sure you want to delete this Message?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                                List<Integer> selectedItemPositions = messagesAdapter.getSelectedItems();
        if (selectedItemPositions.size() == 1) {
            ChatMessage chatMessage = messagesAdapter.getMessageData(selectedItemPositions.get(0));
            Log.d(TAG, "Message id:" + chatMessage.getId());
            Log.d(TAG, "channel id:" + channel.getChannelId());
            db.collection("messages").document(channel.getChannelId()).collection("messages").document(chatMessage.getId()).delete().addOnSuccessListener(aVoid -> {

//                    chatMessages.remove(selectedItemPositions.get(0));
//                    messagesList.removeViewAt(selectedItemPositions.get(0));
//                    messagesAdapter.notifyItemRemoved(selectedItemPositions.get(0));
//                    messagesAdapter.notifyItemRangeChanged(selectedItemPositions.get(0), chatMessages.size());


                Log.d(TAG, "DocumentSnapshot successfully deleted!");

            }).addOnFailureListener(e -> Log.d(TAG, "Error deleting document", e));
        }
                         messagesAdapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    private void MessageInfo() {
        List<Integer> selectedItemPositions = messagesAdapter.getSelectedItems();
        if (selectedItemPositions.size() == 1) {
            ChatMessage chatMessage = messagesAdapter.getMessageData(selectedItemPositions.get(0));
            Intent intent = new Intent(getApplicationContext(), MessageStatusActivity.class);
            intent.putExtra("messageId", chatMessage.getId());
            intent.putExtra("channelId", channel.getChannelId());
            startActivity(intent);
        }
    }

    private void CopyText() {
        List<Integer> selectedItemPositions = messagesAdapter.getSelectedItems();
        if (selectedItemPositions.size() == 1) {
            ChatMessage chatMessage = messagesAdapter.getMessageData(selectedItemPositions.get(0));
            int sdk = Build.VERSION.SDK_INT;
            if (sdk < Build.VERSION_CODES.HONEYCOMB) {
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                if (chatMessage.getType() == 1) {
                    clipboard.setText(chatMessage.getChatText().getText());
                }
            } else {
                if (chatMessage.getType() == 1) {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                            getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData
                            .newPlainText("Message", chatMessage.getChatText().getText());
                    clipboard.setPrimaryClip(clip);
                }
            }
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Message is copied", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        if (messagesAdapter.getSelectedItemCount() > 0) {
            toggleSelection(position);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        recyclerViewState = messagesList.getLayoutManager().onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        messagesList.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connec = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {

            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {

            return false;
        }
        return false;

    }

    private Uri data(String pdf) {
        return Uri.fromFile(new File(pdf));
    }
}


