package inovapps.upopular;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<String, String[]> upasLatLngList;
    private HashMap<String, String[]> upasList;
    private HashMap<String, String[]> upasDetailsList;
    private HashMap<String, String[]> fpbLatLngList;
    private HashMap<String, String[]> fpbList;
    private HashMap<String, String[]> fpbDetailsList;
    private HashMap<String, String[]> fpeLatLngList;
    private HashMap<String, String[]> fpeList;
    private HashMap<String, String[]> fpeDetailsList;
    private boolean upaSelected;
    private boolean fpbSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        readUPAData();
        //readFPBData();
        //readFPEData();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        View mapView = (mapFragment.getFragmentManager().findFragmentById(R.id.map)).getView();
        View btnMyLocation = ((View) mapView.findViewById(1).getParent()).findViewById(2);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(80,80); // size of button in dp
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.setMargins(0, 0, 20, 150);
        btnMyLocation.setLayoutParams(params);


    }

    private void readUPAData(){
        InputStream inputStream = getResources().openRawResource(R.raw.upa_funcionamento_latlng);
        CSVReader csvFile = new  CSVReader (inputStream);
        upasLatLngList = csvFile.read();

        inputStream = getResources().openRawResource(R.raw.upa_funcionamento);
        csvFile = new  CSVReader (inputStream);
        upasList = csvFile.read();

        inputStream = getResources().openRawResource(R.raw.upa_funcionamento_detalhes);
        csvFile = new  CSVReader (inputStream);
        upasDetailsList = csvFile.read();
    }

    private void readFPBData(){
        InputStream inputStream = getResources().openRawResource(R.raw.farmacia_popular_brasil_latlng);
        CSVReader csvFile = new  CSVReader (inputStream);
        fpbLatLngList = csvFile.read();

        inputStream = getResources().openRawResource(R.raw.farmacia_popular_brasil);
        csvFile = new  CSVReader (inputStream);
        fpbList = csvFile.read();

        inputStream = getResources().openRawResource(R.raw.farmacia_popular_brasil_detalhes);
        csvFile = new  CSVReader (inputStream);
        fpbDetailsList = csvFile.read();
    }

    private void readFPEData(){
        InputStream inputStream = getResources().openRawResource(R.raw.farmacia_popular_estabelecimento_latlng);
        CSVReader csvFile = new  CSVReader (inputStream);
        fpeLatLngList = csvFile.read();

        inputStream = getResources().openRawResource(R.raw.farmacia_popular_estabelecimento);
        csvFile = new  CSVReader (inputStream);
        fpeList = csvFile.read();

        inputStream = getResources().openRawResource(R.raw.farmacia_popular_estabelecimento_detalhes);
        csvFile = new  CSVReader (inputStream);
        fpeDetailsList = csvFile.read();
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {




        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        try{
            mMap.setMyLocationEnabled(true);
        }catch (SecurityException e){

        }


        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.info_window, null);

                // Getting the position from the marker
                String kind = arg0.getTitle();
                if(kind.equals("upa")){
                    String[] upaInfo = upasList.get(arg0.getSnippet());

                    // Getting reference to the TextView to set latitude
                    TextView title = (TextView) v.findViewById(R.id.tv_title);
                    title.setText(upaInfo[0]);
                    // Getting reference to the TextView to set longitude
                    TextView subtitle1 = (TextView) v.findViewById(R.id.tv_subtitle1);
                    subtitle1.setText(upaInfo[1] + ", " + upaInfo[2] + "-" + upaInfo[3]);

                    // Getting reference to the TextView to set longitude
                    TextView subtitle2 = (TextView) v.findViewById(R.id.tv_subtitle2);
                    subtitle2.setText(upaInfo[4] + ", " + upaInfo[5]);
                }
                if(kind.equals("fpb")){
                    String[] fpbInfo = fpbList.get(arg0.getSnippet());

                    // Getting reference to the TextView to set latitude
                    TextView title = (TextView) v.findViewById(R.id.tv_title);
                    title.setText("FÁRMACIA POPULAR DO BRASIL");
                    // Getting reference to the TextView to set longitude
                    TextView subtitle1 = (TextView) v.findViewById(R.id.tv_subtitle1);
                    subtitle1.setText(fpbInfo[0]);

                    // Getting reference to the TextView to set longitude
                    TextView subtitle2 = (TextView) v.findViewById(R.id.tv_subtitle2);
                    subtitle2.setText(fpbInfo[1] + ", " + fpbInfo[2]);
                }

                if(kind.equals("fpe")){
                    String[] fpeInfo = fpeList.get(arg0.getSnippet());

                    // Getting reference to the TextView to set latitude
                    TextView title = (TextView) v.findViewById(R.id.tv_title);
                    title.setText(fpeInfo[0]);
                    // Getting reference to the TextView to set longitude
                    TextView subtitle1 = (TextView) v.findViewById(R.id.tv_subtitle1);
                    subtitle1.setText(fpeInfo[1]);

                    // Getting reference to the TextView to set longitude
                    TextView subtitle2 = (TextView) v.findViewById(R.id.tv_subtitle2);
                    subtitle2.setText(fpeInfo[2] + ", " + fpeInfo[3]);
                }


                // Returning the view containing InfoWindow contents
                return v;

            }

        });



        upaSelected = true;
        for(Entry<String, String[]> entry : upasList.entrySet()) {
            String upaId = entry.getKey();

            if(!upaId.equals("gid")){
                String [] latLng = upasLatLngList.get(upaId);

               // Log.d("upa", latLng[0] + latLng[1]);
                LatLng upaLatLong = new LatLng(Float.valueOf(latLng[0]), Float.valueOf(latLng[1]));
                mMap.addMarker(new MarkerOptions()
                        .title("upa")
                        .position(upaLatLong)
                        .snippet(upaId)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(upaLatLong));
            }
        }
