package utechandroid.com.radio.data.api.model;

/**
 * Created by AFF41 on 1/20/2018.
 */

public class EditPhoneContactRequest {
    private String searchText;
    private String corpcode;

    public EditPhoneContactRequest(String searchText, String corpcode) {
        this.searchText = searchText;
        this.corpcode = corpcode;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getCorpcode() {
        return corpcode;
    }

    public void setCorpcode(String corpcode) {
        this.corpcode = corpcode;
    }
}
