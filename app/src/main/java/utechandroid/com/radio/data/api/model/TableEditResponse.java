package utechandroid.com.radio.data.api.model;

import java.util.List;

/**
 * Created by AFF41 on 1/22/2018.
 */

public class TableEditResponse {

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
         * relation : Self
         * houseNo : 5/7
         * address : Gurjar Flats
         * area : Nawa Wadaj
         * city : Ahmedabad
         * state : Gujarat
         * countryCode : India
         * pincode : 380013
         * rPhoneNo : 079-27640976
         * oPhoneNo : 079-26400976
         * mPhoneNo : 9974287967
         * email : vpravinchandra@gmail.com
         * occupation : Chartered Accountant
         */

        private int id;
        private String partyName;
        private String relation;
        private String houseNo;
        private String address;
        private String area;
        private String city;
        private String state;
        private String countryCode;
        private String pincode;
        private String rPhoneNo;
        private String oPhoneNo;
        private String mPhoneNo;
        private String email;
        private String occupation;

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

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }

        public String getHouseNo() {
            return houseNo;
        }

        public void setHouseNo(String houseNo) {
            this.houseNo = houseNo;
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

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public String getRPhoneNo() {
            return rPhoneNo;
        }

        public void setRPhoneNo(String rPhoneNo) {
            this.rPhoneNo = rPhoneNo;
        }

        public String getOPhoneNo() {
            return oPhoneNo;
        }

        public void setOPhoneNo(String oPhoneNo) {
            this.oPhoneNo = oPhoneNo;
        }

        public String getMPhoneNo() {
            return mPhoneNo;
        }

        public void setMPhoneNo(String mPhoneNo) {
            this.mPhoneNo = mPhoneNo;
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
    }
}
