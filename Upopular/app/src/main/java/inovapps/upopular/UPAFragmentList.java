package inovapps.upopular;

/**
 * Created by hallpaz on 09/10/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

public class UPAFragmentList extends Fragment {

    private RecyclerView healthPlaceRecyclerView;
    private HealthPlaceRecyclerAdapter UPAadapter;
    private Map<String, List<String>> upaData;
    private LatLng currentLatLng;

    private HealthDataHolder myDataHolder;

    public interface HealthDataHolder {
        public Map<String, List<String>> uploadData();
        public LatLng uploadLatLng();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.content_health_list, container, false);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        healthPlaceRecyclerView = (RecyclerView) view.findViewById(R.id.places_recycler_view);
        healthPlaceRecyclerView.setLayoutManager(llm);
        healthPlaceRecyclerView.requestFocus();
        healthPlaceRecyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData(myDataHolder.uploadData(), myDataHolder.uploadLatLng());
    }

    public void setData(Map<String, List<String>> data, LatLng latLng){
        upaData = data;
        currentLatLng = latLng;
        UPAadapter = new HealthPlaceRecyclerAdapter(getContext(), currentLatLng, 100.00, upaData);
        healthPlaceRecyclerView.setAdapter(UPAadapter);
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
    /*private void initializeAdapters(){

    }*/

}
