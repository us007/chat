package utechandroid.com.radio.firestore.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Utsav Shah on 24-Nov-17.
 */

@IgnoreExtraProperties
public class MembersData {

    public MembersData(){}

    /* Status
    *  0 = remove
    *  1 = pending
    *  2 = accept
    *  3 = reject
    * */

    public static final String FIELD_COLLECTION = "members";
    public static final String FIELD_COLLECTION_MEMBERS = "members";
    public static final String FIELD_MEMBER_ID = "memberId";
    public static final String FIELD_MEMBER_USER_ID = "memberUserId";
    public static final String FIELD_MEMBER_CHANNEL_ID = "memberChannelId";
    public static final String FIELD_MEMBER_STATUS = "memberStatus";
    public static final String FIELD_MEMBER_REJECTED_DATE = "memberRejectedDate";
    public static final String FIELD_MEMBER_REMOVE_DATE = "memberRemovedDate";
    public static final String FIELD_MEMBER_JOINED_DATE = "memberJoinedDate";
    public static final String FIELD_MEMBER_REQUEST_DATE = "memberRequestDate";

    private String memberId;

    public int getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(int memberStatus) {
        this.memberStatus = memberStatus;
    }

    private int memberStatus;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberUserId() {
        return memberUserId;
    }

    public void setMemberUserId(String memberUserId) {
        this.memberUserId = memberUserId;
    }

    public String getMemberChannelId() {
        return memberChannelId;
    }

    public void setMemberChannelId(String memberChannelId) {
        this.memberChannelId = memberChannelId;
    }

    public Date getMemberRejectedDate() {
        return memberRejectedDate;
    }

    public void setMemberRejectedDate(Date memberRejectedDate) {
        this.memberRejectedDate = memberRejectedDate;
    }

    public Date getMemberJoinedDate() {
        return memberJoinedDate;
    }

    public void setMemberJoinedDate(Date memberJoinedDate) {
        this.memberJoinedDate = memberJoinedDate;
    }

    public Date getMemberRequestDate() {
        return memberRequestDate;
    }

    public void setMemberRequestDate(Date memberRequestDate) {
        this.memberRequestDate = memberRequestDate;
    }

    private String memberUserId;
    private String memberChannelId;
    private @ServerTimestamp
    Date memberRejectedDate;
    private @ServerTimestamp
    Date memberJoinedDate;
    private @ServerTimestamp
    Date memberRequestDate;

    public Date getMemberRemovedDate() {
        return memberRemovedDate;
    }

    public MembersData setMemberRemovedDate(Date memberRemovedDate) {
        this.memberRemovedDate = memberRemovedDate;
        return this;
    }

    private @ServerTimestamp
    Date memberRemovedDate;

}
