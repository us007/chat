package utechandroid.com.radio.util.chatkit.messages;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dmitrymalkovich.android.ProgressFloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.prudenttechno.radioNotification.R;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import utechandroid.com.radio.chatModels.ChatLocation;
import utechandroid.com.radio.chatModels.ChatMessage;
import utechandroid.com.radio.firestore.helper.dbRefrence.FireStoreHelper;
import utechandroid.com.radio.firestore.model.Message;
import utechandroid.com.radio.firestore.model.MessageReadData;
import utechandroid.com.radio.util.RoundRectCornerImageView;
import utechandroid.com.radio.util.chatkit.utils.DateFormatter;

public class MessagesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements StickyRecyclerHeadersAdapter, RecyclerScrollMoreListener.OnLoadMoreListener, MessagesListInterface {

    private static final short VIEW_TYPE_DATE_HEADER = 0;
    private static final short VIEW_TYPE_TEXT_MESSAGE = 1;
    private static final short VIEW_TYPE_IMAGE_MESSAGE = 2;
    private static final short VIEW_TYPE_LOCATION_MESSAGE = 5;
    public static final short VIEW_TYPE_PDF_MESSAGE = 3;
    private List<ChatMessage> items;
    private MessagesListStyle style;
    private String mSenderId;
    private OnLoadMoreListener loadMoreListener;
    private RecyclerView.LayoutManager layoutManager;
    private MessageAdapterListener listener;
    private SparseBooleanArray selectedItems;
    private static int currentSelectedIndex = -1;
    private String channelId;
    private Context mContext;

    StorageReference storageRef, imageRef;
    FirebaseStorage storage;
    UploadTask uploadTask;
    private final HashSet<MapView> mMaps = new HashSet<MapView>();

    public MessagesListAdapter(Context context, String senderId, List<ChatMessage> list, MessageAdapterListener listener) {
        this.items = list;
        this.mSenderId = senderId;
        this.listener = listener;
        this.mContext = context;
        selectedItems = new SparseBooleanArray();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public long getHeaderId(int position) {
        if (position == 0) {
            return -1;
        } else {
            return getItem(position).getDateOnly().getTime();
        }
    }

    private ChatMessage getItem(int position) {
        return items.get(position);
    }

    @Override
    public void loaded() {

    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_header, parent, false);
        return new DefaultDateHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DefaultDateHeaderViewHolder) {
            if (getItem(position).getGetDateName() != null) {
                String header = null;
                try {
                    header = formatToYesterdayOrToday(getItem(position).getGetDateName());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ((DefaultDateHeaderViewHolder) holder).text.setText(header);
            }
        }
    }

    class IncomingTextMessageViewHolder extends RecyclerView.ViewHolder {

        protected ViewGroup bubble;
        protected TextView text;
        protected TextView senderName;
        protected TextView textTime;
        protected RelativeLayout relativeLayout;

