package inovapps.upopular;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

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
