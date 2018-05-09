package utechandroid.com.radio.data.api.model;

/**
 * Created by AFF41 on 1/23/2018.
 */

public class SaveUpdateContactRequest {

    /**
     * id : 0
     * partyname : vipul@shah
     * relation : abc
     * houseno : 2364
     * address : abc
     * area : ahm
     * city : ahm
     * state : gujarat
     * countrycode : 0091
     * pincode : 389151
     * rphoneno : 123
     * ophoneno : 456
     * mphoneno : 789
     * email : stuti@gmail.com
     * occupation : abcxd
     * userEmail : vpravinchandra@gmail.com
     */

    private String id;
    private String partyname;
    private String relation;
    private String houseno;
    private String address;
    private String area;
    private String city;
    private String state;
    private String countrycode;
    private String pincode;
    private String rphoneno;
    private String ophoneno;
    private String mphoneno;
    private String email;
    private String occupation;
    private String userEmail;

    public SaveUpdateContactRequest(String id, String partyname, String relation, String houseno, String address, String area, String city, String state, String countrycode, String pincode, String rphoneno, String ophoneno, String mphoneno, String email, String occupation, String userEmail) {
        this.id = id;
        this.partyname = partyname;
        this.relation = relation;
        this.houseno = houseno;
        this.address = address;
        this.area = area;
        this.city = city;
        this.state = state;
        this.countrycode = countrycode;
        this.pincode = pincode;
        this.rphoneno = rphoneno;
        this.ophoneno = ophoneno;
        this.mphoneno = mphoneno;
        this.email = email;
        this.occupation = occupation;
        this.userEmail = userEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartyname() {
        return partyname;
    }

    public void setPartyname(String partyname) {
        this.partyname = partyname;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getHouseno() {
        return houseno;
    }

    public void setHouseno(String houseno) {
        this.houseno = houseno;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getRphoneno() {
        return rphoneno;
    }

    public void setRphoneno(String rphoneno) {
        this.rphoneno = rphoneno;
    }

    public String getOphoneno() {
        return ophoneno;
    }

    public void setOphoneno(String ophoneno) {
        this.ophoneno = ophoneno;
    }

    public String getMphoneno() {
        return mphoneno;
    }

    public void setMphoneno(String mphoneno) {
        this.mphoneno = mphoneno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
