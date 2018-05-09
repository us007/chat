package utechandroid.com.radio.ui.categories;

import java.util.List;

import utechandroid.com.radio.firestore.model.SubCategory;

/**
 * Created by Dharmik Patel on 24-Nov-17.
 */

public class SectionCategories {
    private String sectionLabel;
    private List<SubCategory> itemArrayList;

    public SectionCategories(String sectionLabel) {
        this.sectionLabel = sectionLabel;
    }

    public SectionCategories(List<SubCategory> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }
//    public SectionCategories(String sectionLabel, List<SubCategory> itemArrayList) {
//        this.sectionLabel = sectionLabel;
//       // this.itemArrayList = itemArrayList;
//    }

    public String getSectionLabel() {
        return sectionLabel;
    }

    public List<SubCategory> getItemArrayList() {
        return itemArrayList;
    }
}
