package inovapps.upopular;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HealthListActivity extends AppCompatActivity implements HealthPlaceFragmentList.HealthDataHolder {

    private HashMap<String, ArrayList<String>> upaData;
    private HashMap<String, ArrayList<String>> phData;
    private LatLng userLatLng;
    protected ViewPagerAdapter viewPagerAdapter;
    private final String TAG = "HEALTHLIST";
    private boolean searchIsActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_list);

        //setContentView(R.layout.activity_main_pager);

        // Locate the viewpager in activity_main.xml
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        // Set the ViewPagerAdapter into ViewPager
        viewPager.setAdapter(viewPagerAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//
        Intent srcIntent = getIntent();
        double latitude = srcIntent.getDoubleExtra(Constants.LATITUDE, MapsActivity.BRASILIA_LATITUDE);
        double longitude = srcIntent.getDoubleExtra(Constants.LONGITUDE, MapsActivity.BRASILIA_LONGITUDE);
        userLatLng = new LatLng(latitude, longitude);
        upaData = (HashMap<String, ArrayList<String>>) srcIntent.getSerializableExtra(Constants.UPA);
        phData = (HashMap<String, ArrayList<String>>) srcIntent.getSerializableExtra(Constants.PH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_healthlist, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // Assumes current activity is the searchable activity
        MenuItem searchItem = menu.findItem(R.id.menu_search_view);
        // Get the SearchView and set the searchable configuration
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() && searchIsActive){
                    searchIsActive = false;
                    new AccessDataBaseList().execute(newText);
                }
                return true;
            }
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);


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

    private void doMySearch(String query){
        //do nothing
        searchIsActive = true;
        new AccessDataBaseList().execute(query);
    }

    @Override
    public HashMap<String, ArrayList<String>> uploadUpaData() {
        return upaData;
    }

    @Override
    public HashMap<String, ArrayList<String>> uploadPHBrasilData() {
        return phData;
    }

    @Override
    public LatLng uploadLatLng() {
        return userLatLng;
    }

    public class AccessDataBaseList extends AsyncTask<String, String, List<HashMap<String, ArrayList<String>>>> {

        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            progressBar =  (ProgressBar) findViewById(R.id.search_progress_bar);
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected List<HashMap<String, ArrayList<String>>> doInBackground(String... params) {
            List<HashMap<String, ArrayList<String>>> data = new ArrayList<HashMap<String, ArrayList<String>>>();
            DatabaseHelper dbHelper = new DatabaseHelper(HealthListActivity.this);
            String query = params[0];
            if (query.isEmpty()){
                upaData = dbHelper.getUPAMainData(userLatLng.latitude, userLatLng.longitude);
                phData = dbHelper.getPHMainData(userLatLng.latitude, userLatLng.longitude);
            }
            else {
                upaData = dbHelper.getUpaByQuery(query, userLatLng);
                phData = dbHelper.getPHByQuery(query, userLatLng);
            }

            data.add(upaData);
            data.add(phData);
            return data;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, ArrayList<String>>> data) {

            HealthListActivity.this.viewPagerAdapter.updateUPAs(data.get(0), userLatLng);
            HealthListActivity.this.viewPagerAdapter.updatePHs(data.get(1), userLatLng);
            progressBar.setVisibility(View.GONE);
        }
    }
}
