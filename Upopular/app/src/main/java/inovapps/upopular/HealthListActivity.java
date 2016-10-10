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
import java.util.List;
import java.util.Map;

public class HealthListActivity extends AppCompatActivity implements HealthPlaceFragmentList.HealthDataHolder {

    private Map<String, List<String>> upaData;
    private Map<String, List<String>> phData;
    private LatLng userLatLng;
    protected ViewPagerAdapter viewPagerAdapter;

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
//
        Intent srcIntent = getIntent();
        double latitude = srcIntent.getDoubleExtra("latitude", 22.3);
        double longitude = srcIntent.getDoubleExtra("longitude", 43.3);
        userLatLng = new LatLng(latitude, longitude);
        upaData = (Map<String, List<String>>) srcIntent.getSerializableExtra("UPAs");
        phData = (Map<String, List<String>>) srcIntent.getSerializableExtra("PHs");
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
        Log.d("SEARCH", query);
        new AccessDataBaseList().execute(query);
    }

    @Override
    public Map<String, List<String>> uploadData() {
        return upaData;
    }

    @Override
    public LatLng uploadLatLng() {
        return userLatLng;
    }

    public class AccessDataBaseList extends AsyncTask<String, String, List<Map<String, List<String>>>> {

        //private ProgressDialog dialog;
        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            progressBar =  (ProgressBar) findViewById(R.id.search_progress_bar);
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected List<Map<String, List<String>>> doInBackground(String... params) {
//            double currentCosLng = Math.cos(MathUtil.deg2rad(currentLng));
//            double currentSinLng = Math.sin(MathUtil.deg2rad(currentLng));

            // double cos_allowed_distance = Math.cos(20.0 / 6371);
            List<Map<String, List<String>>> data = new ArrayList<Map<String, List<String>>>();
            DatabaseHelper dbHelper = new DatabaseHelper(HealthListActivity.this);
            upaData = dbHelper.getUpaByQuery(params[0], userLatLng);
            phData = dbHelper.getPHByQuery(params[0], userLatLng);

            data.add(upaData);
            data.add(phData);
            return data;
        }

        protected void onPostExecute(List<Map<String, List<String>>> data) {
            //UPAadapter = new HealthPlaceRecyclerAdapter(HealthListActivity.this, userLatLng, 100.00, data);
            //healthPlaceRecyclerView.setAdapter(UPAadapter);
            HealthListActivity.this.viewPagerAdapter.updateUPAs(data.get(0), userLatLng);
            HealthListActivity.this.viewPagerAdapter.updatePHs(data.get(1), userLatLng);
            progressBar.setVisibility(View.GONE);
        }
    }
}
