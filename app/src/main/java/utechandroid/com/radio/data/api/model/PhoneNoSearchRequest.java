package utechandroid.com.radio.data.api.model;

public class PhoneNoSearchRequest{
	public PhoneNoSearchRequest(String searchText, String corpcode) {
		this.searchText = searchText;
		this.corpcode = corpcode;
	}

	private String searchText;
	private String corpcode;

	public void setSearchText(String searchText){
		this.searchText = searchText;
	}

	public String getSearchText(){
		return searchText;
	}

	public void setCorpcode(String corpcode){
		this.corpcode = corpcode;
	}

	public String getCorpcode(){
		return corpcode;
	}
}
