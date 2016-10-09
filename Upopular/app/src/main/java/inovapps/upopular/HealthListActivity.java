package inovapps.upopular;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.util.ArrayList;
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
        new AccessDataBaseList(this).execute(query);
    }

    public class AccessDataBaseList extends AsyncTask<String, String, Map<String, List<String>>> {


        private ProgressDialog dialog;
        private Context mContext;

        public AccessDataBaseList(Context context){
            mContext = context;
        }


        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(HealthListActivity.this);
            dialog.setTitle("Carregando os dados");
            dialog.setMessage("Por favor, aguarde...");
            dialog.setCancelable(false);
            dialog.setIcon(android.R.drawable.ic_dialog_info);
            dialog.show();
        }

        @Override
        protected Map<String, List<String>> doInBackground(String... params) {
//            double currentCosLng = Math.cos(MathUtil.deg2rad(currentLng));
//            double currentSinLng = Math.sin(MathUtil.deg2rad(currentLng));

            // double cos_allowed_distance = Math.cos(20.0 / 6371);
            DatabaseHelper dbHelper = new DatabaseHelper(HealthListActivity.this);
            upaData = dbHelper.getUpaByQuery(params[0]);

            return upaData;
        }

        protected void onPostExecute(Map<String, List<String>> data) {

            LatLng userLocation = new LatLng(-22.924315, -43.2411521);
            UPAadapter = new HealthPlaceRecyclerAdapter(mContext, userLocation, 100.00, data);
            healthPlaceRecyclerView.setAdapter(UPAadapter);
            dialog.cancel();
        }
    }


//    public HashMap<String, List<String>> getUpaByQuery(String query)
//    {
//        HashMap<String, List<String>> upaData = new HashMap<>();
//
//        //hp = new HashMap();
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor res =  db.rawQuery( "select gid, nome_fantasia, logradouro, numero, bairro, cidade, estado, latitude, longitude, porte, telefone from fts_table WHERE fts_table MATCH " + query +
//                " ORDER BY abs(latitude) + abs(longitude) LIMIT 5;", null );
//        res.moveToFirst();
//
//        while(res.isAfterLast() == false){
//            ArrayList<String> singleUPA = new ArrayList<>();
//            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_NAME)));
//            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_STREET)));
//            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_NUMBER)));
//            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_DISTRICT)));
//            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_CITY)));
//            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_STATE)));
//            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_LAT)));
//            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_LONG)));
//            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_PORTE)));
//            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_PHONE)));
//
//            upaData.put(res.getString(res.getColumnIndex(UPAS_COLUMN_ID)), singleUPA);
//            res.moveToNext();
//        }
//        return upaData;
//    }
}
