package utechandroid.com.radio.firestore.helper.dbRefrence;

import android.content.Context;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import utechandroid.com.radio.firestore.helper.auth.FireStoreAuthHelper;
import utechandroid.com.radio.firestore.model.Category;
import utechandroid.com.radio.firestore.model.ChannelData;
import utechandroid.com.radio.firestore.model.MembersData;
import utechandroid.com.radio.firestore.model.Message;
import utechandroid.com.radio.firestore.model.MessageReadData;
import utechandroid.com.radio.firestore.model.RadioData;
import utechandroid.com.radio.firestore.model.SubCategory;
import utechandroid.com.radio.firestore.model.User;

/**
 * Created by Utsav Shah on 23-Nov-17.
 */

public class FireStoreHelper {

    private Context mContext;
    private FirebaseFirestore db;

    public FireStoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public FireStoreHelper(Context context) {
        this.mContext = context;
        db = FirebaseFirestore.getInstance();
    }

    /* Radio */

    public DocumentReference GetRadioDocument() {
        return db.collection(RadioData.FIELD_COLLECTION).document();
    }

    public DocumentReference GetRadioByIdDocument(String radioId) {
        return db.collection(RadioData.FIELD_COLLECTION).document(radioId);
    }

    /* User */

    public DocumentReference GetCurrentUserDocument() {
        return db.collection(User.FIELD_COLLECTION).document(new FireStoreAuthHelper().GetUserId());
    }

    public DocumentReference GetUserByDocument(String id) {
        return db.collection(User.FIELD_COLLECTION).document(id);
    }

    /* Channel */

    public DocumentReference GetChannelDocument() {
        return db.collection(ChannelData.FIELD_COLLECTION).document();
    }

    public DocumentReference GetChannelByIdDocument(String channelId) {
        return db.collection(ChannelData.FIELD_COLLECTION).document(channelId);
    }

    /* Category */

    public DocumentReference GetSubCategoriesDocument(String category, String subcategory) {
        return db.collection(SubCategory.FIELD_COLLECTION).document(category).collection("SubCategories").document(subcategory);
    }

    public DocumentReference GetCategoriesDocument(String category) {
        return db.collection(Category.FIELD_COLLECTION).document(category);
    }

    /* Members */

    public DocumentReference GetMemberDocument(String channelId) {
        return db.collection(MembersData.FIELD_COLLECTION).document(channelId).collection(MembersData.FIELD_COLLECTION).document();
    }

    public CollectionReference GetMemberCollection(String channelId) {
        return db.collection(MembersData.FIELD_COLLECTION).document(channelId).collection(MembersData.FIELD_COLLECTION);
    }

    public DocumentReference GetMemberDocumentbyId(String channelId, String memberId) {
        return db.collection(MembersData.FIELD_COLLECTION).document(channelId).collection(MembersData.FIELD_COLLECTION).document(memberId);
    }

    public DocumentReference GetMemberDocument(String channelId, String memberId) {
        return db.collection(MembersData.FIELD_COLLECTION).document(channelId).collection(MembersData.FIELD_COLLECTION).document(memberId);
    }

    /* Messages */

    public DocumentReference GetMessageDocument(String channelId) {
        return db.collection(Message.FIELD_COLLECTION)
                .document(channelId)
                .collection(Message.FIELD_COLLECTION)
                .document();
    }

    public DocumentReference GetMessageDocument(String channelId, String messageId) {
        return db.collection(Message.FIELD_COLLECTION)
                .document(channelId)
                .collection(Message.FIELD_COLLECTION)
                .document(messageId);
    }

    public DocumentReference GetMessageReadDocument(String messageId, String channelId) {
        return db.collection(MessageReadData.FIELD_COLLECTION)
                .document(channelId)
                .collection(MessageReadData.FIELD_COLLECTION)
                .document(messageId);
    }

    public CollectionReference GetMessageReadCollection(String channelId) {
        return db.collection(MessageReadData.FIELD_COLLECTION)
                .document(channelId)
                .collection(MessageReadData.FIELD_COLLECTION);
    }

}
