package utechandroid.com.radio.firestore.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by Utsav Shah on 18-Oct-17.
 */
@IgnoreExtraProperties
public class Members {

    public Members() {
    }

    public static final String FIELD_COLLECTION = "members";

    /*  Status
    *  0 = reject
    *  1 = pending
    *  2 = accept
    * */

    public static final String FIELD_MEMBER_ID = "memberId";
    public static final String FIELD_MEMBER_USER_ID = "memberUserId";
    public static final String FIELD_MEMBER_CHANNEL_ID = "memberChannelId";
    public static final String FIELD_MEMBER_STATUS = "memberStatus";
    public static final String FIELD_MEMBER_JOINED_DATE = "memberJoinedDate";
    public static final String FIELD_MEMBER_REQUEST_DATE = "memberRequestDate";

    public String getMemberId() {
        return memberId;
    }

    public Members setMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    public String getMemberUserId() {
        return memberUserId;
    }

    public Members setMemberUserId(String memberUserId) {
        this.memberUserId = memberUserId;
        return this;
    }

    public String getMemberChannelId() {
        return memberChannelId;
    }

    public Members setMemberChannelId(String memberChannelId) {
        this.memberChannelId = memberChannelId;
        return this;
    }

    public int getMemberStatus() {
        return memberStatus;
    }

    public Members setMemberStatus(int memberStatus) {
        this.memberStatus = memberStatus;
        return this;
    }

    public Date getMemberJoinedDate() {
        return memberJoinedDate;
    }

    public Members setMemberJoinedDate(Date memberJoinedDate) {
        this.memberJoinedDate = memberJoinedDate;
        return this;
    }

    public Date getMemberRequestDate() {
        return memberRequestDate;
    }

    public Members setMemberRequestDate(Date memberRequestDate) {
        this.memberRequestDate = memberRequestDate;
        return this;
    }

    private String memberId;
    private String memberUserId;
    private String memberChannelId;
    private int memberStatus;
    private Date memberJoinedDate;
    private Date memberRequestDate;
}
