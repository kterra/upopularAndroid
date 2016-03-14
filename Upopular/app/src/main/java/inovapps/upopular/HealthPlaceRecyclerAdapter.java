package inovapps.upopular;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by hallpaz on 13/03/2016.
 */
public class HealthPlaceRecyclerAdapter extends RecyclerView.Adapter<HealthPlaceRecyclerAdapter.HealthPlaceViewHolder> {

    private List<HealthPlace> placeList;
    private LatLng userLocation;
    private double radius;

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

    HealthPlaceRecyclerAdapter(List<HealthPlace> dataList, LatLng location, double searchRadius){
        placeList = dataList;
        userLocation = location;
        radius = searchRadius*1000;
    }

    @Override
    public HealthPlaceRecyclerAdapter.HealthPlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.health_card, parent, false);
        HealthPlaceViewHolder hpvh = new HealthPlaceViewHolder(view);
        return hpvh;
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


    public void populateViewHolder(HealthPlaceRecyclerAdapter.HealthPlaceViewHolder viewHolder, HealthPlace place, int position){
        viewHolder.healthPlaceName.setText(place.getName());
        viewHolder.address.setText(place.getAddress().toString());
        double distance = Utils.computeDistance(userLocation, place.getAddress().getLocation());
        DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
        viewHolder.distance.setText(df2.format(distance));
    }


    public void loadMore(){
        //TODO: write after data model specification
    }

    public void setPlaceList(List<HealthPlace> placeList) {
        this.placeList = placeList;
        notifyDataSetChanged();
    }

    public void setRadius(double radius) {
        this.radius = radius*1000;
    }
}


