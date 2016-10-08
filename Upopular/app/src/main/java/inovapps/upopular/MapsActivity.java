package inovapps.upopular;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener,GoogleMap.OnCameraChangeListener, OnMapReadyCallback{

    private GoogleMap mMap;
    private HashMap<String,ArrayList<String>> upaData;
    private ArrayList<String> clickedUpaInfo;
    private boolean upaSelected;
    private DatabaseHelper dbHelper;
    private Location lastPosition;
    private Location currentPosition;


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

        MapsActivity.this.deleteDatabase("Upopular.db");
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
       // mMap.setOnCameraChangeListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
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
                    clickedUpaInfo = upaData.get(arg0.getSnippet());

                    // Getting reference to the TextView to set latitude
                    TextView title = (TextView) v.findViewById(R.id.tv_title);
                    title.setText(clickedUpaInfo.get(0));
                    title.setTextColor(getResources().getColor(R.color.orange));
                    // Getting reference to the TextView to set longitude
                    TextView subtitle1 = (TextView) v.findViewById(R.id.tv_subtitle1);
                    subtitle1.setText(clickedUpaInfo.get(1) + ", " + clickedUpaInfo.get(2) + "-" + clickedUpaInfo.get(3));

                    // Getting reference to the TextView to set longitude
                    TextView subtitle2 = (TextView) v.findViewById(R.id.tv_subtitle2);
                    subtitle2.setText(clickedUpaInfo.get(4) + ", " + clickedUpaInfo.get(5));
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
    public void onCameraChange(final CameraPosition position) {

        currentPosition.setLatitude(position.target.latitude);
        currentPosition.setLongitude(position.target.longitude);

        if(currentPosition.distanceTo(lastPosition) > 1000){
            new AccessDataBase().execute(currentPosition);
            lastPosition.setLatitude(currentPosition.getLatitude());
            lastPosition.setLongitude(currentPosition.getLongitude());
        }


    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        Intent intent = new Intent(MapsActivity.this, DetailsActivity.class);

        String kind = marker.getTitle();
        if (kind.equals("upa")) {

            intent.putExtra("tipo", "UPA");
            intent.putExtra("nome", clickedUpaInfo.get(0));
            intent.putExtra("logradouro", clickedUpaInfo.get(1) + ", " + clickedUpaInfo.get(2));
            intent.putExtra("bairro", clickedUpaInfo.get(3));
            intent.putExtra("cidade", clickedUpaInfo.get(4));
            intent.putExtra("estado", clickedUpaInfo.get(5));
            intent.putExtra("lat", clickedUpaInfo.get(6));
            intent.putExtra("long", clickedUpaInfo.get(7));
            intent.putExtra("porte", clickedUpaInfo.get(8));
            intent.putExtra("telefone", clickedUpaInfo.get(9));


        }

        startActivity(intent);

    }

    public void showList(View button) {
        Intent listIntent = new Intent(this, HealthListActivity.class);
        listIntent.putExtra("UPAs", upaData);
        startActivity(listIntent);
    }



    public class AccessDataBase extends AsyncTask<Location, String, HashMap<String,ArrayList<String>>> {


        private ProgressDialog dialog;

        public AccessDataBase() {

        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MapsActivity.this);
            dialog.setTitle("Carregando os dados");
            dialog.setMessage("Por favor, aguarde...");
            dialog.setCancelable(false);
            dialog.setIcon(android.R.drawable.ic_dialog_info);
            dialog.show();
        }

        @Override
        protected HashMap<String, ArrayList<String>> doInBackground(Location... params) {

            InputStream inputStream = getResources().openRawResource(R.raw.upa_funcionamento_georref);
            dbHelper.insertData(inputStream);

            double currentLat = params[0].getLatitude();
//            double currentCosLat = Math.cos(MathUtil.deg2rad(currentLat));
//            double currentSinLat = Math.sin(MathUtil.deg2rad(currentLat));

            double currentLng = params[0].getLongitude();
//            double currentCosLng = Math.cos(MathUtil.deg2rad(currentLng));
//            double currentSinLng = Math.sin(MathUtil.deg2rad(currentLng));

           // double cos_allowed_distance = Math.cos(20.0 / 6371);

            upaData = dbHelper.getUPAMainData(currentLat, currentLng);

            return upaData;
        }

        protected void onPostExecute(HashMap<String, ArrayList<String>> data) {

            upaSelected = true;
            for (Entry<String, ArrayList<String>> entry : data.entrySet()) {

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

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (data.size() != 0) {
                Toast.makeText(MapsActivity.this, data.size() + "File is built Successfully!" + "\n", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MapsActivity.this, "File fail to build", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



