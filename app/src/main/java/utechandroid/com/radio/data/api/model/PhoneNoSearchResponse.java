package utechandroid.com.radio.data.api.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

//@Generated("com.robohorse.robopojogenerator")
public class PhoneNoSearchResponse{

	@SerializedName("table")
	private List<TableItem> table;

	public void setTable(List<TableItem> table){
		this.table = table;
	}

	public List<TableItem> getTable(){
		return table;
	}
}