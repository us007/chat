package utechandroid.com.radio.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prudenttechno.radioNotification.R;


import java.util.ArrayList;
import java.util.List;

import utechandroid.com.radio.adapter.categoryAdapter.CategoryAdapter;
import utechandroid.com.radio.firestore.model.SubCategory;
import utechandroid.com.radio.ui.categories.SectionCategories;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class SubCategoryActivity extends ColorsAppCompatActivity implements CategoryAdapter.ContactsAdapterListenerNew {
    private FirebaseFirestore db;
    List<SectionCategories> stringList = new ArrayList<>();
    String subcat;
    private RecyclerView rvSubCategory;
    Toolbar toolbar;
    private SearchView searchView;
    CategoryAdapter categoryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        rvSubCategory = findViewById(R.id.recyclerView_sub_categories);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvSubCategory.setLayoutManager(layoutManager);
        db = FirebaseFirestore.getInstance();
        if (getIntent() == null) {
            Toast.makeText(this, "no data", Toast.LENGTH_SHORT).show();
        } else {
            subcat = getIntent().getStringExtra("subcategory");

        }
        subCategory(subcat);
    }

    public void subCategory(String subCategory) {
        db.collection(SubCategory.FIELD_COLLECTION)
                .document(subCategory)
                .collection("SubCategories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<SubCategory> subCategoryList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            SubCategory SubCategory = document.toObject(SubCategory.class);
                            Log.e(document.getId(), " => " + SubCategory.getCategories());
                            subCategoryList.add(SubCategory);
                        }
                        categoryAdapter = new CategoryAdapter(this, subCategoryList, this);
                        rvSubCategory.setAdapter(categoryAdapter);
                        //  stringList.add(new SectionCategories())
                        // stringList.add(new SectionCategories(category.get(finalI)));
                        // categoryAdapter.notifyDataSetChanged();
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                categoryAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                categoryAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                /*if (mDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }*/
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContactSelected(SubCategory contact) {

    }
}
