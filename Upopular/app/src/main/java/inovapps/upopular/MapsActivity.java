package inovapps.upopular;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.*;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnInfoWindowClickListener, OnMapReadyCallback, OnCameraMoveStartedListener, OnCameraIdleListener
{

    private GoogleMap mMap;
    private HashMap<String,ArrayList<String>> upaData;
    private HashMap<String,ArrayList<String>> phBRData;
    private ArrayList<HashMap<String,ArrayList<String>>> data;
    private ArrayList<String> clickedInfo;
    private boolean upaSelected;
    private boolean userGestured;
    private DatabaseHelper dbHelper;
    private Location lastPosition;
    private Location currentPosition;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        lastPosition = new Location("");
        lastPosition.setLatitude(0);
        lastPosition.setLongitude(0);
        currentPosition = new Location("");
        currentPosition.setLatitude(0);
        currentPosition.setLongitude(0);


        dbHelper = new DatabaseHelper(MapsActivity.this);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        View mapView = (mapFragment.getFragmentManager().findFragmentById(R.id.map)).getView();
        View btnMyLocation = ((View) mapView.findViewById(1).getParent()).findViewById(2);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(80, 80); // size of button in dp
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.setMargins(0, 0, 20, 150);
        btnMyLocation.setLayoutParams(params);


    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("MAIN", "ON RESUME");
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
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);


        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
        try {
            mMap.setMyLocationEnabled(true);

        } catch (SecurityException e) {

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
                if (kind.equals("upa")) {
                    clickedInfo = upaData.get(arg0.getSnippet());


                    TextView title = (TextView) v.findViewById(R.id.tv_title);
                    title.setText(clickedInfo.get(0));
                    title.setTextColor(getResources().getColor(R.color.orange));

                    TextView subtitle1 = (TextView) v.findViewById(R.id.tv_subtitle1);
                    subtitle1.setText(clickedInfo.get(1) + ", " + clickedInfo.get(2) + "-" + clickedInfo.get(3));


                    TextView subtitle2 = (TextView) v.findViewById(R.id.tv_subtitle2);
                    subtitle2.setText(clickedInfo.get(4) + ", " + clickedInfo.get(5));
                }
                if (kind.equals("phbrasil")) {
                    clickedInfo = phBRData.get(arg0.getSnippet());


                    TextView title = (TextView) v.findViewById(R.id.tv_title);
                    title.setText(clickedInfo.get(0));
                    title.setTextColor(getResources().getColor(R.color.orange));

                    TextView subtitle1 = (TextView) v.findViewById(R.id.tv_subtitle1);
                    subtitle1.setText(clickedInfo.get(1) + ", " + clickedInfo.get(2));


                    TextView subtitle2 = (TextView) v.findViewById(R.id.tv_subtitle2);
                    subtitle2.setText(clickedInfo.get(3) + ", " + clickedInfo.get(4));
                }


                // Returning the view containing InfoWindow contents
                return v;

            }

        });

        lastPosition.setLatitude(mMap.getCameraPosition().target.latitude);
        lastPosition.setLongitude(mMap.getCameraPosition().target.longitude);


        new AccessDataBase().execute(lastPosition);

    }


    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == OnCameraMoveStartedListener.REASON_GESTURE) {
            userGestured = true;
        }
    }

    @Override
    public void onCameraIdle() {

        if(userGestured == true){

            mMap.clear();
            currentPosition.setLatitude(mMap.getCameraPosition().target.latitude);
            currentPosition.setLongitude(mMap.getCameraPosition().target.longitude);

                new AccessDataBase().execute(currentPosition);
                lastPosition.setLatitude(currentPosition.getLatitude());
                lastPosition.setLongitude(currentPosition.getLongitude());

            userGestured = false;
        }
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

        Intent intent = new Intent(MapsActivity.this, DetailsActivity.class);

        String kind = marker.getTitle();
        if (kind.equals("upa")) {

            intent.putExtra("tipo", "UPA");
            intent.putExtra("nome", clickedInfo.get(0));
            intent.putExtra("logradouro", clickedInfo.get(1) + ", " + clickedInfo.get(2));
            intent.putExtra("bairro", clickedInfo.get(3));
            intent.putExtra("cidade", clickedInfo.get(4));
            intent.putExtra("estado", clickedInfo.get(5));
            intent.putExtra("lat", clickedInfo.get(6));
            intent.putExtra("long", clickedInfo.get(7));
            intent.putExtra("porte", clickedInfo.get(8));
            intent.putExtra("telefone", clickedInfo.get(9));


        }

        if (kind.equals("phbrasil")) {

            intent.putExtra("tipo", "phbrasil");
            intent.putExtra("nome", clickedInfo.get(0));
            intent.putExtra("logradouro", clickedInfo.get(1) + ", " + clickedInfo.get(2));
            intent.putExtra("cidade", clickedInfo.get(3));
            intent.putExtra("estado", clickedInfo.get(4));
            intent.putExtra("lat", clickedInfo.get(5));
            intent.putExtra("long", clickedInfo.get(6));


        }

        startActivity(intent);

    }

    public void showList(View button) {
        Intent listIntent = new Intent(this, HealthListActivity.class);
        listIntent.putExtra("UPAs", upaData);
        startActivity(listIntent);
    }



    public class AccessDataBase extends AsyncTask<Location, String, ArrayList<HashMap<String, ArrayList<String>>>>{


        //private ProgressDialog dialog;

        public AccessDataBase() {

        }

        @Override
        protected void onPreExecute() {
//            dialog = new ProgressDialog(MapsActivity.this);
//            dialog.setTitle("Carregando os dados");
//            dialog.setMessage("Por favor, aguarde...");
//            dialog.setCancelable(false);
//            dialog.setIcon(android.R.drawable.ic_dialog_info);
//            dialog.show();
        }

        @Override
        protected ArrayList<HashMap<String, ArrayList<String>>> doInBackground(Location... params) {



            double currentLat = params[0].getLatitude();
//            double currentCosLat = Math.cos(MathUtil.deg2rad(currentLat));
//            double currentSinLat = Math.sin(MathUtil.deg2rad(currentLat));

            double currentLng = params[0].getLongitude();
//            double currentCosLng = Math.cos(MathUtil.deg2rad(currentLng));
//            double currentSinLng = Math.sin(MathUtil.deg2rad(currentLng));

           // double cos_allowed_distance = Math.cos(20.0 / 6371);

            upaData = dbHelper.getUPAMainData(currentLat, currentLng);
            phBRData = dbHelper.getPHMainData(currentLat, currentLng);
            data = new ArrayList<>();
            data.add(upaData);
            data.add(phBRData);

            return data;
        }

        protected void onPostExecute(ArrayList<HashMap<String, ArrayList<String>>> data) {

            upaSelected = true;
            for (Entry<String, ArrayList<String>> entry : data.get(0).entrySet()) {

                String upaId = entry.getKey();
                ArrayList<String> singleUPAData = entry.getValue();


                Float upaLat = Float.valueOf(singleUPAData.get(6));
                Float upaLong = Float.valueOf(singleUPAData.get(7));

                LatLng upaLatLong = new LatLng(upaLat, upaLong);
                mMap.addMarker(new MarkerOptions()
                        .title("upa")
                        .position(upaLatLong)
                        .snippet(upaId)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(upaLatLong));

            }

            for (Entry<String, ArrayList<String>> entry : data.get(1).entrySet()) {

                String phBRId = entry.getKey();
                ArrayList<String> singlephBRData = entry.getValue();


                Float upaLat = Float.valueOf(singlephBRData.get(5));
                Float upaLong = Float.valueOf(singlephBRData.get(6));

                LatLng phBRLatLong = new LatLng(upaLat, upaLong);
                mMap.addMarker(new MarkerOptions()
                        .title("phbrasil")
                        .position(phBRLatLong)
                        .snippet(phBRId)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(phBRLatLong));

            }
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }

            if (data.size() != 0) {
                Toast.makeText(MapsActivity.this, data.size() + "File is built Successfully!" + "\n", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MapsActivity.this, "File fail to build", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



