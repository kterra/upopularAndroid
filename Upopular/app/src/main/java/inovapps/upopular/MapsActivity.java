package inovapps.upopular;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        readUPAData();
        readFPBData();
        readFPEData();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

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
                String id = arg0.getSnippet();
                if(upasList.get(id)!=null){
                    String[] upaInfo = upasList.get(id);

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
                if(fpbList.get(id)!=null){
                    String[] fpbInfo = fpbList.get(id);

                    // Getting reference to the TextView to set latitude
                    TextView title = (TextView) v.findViewById(R.id.tv_title);
                    title.setVisibility(View.GONE);
                    // Getting reference to the TextView to set longitude
                    TextView subtitle1 = (TextView) v.findViewById(R.id.tv_subtitle1);
                    subtitle1.setText(fpbInfo[0]);

                    // Getting reference to the TextView to set longitude
                    TextView subtitle2 = (TextView) v.findViewById(R.id.tv_subtitle2);
                    subtitle2.setText(fpbInfo[1] + ", " + fpbInfo[2]);
                }


                // Returning the view containing InfoWindow contents
                return v;

            }

        });




        for(Entry<String, String[]> entry : upasList.entrySet()) {
            String upaId = entry.getKey();

            if(!upaId.equals("gid")){
                String [] latLng = upasLatLngList.get(upaId);

                Log.d("upa", latLng[0] + latLng[1]);
                LatLng upaLatLong = new LatLng(Float.valueOf(latLng[0]), Float.valueOf(latLng[1]));
                mMap.addMarker(new MarkerOptions()
                        .position(upaLatLong)
                        .snippet(upaId));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(upaLatLong));
            }
        }

        for(Entry<String, String[]> entry : fpbList.entrySet()) {
            String fpbId = entry.getKey();

            if(!fpbId.equals("gid")){
                String [] latLng = fpbLatLngList.get(fpbId);

                Log.d("fpb", latLng[0] + latLng[1]);
                LatLng fpbLatLong = new LatLng(Float.valueOf(latLng[0]), Float.valueOf(latLng[1]));
                mMap.addMarker(new MarkerOptions()
                        .position(fpbLatLong)
                        .snippet(fpbId));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(fpbLatLong));
            }
        }


    }


}
