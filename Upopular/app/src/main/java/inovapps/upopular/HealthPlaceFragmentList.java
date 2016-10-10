package inovapps.upopular;

/**
 * Created by hallpaz on 09/10/2016.
 */
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

public abstract class HealthPlaceFragmentList extends Fragment {

    private RecyclerView healthPlaceRecyclerView;
    private HealthPlaceRecyclerAdapter healthAdapter;
    //private Map<String, List<String>> healthData;
    //private LatLng currentLatLng;

    protected HealthDataHolder myDataHolder;

    public interface HealthDataHolder {
        public Map<String, List<String>> uploadUpaData();
        public Map<String, List<String>> uploadPHBrasilData();
        public LatLng uploadLatLng();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_health_list, container, false);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        healthPlaceRecyclerView = (RecyclerView) view.findViewById(R.id.places_recycler_view);
        healthPlaceRecyclerView.setLayoutManager(llm);
        healthPlaceRecyclerView.requestFocus();
        healthPlaceRecyclerView.setHasFixedSize(true);

        return view;
    }


    public void setData(Map<String, List<String>> data, LatLng latLng){
        //healthData = data;
        //currentLatLng = latLng;
        healthAdapter = new HealthPlaceRecyclerAdapter(getContext(), latLng, 100.00, data);
        healthPlaceRecyclerView.setAdapter(healthAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            myDataHolder = (HealthDataHolder) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement HealthDataHolder");
        }
    }

}
