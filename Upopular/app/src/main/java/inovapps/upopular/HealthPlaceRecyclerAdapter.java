package inovapps.upopular;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hallpaz on 13/03/2016.
 */

public class HealthPlaceRecyclerAdapter extends RecyclerView.Adapter<HealthPlaceRecyclerAdapter.HealthPlaceViewHolder> {



    private Context context;
    private List<List<String>> placeList;
    private List<String> nearestKeys;
    private LatLng userLocation;
    private double radius;
    private boolean isEmpty = false;
    private final String emptyTextTitle = "Nenhum Estabelecimento Encontrado";
    private final String emptyTextSuggestion = "Que tal fazer uma nova busca com outros termos?";

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

    HealthPlaceRecyclerAdapter(Context c, LatLng location, double searchRadius, List<List<String>> dataList){
        context = c;
        placeList = dataList;
        userLocation = location;
        radius = searchRadius*1000;
    }

    HealthPlaceRecyclerAdapter(Context c, LatLng location, double searchRadius, HashMap<String, ArrayList<String>> dataMap){
        context = c;
        userLocation = location;
        radius = searchRadius*1000;

        placeList = new ArrayList<List<String>>();
        placeList.addAll(dataMap.values());

        Collections.sort(placeList, new Comparator<List<String>>() {
            @Override
            public int compare(List<String> lhs, List<String> rhs) {
                double lhsLat = Double.parseDouble(lhs.get(Constants.LAT_INDEX));
                double lhsLong = Double.parseDouble(lhs.get(Constants.LONG_INDEX));
                double lhsDist = Utils.distance(lhsLat, lhsLong, userLocation.latitude, userLocation.longitude);

                double rhsLat = Double.parseDouble(rhs.get(Constants.LAT_INDEX));
                double rhsLong = Double.parseDouble(rhs.get(Constants.LONG_INDEX));
                double rhsDist = Utils.distance(rhsLat, rhsLong, userLocation.latitude, userLocation.longitude);

                return (int) (lhsDist - rhsDist);
            }
        });
    }

    @Override
    public HealthPlaceRecyclerAdapter.HealthPlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.health_card, parent, false);
        HealthPlaceViewHolder hpvh = new HealthPlaceViewHolder(view);
        return hpvh;
    }

    @Override
    public void onBindViewHolder(HealthPlaceRecyclerAdapter.HealthPlaceViewHolder holder, int position) {
        if (isEmpty){
            this.populateViewHolder(holder, null, position);
        }
        else {
            List<String> data = placeList.get(position);
            this.populateViewHolder(holder, data, position);
        }
    }

    @Override
    public int getItemCount() {
        if (placeList.size() == 0){
            isEmpty = true;
            return 1;
        }
        isEmpty = false;
        return placeList.size();
    }

    public String fullAddress(List<String> data){
        String street = data.get(Constants.STREET_INDEX);
        String number = data.get(Constants.NUMBER_INDEX);
        String district = data.get(Constants.DISTRICT_INDEX);
        String city = data.get(Constants.CITY_INDEX);
        String state = data.get(Constants.STATE_INDEX);

        String representation = "";
        boolean shouldPutComma = false;
        if (street != "" && street != null){
            representation += street;
            shouldPutComma = true;
        }
        if (number != null && !number.isEmpty()){
            if(shouldPutComma)
                representation += ", ";
            representation += number;
            shouldPutComma = true;
        }

        if (district != null && !district.isEmpty()){
            if(shouldPutComma)
                representation += ", ";
            representation += district;
            shouldPutComma = true;
        }
        if (city != null && !city.isEmpty()) {
            if (shouldPutComma)
                representation += ", ";
            representation += city;
            shouldPutComma = true;
        }
        if (state != null && !state.isEmpty()) {
            representation += " (" + state + ")";
        }
        return representation;
    }

    public void populateViewHolder(HealthPlaceRecyclerAdapter.HealthPlaceViewHolder viewHolder, final List<String> place, int position){

        if (isEmpty){
            viewHolder.healthPlaceName.setText(emptyTextTitle);
            viewHolder.address.setText(emptyTextSuggestion);
            return;
        }

        viewHolder.healthPlaceName.setText(place.get(Constants.NAME_INDEX));
        viewHolder.address.setText(fullAddress(place));

        DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );

        if (place.get(Constants.LAT_INDEX) != null && place.get(Constants.LONG_INDEX) != null) {
            double latitude = Double.parseDouble(place.get(Constants.LAT_INDEX));
            double longitude = Double.parseDouble(place.get(Constants.LONG_INDEX));
            viewHolder.distance.setText( "" +
                    df2.format(Utils.distance(latitude, longitude, userLocation.latitude, userLocation.longitude)/1000.0) +
                    " km"
            );
        }
        else {
            viewHolder.distance.setText("N/D");
        }


        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent detailsIntent = new Intent(context, DetailsActivity.class);
                String kind = place.get(Constants.PORT_INDEX).isEmpty() ? Constants.PH : Constants.UPA;

                detailsIntent.putExtra(Constants.KIND, kind);

                detailsIntent.putExtra(Constants.NAME, place.get(Constants.NAME_INDEX));

                // Address
                if (place.get(Constants.NUMBER_INDEX) != null && !place.get(Constants.NUMBER_INDEX).isEmpty()){
                    detailsIntent.putExtra(Constants.STREET, place.get(Constants.STREET_INDEX)+ ", " + place.get(Constants.NUMBER_INDEX));
                }
                else {
                    detailsIntent.putExtra(Constants.STREET, place.get(Constants.STREET_INDEX));
                }

                detailsIntent.putExtra(Constants.DISTRICT, place.get(Constants.DISTRICT_INDEX));
                detailsIntent.putExtra(Constants.CITY, place.get(Constants.CITY_INDEX));
                detailsIntent.putExtra(Constants.STATE, place.get(Constants.STATE_INDEX));

                detailsIntent.putExtra(Constants.LATITUDE, place.get(Constants.LAT_INDEX));
                detailsIntent.putExtra(Constants.LONGITUDE, place.get(Constants.LONG_INDEX));

                detailsIntent.putExtra(Constants.PORT, place.get(Constants.PORT_INDEX));
                detailsIntent.putExtra(Constants.PHONE, place.get(Constants.PHONE_INDEX));

                detailsIntent.putExtra(Constants.ZIPCODE, place.get(Constants.ZIPCODE_INDEX));

                context.startActivity(detailsIntent);
            }
        });
    }

    public void setPlaceList(List<List<String>> placeList) {
        this.placeList = placeList;
        notifyDataSetChanged();
    }

    public void setRadius(double radius) {
        this.radius = radius*1000;
    }
}


