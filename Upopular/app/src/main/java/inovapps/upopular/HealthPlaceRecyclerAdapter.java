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

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hallpaz on 13/03/2016.
 */
public class HealthPlaceRecyclerAdapter extends RecyclerView.Adapter<HealthPlaceRecyclerAdapter.HealthPlaceViewHolder> {

    private Context context;
    private List<HealthPlace> placeList;
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

    HealthPlaceRecyclerAdapter(Context c, LatLng location, double searchRadius, List<HealthPlace> dataList){
        context = c;
        placeList = dataList;
        userLocation = location;
        radius = searchRadius*1000;
    }

    HealthPlaceRecyclerAdapter(Context c, LatLng location, double searchRadius){
        context = c;
        userLocation = location;
        radius = searchRadius*1000;

        placeList = new ArrayList<HealthPlace>();
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
        HealthPlace model = placeList.get(position);
        this.populateViewHolder(holder, model, position);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }


    public void populateViewHolder(HealthPlaceRecyclerAdapter.HealthPlaceViewHolder viewHolder, final HealthPlace place, int position){
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

    private void loadData(){


        placeList.add(new HealthPlace("UPA Sãens Peña", new MyAddress("Rua Conde de Bonfim, 330", null, null, null, null), "(21) 2222-3333"));
        placeList.add(new HealthPlace("UPA Botafogo", new MyAddress("Rua São Clemente, 227", null, null, null, null), "(21) 3333-4444"));

    }

    public void setPlaceList(List<HealthPlace> placeList) {
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


