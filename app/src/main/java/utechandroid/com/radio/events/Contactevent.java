package utechandroid.com.radio.events;

import utechandroid.com.radio.data.api.model.TableItem;

/**
 * Created by Utsav Shah on 20-Nov-17.
 */

public class Contactevent {

    public TableItem getTableItem() {
        return tableItem;
    }

    public Contactevent setTableItem(TableItem tableItem) {
        this.tableItem = tableItem;
        return this;
    }

    public Contactevent(TableItem tableItem) {
        this.tableItem = tableItem;
    }

    private TableItem tableItem;

}
