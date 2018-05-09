package utechandroid.com.radio.firestore.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Utsav Shah on 16-Oct-17.
 */

@IgnoreExtraProperties
public class SubCategory {

    public static final String FIELD_COLLECTION = "subCategories";

    public static final String FIELD_NAME = "name";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_CATEGORY_ID = "categories";

    private String name="";

    public String getName() {
        return name;
    }

    public SubCategory setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public SubCategory setCode(String code) {
        this.code = code;
        return this;
    }

    public String getCategories() {
        return categories;
    }

    public SubCategory setCategories(String categories) {
        this.categories = categories;
        return this;
    }

    private String code="";
    private String categories="";

    public SubCategory(){

    }

    public Map<String, Object> getChannels() {
        return channels;
    }

    public SubCategory setChannels(Map<String, Object> channels) {
        this.channels = channels;
        return this;
    }

    private Map<String, Object> channels = new HashMap<>();
}
