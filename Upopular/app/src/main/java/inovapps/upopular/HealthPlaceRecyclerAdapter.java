package inovapps.upopular;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by hallpaz on 13/03/2016.
 */
public class HealthPlaceRecyclerAdapter extends RecyclerView.Adapter<HealthPlaceRecyclerAdapter.HealthPlaceViewHolder> {

    private List<HealthPlace> placeList;
    private LatLng userLocation;


    HealthPlaceRecyclerAdapter(List<HealthPlace> dataList, LatLng location){
        placeList = dataList;
        userLocation = location;
    }

    @Override
    public HealthPlaceRecyclerAdapter.HealthPlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(HealthPlaceRecyclerAdapter.HealthPlaceViewHolder holder, int position) {
        if (position >= getItemCount()-1){
            loadMore();
        }
        HealthPlace model = placeList.get(position);
        this.populateViewHolder(holder, model, position);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public static class HealthPlaceViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView healthPlaceName;
        TextView address;
        TextView distance;

        HealthPlaceViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.health_cardview);
            healthPlaceName = (TextView)itemView.findViewById(R.id.health_name);
            address = (TextView)itemView.findViewById(R.id.health_address);
            distance = (TextView) itemView.findViewById(R.id.health_distance);
        }
    }

    public void populateViewHolder(HealthPlaceRecyclerAdapter.HealthPlaceViewHolder viewHolder, HealthPlace place, int position){
        viewHolder.healthPlaceName.setText(place.getName());
        viewHolder.address.setText(place.getAddress().toString());
        double distance = Utils.computeDistance(userLocation, place.getAddress().getLocation());
        viewHolder.distance.setText(Double.toString(distance));
    }


    public void loadMore(){
        //TODO: write after data model specification
    }
}