//
//        fpbSelected = true;
//        for(Entry<String, String[]> entry : fpbList.entrySet()) {
//            String fpbId = entry.getKey();
//
//            if(!fpbId.equals("gid")){
//                String [] latLng = fpbLatLngList.get(fpbId);
//
//               // Log.d("fpb", latLng[0] + latLng[1]);
//                LatLng fpbLatLong = new LatLng(Float.valueOf(latLng[0]), Float.valueOf(latLng[1]));
//                mMap.addMarker(new MarkerOptions()
//                        .title("fpb")
//                        .position(fpbLatLong)
//                        .snippet(fpbId)
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(fpbLatLong));
//            }
//        }

//        for(Entry<String, String[]> entry : fpeList.entrySet()) {
//            String fpeId = entry.getKey();
//
//            if(!fpeId.equals("id")){
//                String [] latLng = fpeLatLngList.get(fpeId);
//
//                Log.d("fpe", latLng[0] + latLng[1]);
//                LatLng fpeLatLong = new LatLng(Float.valueOf(latLng[0]), Float.valueOf(latLng[1]));
//                mMap.addMarker(new MarkerOptions()
//                        .title("fpe")
//                        .position(fpeLatLong)
//                        .snippet(fpeId)
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(fpeLatLong));
//            }
//        }


    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        Intent intent = new Intent(MapsActivity.this, DetailsActivity.class);

        String kind = marker.getTitle();
        if(kind.equals("upa")){
            String[] upaInfo = upasList.get(marker.getSnippet());
            String[] upaDetails = upasDetailsList.get(marker.getSnippet());

            intent.putExtra("tipo", "UPA");
            intent.putExtra("nome", upaInfo[0]);
            intent.putExtra("logradouro", upaInfo[1]);
            intent.putExtra("bairro", upaInfo[2]);
            intent.putExtra("cidade", upaInfo[3]);
            intent.putExtra("estado", upaInfo[4]);



        }
        if(kind.equals("fpb")){

            String[] fpbInfo = fpbList.get(marker.getSnippet());
            String[] fpbDetails = fpbDetailsList.get(marker.getSnippet());

            intent.putExtra("tipo", "FPB");
            intent.putExtra("nome", "FÁRMACIA POPULAR DO BRASIL");
            intent.putExtra("logradouro", fpbInfo[0]);
            intent.putExtra("bairro", "");
            intent.putExtra("cidade", fpbInfo[1]);
            intent.putExtra("estado", fpbInfo[2]);


        }

        startActivity(intent);

    }

    public void getUPA(View button){
        Button buttonClicked = (Button) button;

        if(upaSelected){
            buttonClicked.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            buttonClicked.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_local_hospital_blue_24dp), null, null, null);
            buttonClicked.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

            mMap.clear();
            if(fpbSelected){
                for(Entry<String, String[]> entry : fpbList.entrySet()) {
                    String fpbId = entry.getKey();

                    if(!fpbId.equals("gid")){
                        String [] latLng = fpbLatLngList.get(fpbId);

                        // Log.d("fpb", latLng[0] + latLng[1]);
                        LatLng fpbLatLong = new LatLng(Float.valueOf(latLng[0]), Float.valueOf(latLng[1]));
                        mMap.addMarker(new MarkerOptions()
                                .title("fpb")
                                .position(fpbLatLong)
                                .snippet(fpbId)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    }
                }
            }

        }else{
            buttonClicked.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            buttonClicked.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_local_hospital_white_24dp), null, null, null);
            buttonClicked.setTextColor(ContextCompat.getColor(this, R.color.white));

            for(Entry<String, String[]> entry : upasList.entrySet()) {
                String upaId = entry.getKey();

                if(!upaId.equals("gid")){
                    String [] latLng = upasLatLngList.get(upaId);

                    // Log.d("upa", latLng[0] + latLng[1]);
                    LatLng upaLatLong = new LatLng(Float.valueOf(latLng[0]), Float.valueOf(latLng[1]));
                    mMap.addMarker(new MarkerOptions()
                            .title("upa")
                            .position(upaLatLong)
                            .snippet(upaId)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
            }

        }



        upaSelected = (!upaSelected);


    }

    public void getFPB(View button){
        Button buttonClicked = (Button) button;

        if(fpbSelected){
            buttonClicked.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            buttonClicked.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_local_pharmacy_blue_24dp), null, null, null);
            buttonClicked.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

            mMap.clear();

            if(upaSelected){
                for(Entry<String, String[]> entry : upasList.entrySet()) {
                    String upaId = entry.getKey();

                    if(!upaId.equals("gid")){
                        String [] latLng = upasLatLngList.get(upaId);

                        // Log.d("upa", latLng[0] + latLng[1]);
                        LatLng upaLatLong = new LatLng(Float.valueOf(latLng[0]), Float.valueOf(latLng[1]));
                        mMap.addMarker(new MarkerOptions()
                                .title("upa")
                                .position(upaLatLong)
                                .snippet(upaId)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    }
                }
            }


                }else{
            buttonClicked.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            buttonClicked.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.drawable.ic_local_pharmacy_white_24dp), null, null, null);
            buttonClicked.setTextColor(ContextCompat.getColor(this, R.color.white));

            for(Entry<String, String[]> entry : fpbList.entrySet()) {
                String fpbId = entry.getKey();

                if(!fpbId.equals("gid")){
                    String [] latLng = fpbLatLngList.get(fpbId);

                    // Log.d("fpb", latLng[0] + latLng[1]);
                    LatLng fpbLatLong = new LatLng(Float.valueOf(latLng[0]), Float.valueOf(latLng[1]));
                    mMap.addMarker(new MarkerOptions()
                            .title("fpb")
                            .position(fpbLatLong)
                            .snippet(fpbId)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }
            }
        }

        fpbSelected = (!fpbSelected);

    }

}
