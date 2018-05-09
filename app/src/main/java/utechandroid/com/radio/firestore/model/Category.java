package utechandroid.com.radio.firestore.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Created by Utsav Shah on 12-Oct-17.
 */

@IgnoreExtraProperties
public class Category {

    public static final String FIELD_COLLECTION = "categories";

    public static final String FIELD_NAME = "name";
    public static final String FIELD_CODE = "code";

    public Category(){

    }

    public Category(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Category setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Category setCode(String code) {
        this.code = code;
        return this;
    }

    private String name,code;
}
