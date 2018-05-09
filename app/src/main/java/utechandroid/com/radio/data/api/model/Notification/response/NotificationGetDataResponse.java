package utechandroid.com.radio.data.api.model.Notification.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by AFF41 on 12-Mar-18.
 */

public class NotificationGetDataResponse {

    private List<Table> table;
    private List<Table1> table1;

    public List<Table> getTable() {
        return table;
    }

    public void setTable(List<Table> table) {
        this.table = table;
    }

    public List<Table1> getTable1() {
        return table1;
    }

    public void setTable1(List<Table1> table1) {
        this.table1 = table1;
    }

    public static class Table {
        /**
         * success : 1
         * message : Data get successfully
         */

        private int success;
        private String message;

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Table1 implements Parcelable {
        /**
         * id : 5
         * title : 123
         * body : hello
         * imageUrl : string
         * address : string
         * priority : 0
         * expiryDate : 2018-01-01T00:00:00
         * createdDate : 2018-03-10T16:59:43.233
         * corpCode : string
         */

        private int id;
        private String title;
        private String body;
        private String imageUrl;
        private String address;
        private int priority;
        private String expiryDate;
        private String createdDate;
        private String corpCode;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getCorpCode() {
            return corpCode;
        }

        public void setCorpCode(String corpCode) {
            this.corpCode = corpCode;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.title);
            dest.writeString(this.body);
            dest.writeString(this.imageUrl);
            dest.writeString(this.address);
            dest.writeInt(this.priority);
            dest.writeString(this.expiryDate);
            dest.writeString(this.createdDate);
            dest.writeString(this.corpCode);
        }

        public Table1() {
        }

        protected Table1(Parcel in) {
            this.id = in.readInt();
            this.title = in.readString();
            this.body = in.readString();
            this.imageUrl = in.readString();
            this.address = in.readString();
            this.priority = in.readInt();
            this.expiryDate = in.readString();
            this.createdDate = in.readString();
            this.corpCode = in.readString();
        }

        public static final Parcelable.Creator<Table1> CREATOR = new Parcelable.Creator<Table1>() {
            @Override
            public Table1 createFromParcel(Parcel source) {
                return new Table1(source);
            }

            @Override
            public Table1[] newArray(int size) {
                return new Table1[size];
            }
        };
    }
}
