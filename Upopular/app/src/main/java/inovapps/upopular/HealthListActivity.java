package inovapps.upopular;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HealthListActivity extends AppCompatActivity {

    private RecyclerView healthPlaceRecyclerView;
    private HealthPlaceRecyclerAdapter pharmacyAdapter;
    private HealthPlaceRecyclerAdapter UPAadapter;

    private Map<String, List<String>> upaData;

    private boolean lookingForPharmacies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent srcIntent = getIntent();
        if (Intent.ACTION_SEARCH.equals(srcIntent.getAction())) {
            String query = srcIntent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
            upaData = (Map<String, List<String>>) srcIntent.getSerializableExtra("UPAs");
        } else{
            upaData = (Map<String, List<String>>) srcIntent.getSerializableExtra("UPAs");
        }

        LinearLayoutManager llm = new LinearLayoutManager(this);
        healthPlaceRecyclerView = (RecyclerView) findViewById(R.id.places_recycler_view);
        healthPlaceRecyclerView.setLayoutManager(llm);
        healthPlaceRecyclerView.requestFocus();
        healthPlaceRecyclerView.setHasFixedSize(true);

        lookingForPharmacies = false;
        initializeAdapters();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_healthlist, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) menu.findItem(R.id.menu_search_view).getActionView();
        // Assumes current activity is the searchable activity

        MenuItem searchItem = menu.findItem(R.id.menu_search_view);
        // Get the SearchView and set the searchable configuration
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);//MenuItemCompat.getActionView(searchItem);//menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);

        //super.onCreateOptionsMenu(menu, inflater);

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("NEW INTENT", "Funcionou!!!!");
            doMySearch(query);
        }
    }


    private void initializeAdapters(){
        //pharmacyAdapter = new HealthPlaceRecyclerAdapter(this,);
        LatLng userLocation = new LatLng(-22.924315, -43.2411521);
        UPAadapter = new HealthPlaceRecyclerAdapter(this, userLocation, 100.00, upaData);

        if (lookingForPharmacies){
            healthPlaceRecyclerView.setAdapter(pharmacyAdapter);
        }
        else{
            healthPlaceRecyclerView.setAdapter(UPAadapter);
        }
    }

    private void doMySearch(String query){
        //do nothing
        Log.d("SEARCH", query);
    }


}
