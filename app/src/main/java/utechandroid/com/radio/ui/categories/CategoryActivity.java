package utechandroid.com.radio.ui.categories;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.prudenttechno.radioNotification.R;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utechandroid.com.radio.firestore.model.Category;
import utechandroid.com.radio.util.Colors.ColorsAppCompatActivity;

public class CategoryActivity extends ColorsAppCompatActivity implements SectionRecyclerViewAdapter.ContactsAdapterListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView_categories)
    RecyclerView recyclerViewCategories;
    @BindView(R.id.progressBar_categories)
    ProgressBar progressBarCategories;

    private FirebaseFirestore db;
    List<SectionCategories> stringList = new ArrayList<>();
    private SectionRecyclerViewAdapter categoryAdapter;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        db = FirebaseFirestore.getInstance();

        recyclerViewCategories.setLayoutManager(new GridLayoutManager(this, 2));
        //recyclerViewCategories.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        LoadCategories();

    }

    private void LoadCategories() {
        ProgressBarShow();
        db.collection(Category.FIELD_COLLECTION)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    List<Category> Category = documentSnapshot.toObjects(Category.class);
                    List<String> cat = new ArrayList<>();
                    for (int i = 0; i < Category.size(); i++) {
                        cat.add(Category.get(i).getName());
                    }
                    LoadSubCategories(cat);
                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show());
    }

    private void LoadSubCategories(List<String> category) {
        categoryAdapter = new SectionRecyclerViewAdapter(getApplicationContext(), stringList, this);
        recyclerViewCategories.setAdapter(categoryAdapter);


        for (int i = 0; i < category.size(); i++) {
            int finalI = i;
//            db.collection(SubCategory.FIELD_COLLECTION)
//                    .document(category.get(i))
//                    .collection("SubCategories")
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            List<SubCategory> subCategoryList = new ArrayList<>();
//                            for (DocumentSnapshot document : task.getResult()) {
//                                SubCategory SubCategory = document.toObject(SubCategory.class);
//                                Log.e(document.getId(), " => " + SubCategory.getCategories());
//                                subCategoryList.add(SubCategory);
//                            }
//                            stringList.add(new SectionCategories(category.get(finalI)));
//                            categoryAdapter.notifyDataSetChanged();
//                        }
//                    });

            stringList.add(new SectionCategories(category.get(finalI)));
            categoryAdapter.notifyDataSetChanged();
        }
        ProgressBarHide();
    }

    private void ProgressBarShow() {
        progressBarCategories.setVisibility(View.VISIBLE);
        recyclerViewCategories.setVisibility(View.INVISIBLE);
    }

    private void ProgressBarHide() {
        progressBarCategories.setVisibility(View.GONE);
        recyclerViewCategories.setVisibility(View.VISIBLE);
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
        if (id == R.id.action_search) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
        }
//        switch (id) {
//            case android.R.id.home:
//                /*if (mDrawerToggle.onOptionsItemSelected(item)) {
//                    return true;
//                }*/
//                onBackPressed();
//                break;
//            case R.id.action_search:
//                return true;
//
//
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContactSelected(SectionCategories contact) {

    }
}
