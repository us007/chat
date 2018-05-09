package utechandroid.com.radio.data.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by AFF41 on 1/20/2018.
 */

public class EditPhoneContactResponse {
    private List<Table> table;

    public List<Table> getTable() {
        return table;
    }

    public void setTable(List<Table> table) {
        this.table = table;
    }

    public static class Table {
        /**
         * id : 1
         * partyName : Vipul Pravinchandra Shah
         */
        @SerializedName("id")
        private int id;
        @SerializedName("partyName")
        private String partyName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPartyName() {
            return partyName;
        }

        public void setPartyName(String partyName) {
            this.partyName = partyName;
        }
    }

//    private List<Table1> table;
//    private List<Table1> table1;
//
//    public List<?> getTable() {
//        return table;
//    }
//
//    public void setTable(List<Table1> table) {
//        this.table = table;
//    }
//
//    public List<Table1> getTable1() {
//        return table1;
//    }
//
//    public void setTable1(List<Table1> table1) {
//        this.table1 = table1;
//    }
//
//    public static class Table1 {
//        /**
//         * id : 1
//         * partyName : Vipul Pravinchandra Shah
//         */
//        @SerializedName("id")
//        private int id;
//        @SerializedName("partyName")
//        private String partyName;
//
//        public int getId() {
//            return id;
//        }
//
//        public void setId(int id) {
//            this.id = id;
//        }
//
//        public String getPartyName() {
//            return partyName;
//        }
//
//        public void setPartyName(String partyName) {
//            this.partyName = partyName;
//        }
//    }
}
