package inovapps.upopular;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hallpaz on 13/03/2016.
 */

public class HealthPlaceRecyclerAdapter extends RecyclerView.Adapter<HealthPlaceRecyclerAdapter.HealthPlaceViewHolder> {

    static class Constants {
        public static final int NAME = 0;
        //address
        public static final int STREET = 1;
        public static final int NUMBER = 2;
        //public static final int COMPLEMENT = 0;
        public static final int DISTRICT = 3;
        public static final int CITY = 4;
        public static final int STATE = 5;

        public static final int LAT = 6;
        public static final int LONG = 7;
        public static final int PORT = 8;
        public static final int PHONE = 9;

    }

    private Context context;
    //private List<HealthPlace> placeList;
    private List<List<String>> placeList;
    private List<String> nearestKeys;
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

    HealthPlaceRecyclerAdapter(Context c, LatLng location, double searchRadius, List<List<String>> dataList){
        context = c;
        placeList = dataList;
        userLocation = location;
        radius = searchRadius*1000;
    }

    HealthPlaceRecyclerAdapter(Context c, LatLng location, double searchRadius, Map<String, List<String>> dataMap){
        context = c;
        userLocation = location;
        radius = searchRadius*1000;

        placeList = new ArrayList<List<String>>();
        placeList.addAll(dataMap.values());

        Collections.sort(placeList, new Comparator<List<String>>() {
            @Override
            public int compare(List<String> lhs, List<String> rhs) {
                double lhsLat = Double.parseDouble(lhs.get(Constants.LAT));
                double lhsLong = Double.parseDouble(lhs.get(Constants.LONG));
                double lhsDist = Utils.distance(lhsLat, lhsLong, userLocation.latitude, userLocation.longitude);

                double rhsLat = Double.parseDouble(rhs.get(Constants.LAT));
                double rhsLong = Double.parseDouble(rhs.get(Constants.LONG));
                double rhsDist = Utils.distance(rhsLat, rhsLong, userLocation.latitude, userLocation.longitude);

                return (int) (lhsDist - rhsDist);
            }
        });
    }

    HealthPlaceRecyclerAdapter(Context c, LatLng location, double searchRadius){
        context = c;
        userLocation = location;
        radius = searchRadius*1000;

        placeList = new ArrayList<List<String>>();
        nearestKeys = new ArrayList<String>();


        loadData();
        //loaddata on background
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        });*/

    }

    @Override
    public HealthPlaceRecyclerAdapter.HealthPlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.health_card, parent, false);
        HealthPlaceViewHolder hpvh = new HealthPlaceViewHolder(view);
        return hpvh;
    }

    @Override
    public void onBindViewHolder(HealthPlaceRecyclerAdapter.HealthPlaceViewHolder holder, int position) {
        /*if (position >= getItemCount()-1){
            loadMore();
        }*/
        //HealthPlace model = placeList.get(position);
        List<String> data = placeList.get(position);
        //this.populateViewHolder(holder, model, position);
        this.populateViewHolder(holder, data, position);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public String fullAddress(List<String> data){
        String street = data.get(Constants.STREET);
        String number = data.get(Constants.NUMBER);
        String district = data.get(Constants.DISTRICT);
        String city = data.get(Constants.CITY);
        String state = data.get(Constants.STATE);

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

        viewHolder.healthPlaceName.setText(place.get(Constants.NAME));
        viewHolder.address.setText(fullAddress(place));

        DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );

        if (place.get(Constants.LAT) != null && place.get(Constants.LONG) != null) {
            double latitude = Double.parseDouble(place.get(Constants.LAT));
            double longitude = Double.parseDouble(place.get(Constants.LONG));
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
                detailsIntent.putExtra("tipo", "UPA");
                detailsIntent.putExtra("nome", place.get(Constants.NAME));

                // Address
                detailsIntent.putExtra("logradouro", place.get(Constants.STREET));
                detailsIntent.putExtra("bairro", place.get(Constants.DISTRICT));
                detailsIntent.putExtra("cidade", place.get(Constants.CITY));
                detailsIntent.putExtra("estado", place.get(Constants.STATE));
                detailsIntent.putExtra("porte", place.get(Constants.PORT));
                detailsIntent.putExtra("telefone", place.get(Constants.PHONE));

                context.startActivity(detailsIntent);
            }
        });
    }

    /*public void populateViewHolder(HealthPlaceRecyclerAdapter.HealthPlaceViewHolder viewHolder, final HealthPlace place, int position){
        viewHolder.healthPlaceName.setText(place.getName());
        viewHolder.address.setText(place.getFullAddress());
        // double distance = Utils.distance(userLocation, place.getAddress().getLocation());
        //TODO: deal with it!
        double distance = 7.777;
        DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.00" );
        viewHolder.distance.setText(df2.format(distance));


        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent detailsIntent = new Intent(context, DetailsActivity.class);
                detailsIntent.putExtra("tipo", place.getType());
                detailsIntent.putExtra("nome", place.getName());

                // Address
                detailsIntent.putExtra("logradouro", place.getStreet());
                detailsIntent.putExtra("bairro", place.getDistrict());
                detailsIntent.putExtra("cidade", place.getCity());
                detailsIntent.putExtra("estado", place.getState());

                context.startActivity(detailsIntent);
            }
        });
    }
*/

    private void loadData(){
        placeList.add(Arrays.asList("UPA Sãens Peña", "Rua Conde de Bonfim", "330", "Tijuca", "Rio de Janeiro", "RJ", null, null, "1", "(21) 2222-3333"));
        placeList.add(Arrays.asList("UPA Botafogo", "Rua São Clemente", "227", "Botafogo", "Rio de Janeiro", "RJ", null, null, "3", "(21) 3333-4444"));
    }

    public void setPlaceList(List<List<String>> placeList) {
        this.placeList = placeList;
        notifyDataSetChanged();
    }

    public void setRadius(double radius) {
        this.radius = radius*1000;
    }


    /* private void loadData(){
        InputStream inputStream = context.getResources().openRawResource(R.raw.upa_funcionamento_latlng);
        CSVReader csvFile = new  CSVReader (inputStream);
        HashMap<String, String[]> currentData = csvFile.read();

        //Check for all keys near location
        for(Map.Entry<String, String[]> entry : currentData.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            double latitude = Double.parseDouble(value[0]);
            double longitude = Double.parseDouble(value[1]);

            if (Utils.distance(latitude, longitude, userLocation.latitude, userLocation.longitude) < radius){
                nearestKeys.add(key);
            }
        }

        inputStream = context.getResources().openRawResource(R.raw.upa_funcionamento);
        csvFile = new  CSVReader (inputStream);
        currentData = csvFile.read();

        for (String key: nearestKeys) {
            String[] values = currentData.get(key);
            placeList.add(new HealthPlace(values[1], values[2], "23456-7890"));
            notifyItemInserted(placeList.size() - 1);
        }

    }*/


    /*public void loadMore(){
        //TODO: write after data model specification
    }*/
}