        public IncomingTextMessageViewHolder(View itemView) {
            super(itemView);
            bubble = (ViewGroup) itemView.findViewById(R.id.bubble);
            text = (TextView) itemView.findViewById(R.id.messageText);
            senderName = (TextView) itemView.findViewById(R.id.messageSender);
            textTime = (TextView) itemView.findViewById(R.id.messageTime);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout_chat_message);
        }
    }

    class OutcomingTextMessageViewHolder extends RecyclerView.ViewHolder {

        protected ViewGroup bubble;
        protected TextView text;
        protected TextView textTime;
        protected RelativeLayout relativeLayout;

        public OutcomingTextMessageViewHolder(View itemView) {
            super(itemView);
            bubble = (ViewGroup) itemView.findViewById(R.id.bubble);
            text = (TextView) itemView.findViewById(R.id.messageText);
            textTime = (TextView) itemView.findViewById(R.id.messageTime);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout_chat_message);
        }
    }

    class IncomingImageMessageViewHolder extends RecyclerView.ViewHolder {

        protected RoundRectCornerImageView image;
        protected ProgressBar circularProgressBar;
        protected ProgressFloatingActionButton progressFloatingActionButton;
        protected ViewGroup bubble;
        protected TextView text;
        protected TextView textTime;
        protected TextView senderName;
        protected RelativeLayout relativeLayout;

        public IncomingImageMessageViewHolder(View itemView) {
            super(itemView);
            image = (RoundRectCornerImageView) itemView.findViewById(R.id.image);
            progressFloatingActionButton = (ProgressFloatingActionButton) itemView.findViewById(R.id.fab_download);
            circularProgressBar = (ProgressBar) itemView.findViewById(R.id.fab_progressBar);
            bubble = (ViewGroup) itemView.findViewById(R.id.bubble);
            text = (TextView) itemView.findViewById(R.id.messageText);
            textTime = (TextView) itemView.findViewById(R.id.messageTime);
            senderName = (TextView) itemView.findViewById(R.id.messageSender);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout_chat_message);

        }
    }

    class OutcomingImageMessageViewHolder extends RecyclerView.ViewHolder {

        protected RoundRectCornerImageView image;
        protected ProgressBar circularProgressBar;
        protected ProgressFloatingActionButton progressFloatingActionButton;
        protected ViewGroup bubble;
        protected TextView text;
        protected TextView textTime;
        protected RelativeLayout relativeLayout;

        public OutcomingImageMessageViewHolder(View itemView) {
            super(itemView);
            image = (RoundRectCornerImageView) itemView.findViewById(R.id.image);
            progressFloatingActionButton = (ProgressFloatingActionButton) itemView.findViewById(R.id.fab_download);
            circularProgressBar = (ProgressBar) itemView.findViewById(R.id.fab_progressBar);
            bubble = (ViewGroup) itemView.findViewById(R.id.bubble);
            text = (TextView) itemView.findViewById(R.id.messageText);
            textTime = (TextView) itemView.findViewById(R.id.messageTime);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout_chat_message);

        }
    }

    class IncomingPdfMessageViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvDocument;
        protected ProgressBar circularProgressBar;
        protected ProgressFloatingActionButton progressFloatingActionButton;
        protected ViewGroup bubble;
        protected TextView text;
        protected TextView textTime;
        protected TextView senderName;
        protected RelativeLayout relativeLayout;

        public IncomingPdfMessageViewHolder(View itemView) {
            super(itemView);
            tvDocument = itemView.findViewById(R.id.pdf);
            progressFloatingActionButton = (ProgressFloatingActionButton) itemView.findViewById(R.id.fab_download);
            circularProgressBar = (ProgressBar) itemView.findViewById(R.id.fab_progressBar);
            bubble = (ViewGroup) itemView.findViewById(R.id.bubble);
            text = (TextView) itemView.findViewById(R.id.messageText);
            textTime = (TextView) itemView.findViewById(R.id.messageTime);
            senderName = (TextView) itemView.findViewById(R.id.messageSender);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout_chat_message);

        }
    }

    class OutcomingPdfMessageViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvDocument;
        protected ProgressBar circularProgressBar;
        protected ProgressFloatingActionButton progressFloatingActionButton;
        protected ViewGroup bubble;
        protected TextView text;
        protected TextView textTime;
        protected RelativeLayout relativeLayout;


        public OutcomingPdfMessageViewHolder(View itemView) {
            super(itemView);
            tvDocument = itemView.findViewById(R.id.pdf);
            progressFloatingActionButton = (ProgressFloatingActionButton) itemView.findViewById(R.id.fab_download);
            circularProgressBar = (ProgressBar) itemView.findViewById(R.id.fab_progressBar);
            bubble = (ViewGroup) itemView.findViewById(R.id.bubble);
            text = (TextView) itemView.findViewById(R.id.messageText);
            textTime = (TextView) itemView.findViewById(R.id.messageTime);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout_chat_message);

        }
    }

    class IncomingLocationMessageViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

        protected MapView image;
        GoogleMap map;
        protected ViewGroup bubble;
        protected TextView text;
        protected TextView textTime;
        protected TextView senderName;
        protected RelativeLayout relativeLayout;

        public IncomingLocationMessageViewHolder(View itemView) {
            super(itemView);
            image = (MapView) itemView.findViewById(R.id.lite_map);
            bubble = (ViewGroup) itemView.findViewById(R.id.bubble);
            text = (TextView) itemView.findViewById(R.id.messageText);
            textTime = (TextView) itemView.findViewById(R.id.messageTime);
            senderName = (TextView) itemView.findViewById(R.id.messageSender);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout_chat_message);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(mContext);
            map = googleMap;
            ChatLocation data = (ChatLocation) image.getTag();
            if (data != null) {
                setMapLocation(map, data);
            }
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        public void initializeMapView() {
            if (image != null) {
                // Initialise the MapView
                image.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                image.getMapAsync(this);
                image.onResume();

            }
        }
    }

    class OutcomingLocationMessageViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

        GoogleMap map;
        protected MapView image;
        protected ViewGroup bubble;
        protected TextView text;
        protected TextView textTime;
        protected RelativeLayout relativeLayout;

        public OutcomingLocationMessageViewHolder(View itemView) {
            super(itemView);
            image = (MapView) itemView.findViewById(R.id.lite_map);
            bubble = (ViewGroup) itemView.findViewById(R.id.bubble);
            text = (TextView) itemView.findViewById(R.id.messageText);
            textTime = (TextView) itemView.findViewById(R.id.messageTime);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout_chat_message);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(mContext);
            map = googleMap;
            ChatLocation data = (ChatLocation) image.getTag();
            if (data != null) {
                setMapLocation(map, data);
            }
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        public void initializeMapView() {
            if (image != null) {
                // Initialise the MapView
                image.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                image.getMapAsync(this);
                image.onResume();
            }
        }
    }

    class DefaultDateHeaderViewHolder extends RecyclerView.ViewHolder {

        protected TextView text;
        protected String dateFormat;
        protected DateFormatter.Formatter dateHeadersFormatter;

        public DefaultDateHeaderViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.messageText);
        }

    }

    private static void setMapLocation(GoogleMap map, ChatLocation data) {
        LatLng latLng = new LatLng(data.getLatitude(), data.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4f));
        map.addMarker(new MarkerOptions().position(latLng));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (VIEW_TYPE_DATE_HEADER == viewType) {
            View v1 = inflater.inflate(R.layout.item_date_header, parent,
                    false);
            viewHolder = new DefaultDateHeaderViewHolder(v1);
        } else if (VIEW_TYPE_TEXT_MESSAGE == viewType) {
            View v1 = inflater.inflate(R.layout.item_incoming_text_message, parent,
                    false);
            viewHolder = new IncomingTextMessageViewHolder(v1);
        } else if (-VIEW_TYPE_TEXT_MESSAGE == viewType) {
            View v1 = inflater.inflate(R.layout.item_outcoming_text_message, parent,
                    false);
            viewHolder = new OutcomingTextMessageViewHolder(v1);
        } else if (VIEW_TYPE_IMAGE_MESSAGE == viewType) {
            View v1 = inflater.inflate(R.layout.item_incoming_image_message, parent,
                    false);
            viewHolder = new IncomingImageMessageViewHolder(v1);
        } else if (-VIEW_TYPE_IMAGE_MESSAGE == viewType) {
            View v1 = inflater.inflate(R.layout.item_outcoming_image_message, parent,
                    false);
            viewHolder = new OutcomingImageMessageViewHolder(v1);
        } else if (VIEW_TYPE_LOCATION_MESSAGE == viewType) {
            View v1 = inflater.inflate(R.layout.item_incoming_location_message, parent,
                    false);
            viewHolder = new IncomingLocationMessageViewHolder(v1);
        } else if (-VIEW_TYPE_LOCATION_MESSAGE == viewType) {
            View v1 = inflater.inflate(R.layout.item_outcoming_location_message, parent,
                    false);
            viewHolder = new OutcomingLocationMessageViewHolder(v1);
        } else if (VIEW_TYPE_PDF_MESSAGE == viewType) {
            View v1 = inflater.inflate(R.layout.item_incoming_pdf_message, parent,
                    false);
            viewHolder = new IncomingPdfMessageViewHolder(v1);
        } else if (-VIEW_TYPE_PDF_MESSAGE == viewType) {
            View v1 = inflater.inflate(R.layout.item_outcoming_pdf_message, parent,
                    false);
            viewHolder = new OutcomingPdfMessageViewHolder(v1);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = viewHolder.getItemViewType();

        if (VIEW_TYPE_TEXT_MESSAGE == viewType) {
            ChatMessage chatMessage = (ChatMessage) items.get(position);
            IncomingTextMessageViewHolder holder = (IncomingTextMessageViewHolder) viewHolder;

            holder.itemView.setActivated(selectedItems.get(position, false));

            if (holder.text != null) {
                holder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getIncomingTextSize());
                holder.text.setTypeface(holder.text.getTypeface(), style.getIncomingTextStyle());
                holder.text.setAutoLinkMask(style.getTextAutoLinkMask());
                holder.text.setText(chatMessage.getChatText().getText());
            }

            if (holder.senderName != null) {
                holder.senderName.setText(chatMessage.getUser().getName());
            }

            if (holder.textTime != null) {
                holder.textTime.setText(GetTime(chatMessage.getCreatedAt().toString()));
            }

            holder.relativeLayout.setOnLongClickListener(view -> {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            });
            holder.relativeLayout.setOnClickListener(view -> {
                listener.onMessageRowClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            });

        } else if (-VIEW_TYPE_TEXT_MESSAGE == viewType) {
            ChatMessage chatMessage = (ChatMessage) items.get(position);
            OutcomingTextMessageViewHolder holder = (OutcomingTextMessageViewHolder) viewHolder;

            DocumentReference documentReference = new FireStoreHelper().GetMessageReadDocument(chatMessage.getId(), channelId);
            documentReference.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    return;
                }
                if (snapshot.exists()) {
                    MessageReadData user = snapshot.toObject(MessageReadData.class);
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

                      /*  if(readbylist.size()>0 && sentbylist.size()== user.getMembers().entrySet().size()){
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_unread_message, 0);
                        }*/

                        if (readbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_read_message, 0);
                        } else if (unreadbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_unread_message, 0);
                        } else if (unreadbylist.size() + readbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_unread_message, 0);
                        } else {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sent_message, 0);
                        }
                    }
                }
            });

            holder.itemView.setActivated(selectedItems.get(position, false));

            if (holder.text != null) {
                holder.text.setText(chatMessage.getChatText().getText());
                holder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getOutcomingTextSize());
                holder.text.setTypeface(holder.text.getTypeface(), style.getOutcomingTextStyle());
                holder.text.setAutoLinkMask(style.getTextAutoLinkMask());
            }

            if (holder.textTime != null) {
                holder.textTime.setText(GetTime(chatMessage.getCreatedAt().toString()));
            }

            holder.relativeLayout.setOnLongClickListener(view -> {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            });
            holder.relativeLayout.setOnClickListener(view -> {
                listener.onMessageRowClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            });

        } else if (-VIEW_TYPE_PDF_MESSAGE == viewType) {

            ChatMessage chatMessage = (ChatMessage) items.get(position);
            OutcomingPdfMessageViewHolder holder = (OutcomingPdfMessageViewHolder) viewHolder;

            holder.circularProgressBar.getIndeterminateDrawable().setColorFilter(getThemeAccentColor(mContext), PorterDuff.Mode.MULTIPLY);
            String filename = chatMessage.getChatPdf().getFilename();
            holder.tvDocument.setText(filename);

            if (chatMessage.getChatPdf().getStorageSize() == 0) {

                File file = new File(chatMessage.getChatPdf().getLocalPath());
                //   String extension = uri.toString().substring(uri.toString().lastIndexOf("."));

                //   String filename = chatMessage.getChatPdf().getFilename();
                String extension = filename.substring(filename.lastIndexOf("."));
                String exfile = extension.substring(1);
                holder.tvDocument.setText(exfile);
                Log.e("file name ", filename);

                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    //   holder.image.setImageURI(uri);
                    uploadPdf(uri, chatMessage.getId(), filename, holder, position);
                }
            } else {
                File file = new File(chatMessage.getChatPdf().getLocalPath());
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    //  holder.image.setImageURI(uri);
                    holder.progressFloatingActionButton.setVisibility(View.INVISIBLE);
                    holder.circularProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    //  holder.image.setImageResource(R.drawable.placeholder);
                    holder.progressFloatingActionButton.setVisibility(View.INVISIBLE);
                    holder.circularProgressBar.setVisibility(View.INVISIBLE);
                }
            }

            if (holder.text != null) {
                holder.text.setText(chatMessage.getChatPdf().getText());
                holder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getOutcomingTextSize());
                holder.text.setTypeface(holder.text.getTypeface(), style.getOutcomingTextStyle());
                holder.text.setAutoLinkMask(style.getTextAutoLinkMask());
                if (chatMessage.getChatPdf().getText().isEmpty()) {
                    holder.text.setVisibility(View.GONE);
                } else {
                    holder.text.setVisibility(View.VISIBLE);
                }
            }

            if (holder.textTime != null) {
                holder.textTime.setText(GetTime(chatMessage.getCreatedAt().toString()));
            }

            DocumentReference documentReference = new FireStoreHelper().GetMessageReadDocument(chatMessage.getId(), channelId);
            documentReference.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    return;
                }
                if (snapshot.exists()) {
                    MessageReadData user = snapshot.toObject(MessageReadData.class);
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

                        if (readbylist.size() > 0 && sentbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_unread_message, 0);
                        }

                        if (readbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_read_message, 0);
                        } else if (unreadbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_unread_message, 0);
                        } else if (unreadbylist.size() + readbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_unread_message, 0);
                        } else {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sent_message, 0);
                        }
                    }

                }
            });

            holder.itemView.setActivated(selectedItems.get(position, false));

            holder.relativeLayout.setOnLongClickListener(view -> {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            });
            holder.relativeLayout.setOnClickListener(view -> {
                listener.onMessageRowClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            });
        } else if (VIEW_TYPE_PDF_MESSAGE == viewType) {
            ChatMessage chatMessage = (ChatMessage) items.get(position);
            IncomingPdfMessageViewHolder holder = (IncomingPdfMessageViewHolder) viewHolder;

            File myDir = Environment.getExternalStorageDirectory();
            String file_dir = "RadioBroadcast/RadioBroadcast Documents";
            File fileFolder = new File(myDir, file_dir);
            fileFolder.mkdirs();
            String publicC = chatMessage.getChatPdf().getFilename() + "";
            File imageFolder = new File(fileFolder, publicC);

            holder.circularProgressBar.getIndeterminateDrawable().setColorFilter(getThemeAccentColor(mContext), PorterDuff.Mode.MULTIPLY);

            if (imageFolder.exists()) {
                holder.progressFloatingActionButton.setVisibility(View.INVISIBLE);
                holder.circularProgressBar.setVisibility(View.INVISIBLE);
                Uri uri = Uri.fromFile(imageFolder);
                //  holder.image.setImageURI(uri);
            } else {
                //  holder.image.setImageResource(R.drawable.placeholder);
                holder.progressFloatingActionButton.setVisibility(View.VISIBLE);
                holder.circularProgressBar.setVisibility(View.INVISIBLE);
                holder.progressFloatingActionButton.setOnClickListener(view -> {
                    holder.circularProgressBar.setVisibility(View.VISIBLE);
                    DocumentReference documentReference = new FireStoreHelper()
                            .GetMessageDocument(channelId, chatMessage.getId());
                    documentReference.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                final Message message = document.toObject(Message.class);
                                final Map<String, Object> data = message.getData();
                                String url = data.get(Message.FIELD_MESSAGE_DATA_MESSAGE_IMAGE_URL).toString();
                                String filename = data.get(Message.FIELD_MESSAGE_FILE_NAME).toString();
                                Log.e("file name ", filename);
                                if (url.isEmpty()) {
                                    LoadAlertDialog("Error", "Image is not uploaded yet,Please Try again");
                                } else {
                                    StorageReference storageRef = storage.getReferenceFromUrl(url);
                                    if (!fileFolder.exists()) {
                                        fileFolder.mkdirs();
                                    }
                                    final File localFile = new File(fileFolder, chatMessage.getChatPdf().getFilename());
                                    storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            notifyDataSetChanged();
                                            holder.progressFloatingActionButton.setVisibility(View.INVISIBLE);
                                            holder.circularProgressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }).addOnProgressListener(taskSnapshot -> {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        holder.circularProgressBar.setMax((int) taskSnapshot.getTotalByteCount());
                                        holder.circularProgressBar.setProgress((int) progress);
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            LoadAlertDialog("Error", "Something went wrong...Try again");
                                            holder.progressFloatingActionButton.setVisibility(View.VISIBLE);
                                            holder.circularProgressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            } else {
                                LoadAlertDialog("Error", "No Data Available");
                                holder.progressFloatingActionButton.setVisibility(View.VISIBLE);
                                holder.circularProgressBar.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            LoadAlertDialog("Error", task.getException().toString());
                            holder.progressFloatingActionButton.setVisibility(View.VISIBLE);
                            holder.circularProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                });
            }

            holder.itemView.setActivated(selectedItems.get(position, false));

            if (holder.text != null) {
                holder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getIncomingTextSize());
                holder.text.setTypeface(holder.text.getTypeface(), style.getIncomingTextStyle());
                holder.text.setAutoLinkMask(style.getTextAutoLinkMask());
                holder.text.setText(chatMessage.getChatPdf().getText());
                if (chatMessage.getChatPdf().getText().isEmpty()) {
                    holder.text.setVisibility(View.GONE);
                } else {
                    holder.text.setVisibility(View.VISIBLE);
                }
            }

            if (holder.senderName != null) {
                holder.senderName.setText(chatMessage.getUser().getName());
            }
            if (holder.tvDocument != null) {
                holder.tvDocument.setText("" + publicC);
            }
            if (holder.textTime != null) {
                holder.textTime.setText(GetTime(chatMessage.getCreatedAt().toString()));
            }

            holder.relativeLayout.setOnLongClickListener(view -> {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            });
            holder.relativeLayout.setOnClickListener(view -> {
                listener.onMessageRowClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            });

            holder.bubble.setOnClickListener(view -> {
                if (imageFolder.exists()) {
                    String extension = publicC.substring(publicC.lastIndexOf("."));
                    String exfile = extension.substring(1);
                    Log.e("extension", exfile);
                    if (exfile.equals("docx") || exfile.equals("doc")) {
                        Uri uri = Uri.fromFile(imageFolder);
                        Intent target = new Intent(Intent.ACTION_VIEW);
                        target.setDataAndType(uri, "application/msword");
                        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                        Intent intent = Intent.createChooser(target, "Open File");
                        try {
                            mContext.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            // Instruct the user to install a PDF reader here, or something
                        }
                    } else if (exfile.equals("pdf")) {
                        Uri uri = Uri.fromFile(imageFolder);
                        Intent target = new Intent(Intent.ACTION_VIEW);
                        target.setDataAndType(uri, "application/pdf");
                        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                        Intent intent = Intent.createChooser(target, "Open File");
                        try {
                            mContext.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            // Instruct the user to install a PDF reader here, or something
                        }
                    } else if (exfile.equals("xls") || exfile.equals("xlsx")) {
                        Uri uri = Uri.fromFile(imageFolder);
                        Intent target = new Intent(Intent.ACTION_VIEW);
                        target.setDataAndType(uri, "application/vnd.ms-excel");
                        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                        Intent intent = Intent.createChooser(target, "Open File");
                        try {
                            mContext.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            // Instruct the user to install a PDF reader here, or something
                        }
                    }


                    // listener.onImageClick(holder.image, uri.toString());
                } else {
                    // LoadAlertDialog("Error", "There is no Image in your Memory");
                }
            });

        } else if (-VIEW_TYPE_IMAGE_MESSAGE == viewType) {
            ChatMessage chatMessage = (ChatMessage) items.get(position);
            OutcomingImageMessageViewHolder holder = (OutcomingImageMessageViewHolder) viewHolder;

            holder.circularProgressBar.getIndeterminateDrawable().setColorFilter(getThemeAccentColor(mContext), PorterDuff.Mode.MULTIPLY);

            if (chatMessage.getChatImage().getStorageSize() == 0) {
                File file = new File(URI.create(chatMessage.getChatImage().getLocalPath()).getPath());
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    holder.image.setImageURI(uri);

                    uploadImage(uri, chatMessage.getId(), holder, position);
                }
            } else {
                File file = new File(URI.create(chatMessage.getChatImage().getLocalPath()).getPath());
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    holder.image.setImageURI(uri);
                    holder.progressFloatingActionButton.setVisibility(View.INVISIBLE);
                    holder.circularProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    holder.image.setImageResource(R.drawable.placeholder);
                    holder.progressFloatingActionButton.setVisibility(View.INVISIBLE);
                    holder.circularProgressBar.setVisibility(View.INVISIBLE);
                }
            }

            if (holder.text != null) {
                holder.text.setText(chatMessage.getChatImage().getText());
                holder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getOutcomingTextSize());
                holder.text.setTypeface(holder.text.getTypeface(), style.getOutcomingTextStyle());
                holder.text.setAutoLinkMask(style.getTextAutoLinkMask());
                if (chatMessage.getChatImage().getText().isEmpty()) {
                    holder.text.setVisibility(View.GONE);
                } else {
                    holder.text.setVisibility(View.VISIBLE);
                }
            }

            if (holder.textTime != null) {
                holder.textTime.setText(GetTime(chatMessage.getCreatedAt().toString()));
            }

            DocumentReference documentReference = new FireStoreHelper().GetMessageReadDocument(chatMessage.getId(), channelId);
            documentReference.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    return;
                }
                if (snapshot.exists()) {
                    MessageReadData user = snapshot.toObject(MessageReadData.class);
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

                        if (readbylist.size() > 0 && sentbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_unread_message, 0);
                        }

                        if (readbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_read_message, 0);
                        } else if (unreadbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_unread_message, 0);
                        } else if (unreadbylist.size() + readbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_unread_message, 0);
                        } else {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sent_message, 0);
                        }
                    }

                }
            });

            holder.itemView.setActivated(selectedItems.get(position, false));

            holder.relativeLayout.setOnLongClickListener(view -> {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            });
            holder.relativeLayout.setOnClickListener(view -> {
                listener.onMessageRowClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            });

        } else if (VIEW_TYPE_IMAGE_MESSAGE == viewType) {
            ChatMessage chatMessage = (ChatMessage) items.get(position);
            IncomingImageMessageViewHolder holder = (IncomingImageMessageViewHolder) viewHolder;

            File myDir = Environment.getExternalStorageDirectory();
            String file_dir = "RadioBroadcast/RadioBroadcast Images";
            File fileFolder = new File(myDir, file_dir);
            fileFolder.mkdirs();
            String publicC = chatMessage.getId() + ".jpg";
            File imageFolder = new File(fileFolder, publicC);

            holder.circularProgressBar.getIndeterminateDrawable().setColorFilter(getThemeAccentColor(mContext), PorterDuff.Mode.MULTIPLY);

            if (imageFolder.exists()) {
                holder.progressFloatingActionButton.setVisibility(View.INVISIBLE);
                holder.circularProgressBar.setVisibility(View.INVISIBLE);
                Uri uri = Uri.fromFile(imageFolder);
                holder.image.setImageURI(uri);
            } else {
                holder.image.setImageResource(R.drawable.placeholder);
                holder.progressFloatingActionButton.setVisibility(View.VISIBLE);
                holder.circularProgressBar.setVisibility(View.INVISIBLE);
                holder.progressFloatingActionButton.setOnClickListener(view -> {
                    holder.circularProgressBar.setVisibility(View.VISIBLE);
                    DocumentReference documentReference = new FireStoreHelper()
                            .GetMessageDocument(channelId, chatMessage.getId());
                    documentReference.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                final Message message = document.toObject(Message.class);
                                final Map<String, Object> data = message.getData();
                                String url = data.get(Message.FIELD_MESSAGE_DATA_MESSAGE_IMAGE_URL).toString();

                                if (url.isEmpty()) {
                                    LoadAlertDialog("Error", "Image is not uploaded yet,Please Try again");
                                } else {
                                    StorageReference storageRef = storage.getReferenceFromUrl(url);
                                    if (!fileFolder.exists()) {
                                        fileFolder.mkdirs();
                                    }
                                    final File localFile = new File(fileFolder, chatMessage.getId() + ".jpg");
                                    storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            notifyDataSetChanged();
                                            holder.progressFloatingActionButton.setVisibility(View.INVISIBLE);
                                            holder.circularProgressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }).addOnProgressListener(taskSnapshot -> {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        holder.circularProgressBar.setMax((int) taskSnapshot.getTotalByteCount());
                                        holder.circularProgressBar.setProgress((int) progress);
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            LoadAlertDialog("Error", "Something went wrong...Try again");
                                            holder.progressFloatingActionButton.setVisibility(View.VISIBLE);
                                            holder.circularProgressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            } else {
                                LoadAlertDialog("Error", "No Data Available");
                                holder.progressFloatingActionButton.setVisibility(View.VISIBLE);
                                holder.circularProgressBar.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            LoadAlertDialog("Error", task.getException().toString());
                            holder.progressFloatingActionButton.setVisibility(View.VISIBLE);
                            holder.circularProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                });
            }

            holder.itemView.setActivated(selectedItems.get(position, false));

            if (holder.text != null) {
                holder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getIncomingTextSize());
                holder.text.setTypeface(holder.text.getTypeface(), style.getIncomingTextStyle());
                holder.text.setAutoLinkMask(style.getTextAutoLinkMask());
                holder.text.setText(chatMessage.getChatImage().getText());
                if (chatMessage.getChatImage().getText().isEmpty()) {
                    holder.text.setVisibility(View.GONE);
                } else {
                    holder.text.setVisibility(View.VISIBLE);
                }
            }

            if (holder.senderName != null) {
                holder.senderName.setText(chatMessage.getUser().getName());
            }

            if (holder.textTime != null) {
                holder.textTime.setText(GetTime(chatMessage.getCreatedAt().toString()));
            }

            holder.relativeLayout.setOnLongClickListener(view -> {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            });
            holder.relativeLayout.setOnClickListener(view -> {
                listener.onMessageRowClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            });

            holder.bubble.setOnClickListener(view -> {
                if (imageFolder.exists()) {
                    Uri uri = Uri.fromFile(imageFolder);
                    listener.onImageClick(holder.image, uri.toString());
                } else {
                    LoadAlertDialog("Error", "There is no Image in your Memory");
                }
            });

        } else if (-VIEW_TYPE_LOCATION_MESSAGE == viewType) {
            ChatMessage chatMessage = (ChatMessage) items.get(position);
            OutcomingLocationMessageViewHolder holder = (OutcomingLocationMessageViewHolder) viewHolder;

            ChatLocation item = getItem(position).getChatLocation();

            holder.initializeMapView();

            // Keep track of MapView
            mMaps.add(holder.image);
            holder.image.setTag(item);

            if (holder.map != null) {
                setMapLocation(holder.map, item);
            }

            if (holder.map != null) {
                holder.map.setOnMapClickListener(latLng -> {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", chatMessage.getChatLocation().getLatitude()
                            , chatMessage.getChatLocation().getLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    mContext.startActivity(intent);
                });
            }

            holder.bubble.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=loc:" + chatMessage.getChatLocation().getLatitude() + "," + chatMessage.getChatLocation().getLongitude()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Only if initiating from a Broadcast Receiver
                    String mapsPackageName = "com.google.android.apps.maps";
                    PackageManager pm = mContext.getPackageManager();
                    boolean isInstalled = isPackageInstalled(mapsPackageName, pm);
                    if (isInstalled) {
                        mContext.startActivity(i);
                    }
                }
            });

            if (holder.text != null) {
                holder.text.setText(chatMessage.getChatLocation().getPlace());
                holder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getOutcomingTextSize());
                holder.text.setTypeface(holder.text.getTypeface(), style.getOutcomingTextStyle());
                holder.text.setAutoLinkMask(style.getTextAutoLinkMask());
                if (chatMessage.getChatLocation().getPlace().isEmpty()) {
                    holder.text.setVisibility(View.GONE);
                } else {
                    holder.text.setVisibility(View.VISIBLE);
                }
            }

            if (holder.textTime != null) {
                holder.textTime.setText(GetTime(chatMessage.getCreatedAt().toString()));
            }

            DocumentReference documentReference = new FireStoreHelper().GetMessageReadDocument(chatMessage.getId(), channelId);
            documentReference.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    return;
                }
                if (snapshot.exists()) {
                    MessageReadData user = snapshot.toObject(MessageReadData.class);
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

                        if (readbylist.size() > 0 && sentbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_unread_message, 0);
                        }

                        if (readbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_read_message, 0);
                        } else if (unreadbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_unread_message, 0);
                        } else if (unreadbylist.size() + readbylist.size() == user.getMembers().entrySet().size()) {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_unread_message, 0);
                        } else {
                            holder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sent_message, 0);
                        }
                    }

                }
            });

            holder.itemView.setActivated(selectedItems.get(position, false));

            holder.relativeLayout.setOnLongClickListener(view -> {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            });
            holder.relativeLayout.setOnClickListener(view -> {
                listener.onMessageRowClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            });

        } else if (VIEW_TYPE_LOCATION_MESSAGE == viewType) {
            ChatMessage chatMessage = (ChatMessage) items.get(position);
            IncomingLocationMessageViewHolder holder = (IncomingLocationMessageViewHolder) viewHolder;

            ChatLocation item = chatMessage.getChatLocation();

            holder.initializeMapView();

            // Keep track of MapView
            mMaps.add(holder.image);
            holder.image.setTag(item);

            if (holder.map != null) {
                setMapLocation(holder.map, item);
            }

            if (holder.map != null) {
                holder.map.setOnMapClickListener(latLng -> {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", chatMessage.getChatLocation().getLatitude()
                            , chatMessage.getChatLocation().getLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    mContext.startActivity(intent);
                });
            }

            holder.bubble.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=loc:" + chatMessage.getChatLocation().getLatitude() + "," + chatMessage.getChatLocation().getLongitude()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Only if initiating from a Broadcast Receiver
                    String mapsPackageName = "com.google.android.apps.maps";
                    PackageManager pm = mContext.getPackageManager();
                    boolean isInstalled = isPackageInstalled(mapsPackageName, pm);
                    if (isInstalled) {
                        mContext.startActivity(i);
                    }
                }
            });

            holder.itemView.setActivated(selectedItems.get(position, false));

            if (holder.text != null) {
                holder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getIncomingTextSize());
                holder.text.setTypeface(holder.text.getTypeface(), style.getIncomingTextStyle());
                holder.text.setAutoLinkMask(style.getTextAutoLinkMask());
                holder.text.setText(chatMessage.getChatLocation().getPlace());
                if (chatMessage.getChatLocation().getPlace().isEmpty()) {
                    holder.text.setVisibility(View.GONE);
                } else {
                    holder.text.setVisibility(View.VISIBLE);
                }
            }

            if (holder.senderName != null) {
                holder.senderName.setText(chatMessage.getUser().getName());

            }

            if (holder.textTime != null) {
                holder.textTime.setText(GetTime(chatMessage.getCreatedAt().toString()));
            }

            holder.relativeLayout.setOnLongClickListener(view -> {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            });
            holder.relativeLayout.setOnClickListener(view -> {
                listener.onMessageRowClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            });

            /*holder.image.setOnClickListener(view -> {

            });*/
        }
    }

    @Override
    public void onLoadMore(int page, int total) {
        if (loadMoreListener != null) {
            loadMoreListener.onLoadMore(page, total);
        }
    }

    public void addToStart(ChatMessage message, boolean scroll) {
        int oldsize = items.size() - 1;
        items.add(0, message);
        notifyItemInserted(0);
        if (message.getUser().getId().equals(mSenderId)) {
            if (layoutManager != null && scroll) {
                layoutManager.scrollToPosition(0);
            }
        }
    }

    public void addMessage(ChatMessage message, int position) {
        int oldsize = items.size() - 1;
        items.add(position, message);
        notifyItemInserted(position);
        /*if (layoutManager != null && scroll) {
            layoutManager.scrollToPosition(0);
        }*/
    }

    public void addNewMessage(List<ChatMessage> message) {
        //Collections.reverse(message);
        this.items.addAll(message);
        // removeDuplicates(this.items);

        //   clear();
      /*  if (message.size() > 0){
            Map<String, ChatMessage> map = new LinkedHashMap<>();
            for (ChatMessage ays : items) {
                map.put(ays.getId(), ays);
            }
            items.clear();
            items.addAll(map.values());
        }*/

        notifyDataSetChanged();
        //  layoutManager.scrollToPosition(items.size()-5);
       /* if (layoutManager != null && scroll) {
            layoutManager.scrollToPosition(items.size() - 2);
        }*/
    }

    public void addToEnd(ChatMessage messages, boolean reverse) {
        // if (reverse) Collections.reverse(messages);

        /*if (!items.isEmpty()) {
            int lastItemPosition = items.size() - 1;
            Date lastItem = (Date) items.get(lastItemPosition).item;
            if (DateFormatter.isSameDay(messages.get(0).getCreatedAt(), lastItem)) {
                items.remove(lastItemPosition);
                notifyItemRemoved(lastItemPosition);
            }
        }

        int oldSize = items.size();
        generateDateHeaders(messages);
        notifyItemRangeInserted(oldSize, items.size() - oldSize);*/
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        int size = this.items.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.items.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    void setStyle(MessagesListStyle style) {
        this.style = style;
    }

    public interface OnLoadMoreListener {

        /**
         * Fires when user scrolled to the end of list.
         *
         * @param page            next page to download.
         * @param totalItemsCount current items count.
         */
        void onLoadMore(int page, int totalItemsCount);
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

    private static String GetDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d hh:mm:ss z yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM yyyy");
        try {
            Date dat1e = dateFormat.parse(date);
            return dateFormat2.format(dat1e);
        } catch (ParseException e) {
            return e.toString();
        }
    }

    @Override
    public int getItemViewType(int position) {
        boolean isOutcoming = false;
        int viewType;
        ChatMessage message = items.get(position);
        isOutcoming = message.getUser().getId().contentEquals(mSenderId);
        viewType = message.getType();


        return isOutcoming ? viewType * -1 : viewType;
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    private String formatToYesterdayOrToday(String date) throws ParseException {
        Date dateTime = new SimpleDateFormat("dd MMM yyyy").parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return "Today";
        } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday";
        } else {
            return date;
        }
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface MessageAdapterListener {
        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);

        void onImageClick(RoundRectCornerImageView imageView, String uri);
    }

    public ChatMessage getMessageData(int position) {
        return items.get(position);
    }

    public List<ChatMessage> GetMessageList() {
        return items;
    }

    private void uploadImage(Uri uri, String id,
                             OutcomingImageMessageViewHolder holder,
                             int position) {
        holder.progressFloatingActionButton.setVisibility(View.VISIBLE);
        holder.circularProgressBar.setVisibility(View.VISIBLE);
        try {
            Bitmap mBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            TimeZone tz = TimeZone.getDefault();
            String timezone = tz.getDisplayName().trim();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            imageRef = storageRef.child("messages/" + "/" + date + "" + timezone + "/" + channelId + "/images/ " + id + ".jpg");
            uploadTask = imageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    LoadAlertDialog("Error", "Something went wrong...Try again");
                    holder.progressFloatingActionButton.setVisibility(View.VISIBLE);
                    holder.circularProgressBar.setVisibility(View.INVISIBLE);
                }
            }).addOnSuccessListener(taskSnapshot -> {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                long downloadsize = taskSnapshot.getTotalByteCount();

                Map<String, Object> data1 = new HashMap<>();
                data1.put(Message.FIELD_MESSAGE_DATA + "." + Message.FIELD_MESSAGE_DATA_MESSAGE_IMAGE_URL, downloadUrl.toString());
                data1.put(Message.FIELD_MESSAGE_DATA + "." + Message.FIELD_MESSAGE_DATA_MESSAGE_SIZE, downloadsize);

                DocumentReference washingtonRef = new FireStoreHelper(mContext).GetMessageDocument(channelId, id);
                washingtonRef.update(data1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                items.get(position).getChatImage().setStorageSize(downloadsize);
                                items.get(position).getChatImage().setStoragePath(downloadUrl.toString());
                                notifyDataSetChanged();
                                holder.progressFloatingActionButton.setVisibility(View.INVISIBLE);
                                holder.circularProgressBar.setVisibility(View.INVISIBLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                LoadAlertDialog("Error", "Something went wrong...Try again");
                                holder.progressFloatingActionButton.setVisibility(View.VISIBLE);
                                holder.circularProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });


            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                holder.circularProgressBar.setMax((int) taskSnapshot.getTotalByteCount());
                holder.circularProgressBar.setProgress((int) progress);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadPdf(Uri uri, String id, String filename,
                           OutcomingPdfMessageViewHolder holder,
                           int position) {
        holder.progressFloatingActionButton.setVisibility(View.VISIBLE);
        holder.circularProgressBar.setVisibility(View.VISIBLE);


        if (uri.toString().contains(".")) {
            String extension = uri.toString().substring(uri.toString().lastIndexOf("."));

            TimeZone tz = TimeZone.getDefault();
            String timezone = tz.getDisplayName().trim();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            imageRef = storageRef.child("messages/" + "/" + date + "" + timezone + "/" + channelId + "/document/ " + filename);
            uploadTask = imageRef.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    LoadAlertDialog("Error", "Something went wrong...Try again");
                    holder.progressFloatingActionButton.setVisibility(View.VISIBLE);
                    holder.circularProgressBar.setVisibility(View.INVISIBLE);
                }
            }).addOnSuccessListener(taskSnapshot -> {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                long downloadsize = taskSnapshot.getTotalByteCount();

                Map<String, Object> data1 = new HashMap<>();
                data1.put(Message.FIELD_MESSAGE_DATA + "." + Message.FIELD_MESSAGE_DATA_MESSAGE_IMAGE_URL, downloadUrl.toString());
                data1.put(Message.FIELD_MESSAGE_DATA + "." + Message.FIELD_MESSAGE_DATA_MESSAGE_SIZE, downloadsize);

                DocumentReference washingtonRef = new FireStoreHelper(mContext).GetMessageDocument(channelId, id);
                washingtonRef.update(data1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                items.get(position).getChatPdf().setStorageSize(downloadsize);
                                items.get(position).getChatPdf().setStoragePath(downloadUrl.toString());
                                notifyDataSetChanged();
                                holder.progressFloatingActionButton.setVisibility(View.INVISIBLE);
                                holder.circularProgressBar.setVisibility(View.INVISIBLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                LoadAlertDialog("Error", "Something went wrong...Try again");
                                holder.progressFloatingActionButton.setVisibility(View.VISIBLE);
                                holder.circularProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });


            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                holder.circularProgressBar.setMax((int) taskSnapshot.getTotalByteCount());
                holder.circularProgressBar.setProgress((int) progress);
            });

        }
    }

    private void LoadAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK",
                (dialog, which) -> {

                });
        builder.show();
    }

    private static int getThemeAccentColor(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorPrimaryDark;
        } else {
            colorAttr = context.getResources().getIdentifier("colorPrimaryDark", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    public HashSet<MapView> getMaps() {
        return mMaps;
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
