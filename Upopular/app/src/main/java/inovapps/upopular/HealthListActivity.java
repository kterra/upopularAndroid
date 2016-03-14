package inovapps.upopular;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class HealthListActivity extends AppCompatActivity {

    private RecyclerView healthPlaceRecyclerView;
    private HealthPlaceRecyclerAdapter pharmacyAdapter;
    private HealthPlaceRecyclerAdapter UPAadapter;

    private boolean lookingToPharmacies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_list);

        lookingToPharmacies = false;
    }


    private void initializeAdapters(){
        //pharmacyAdapter = new HealthPlaceRecyclerAdapter();
        //UPAadapter = new HealthPlaceRecyclerAdapter();

        if (lookingToPharmacies){
            healthPlaceRecyclerView.setAdapter(pharmacyAdapter);
        }
        else{
            healthPlaceRecyclerView.setAdapter(UPAadapter);
        }
    }

}
