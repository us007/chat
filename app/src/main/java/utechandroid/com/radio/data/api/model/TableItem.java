package utechandroid.com.radio.data.api.model;


import com.google.gson.annotations.SerializedName;

//@Generated("com.robohorse.robopojogenerator")
public class TableItem{

	@SerializedName("address")
	private String address;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@SerializedName("id")
	private String id;

	@SerializedName("occupation")
	private String occupation;

	@SerializedName("partyName")
	private String partyName;

	@SerializedName("mobile")
	private String mobile;

	@SerializedName("office")
	private String office;

	@SerializedName("residence")
	private String residence;

	@SerializedName("email")
	private String email;

	public void setAddress(String address){
		this.address = address;
	}

	public String getAddress(){
		if (address==null){
			return " - ";
		}else {
			return address;
		}
	}

	public void setOccupation(String occupation){
		this.occupation = occupation;
	}

	public String getOccupation(){
		if (occupation==null){
			return " - ";
		}else {
			return occupation;
		}
	}

	public void setPartyName(String partyName){
		this.partyName = partyName;
	}

	public String getPartyName(){
		if (partyName==null){
			return " - ";
		}else {
			return partyName;
		}
	}

	public void setMobile(String mobile){
		this.mobile = mobile;
	}

	public String getMobile(){
		if (mobile==null){
			return " - ";
		}else {
			return mobile;
		}
	}

	public void setOffice(String office){
		this.office = office;
	}

	public String getOffice(){
		if (office==null){
			return " - ";
		}else {
			return office;
		}
	}

	public void setResidence(String residence){
		this.residence = residence;
	}

	public String getResidence(){
		if (residence==null){
			return " - ";
		}else {
			return residence;
		}
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		if (email==null){
			return " - ";
		}else {
			return email;
		}
	}
}