package utechandroid.com.radio.data.api.model;

import java.util.List;

/**
 * Created by AFF41 on 1/23/2018.
 */

public class SaveUpdateContactResponse {

    private List<Table> table;

    public List<Table> getTable() {
        return table;
    }

    public void setTable(List<Table> table) {
        this.table = table;
    }

    public static class Table {
        /**
         * msg : Success
         */

        private String msg;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
