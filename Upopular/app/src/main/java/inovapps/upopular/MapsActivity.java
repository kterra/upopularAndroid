package inovapps.upopular;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.*;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;

public class MapsActivity extends FragmentActivity implements OnInfoWindowClickListener, OnMapReadyCallback, OnCameraMoveStartedListener, OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{

    private GoogleMap mMap;
    private HashMap<String,ArrayList<String>> upaData;
    private HashMap<String,ArrayList<String>> phBRData;
    private ArrayList<HashMap<String,ArrayList<String>>> data;
    private ArrayList<String> clickedInfo;

    private boolean userGestured;
    private DatabaseHelper dbHelper;
    private Location currentLocation;
    private GoogleApiClient googleApiClient;


    private boolean upaSelected = true;
    private boolean phSelected = true;

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int LOCATION_REQUEST_INTERVAL = 30000;
    private static final int LOCATION_REQUEST_FASTEST_INTERVAL = 5000;
    private static final double BRASILIA_LATITUDE = -15.7217174;
    private static final double BRASILIA_LONGITUDE = -48.0783226;
    private static final float MAP_MIN_ZOOM = 10.0f;
    private static final float MAP_MAX_ZOOM = 12.0f;
    private final String TAG = "MAP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        currentLocation = new Location("");
        currentLocation.setLatitude(BRASILIA_LATITUDE);
        currentLocation.setLongitude(BRASILIA_LONGITUDE);

        dbHelper = new DatabaseHelper(MapsActivity.this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        View mapView = (mapFragment.getFragmentManager().findFragmentById(R.id.map)).getView();
        View btnMyLocation = ((View) mapView.findViewById(1).getParent()).findViewById(2);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnMyLocation.getLayoutParams(); // size of button in dp
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.setMargins(0, 0, 20, 350);
        btnMyLocation.setLayoutParams(params);


        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(this, "Permissão de Localização!", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
            Log.i(MapsActivity.class.getSimpleName(), "pediu permissao!");
        }else{
            gpsEnabledChecker();
        }
    }

    public void gpsEnabledChecker()
    {
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        startLocationUpdates(locationRequest);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    protected void startLocationUpdates(LocationRequest mLocationRequest) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, mLocationRequest, MapsActivity.this);
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    public void onLocationChanged(Location location) {
        currentLocation = location;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), MAP_MAX_ZOOM));
        new AccessDataBase().execute(currentLocation);
        stopLocationUpdates();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
    // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        gpsEnabledChecker();
                        break;
                    case Activity.RESULT_CANCELED:
                        setDefaultLocation();
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gpsEnabledChecker();
                } else{
                    setDefaultLocation();
                }

                break;
        }
    }

    public void setDefaultLocation(){
        currentLocation.setLatitude(BRASILIA_LATITUDE);
        currentLocation.setLongitude(BRASILIA_LONGITUDE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), MAP_MIN_ZOOM));
        new AccessDataBase().execute(currentLocation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("MAIN", "ON RESUME");
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(MapsActivity.class.getSimpleName(), "Connected to Google Play Services!");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(MapsActivity.class.getSimpleName(), "Can't connect to Google Play Services!");
        setDefaultLocation();
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
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker selectedMarker) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker selectedMarker) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.info_window, null);

                // Getting the position from the marker
                String kind = selectedMarker.getTitle();
                if (kind.equals(Constants.UPA)) {
                    clickedInfo = upaData.get(selectedMarker.getSnippet());

                    TextView subtitle1 = (TextView) v.findViewById(R.id.tv_subtitle1);
                    subtitle1.setText(clickedInfo.get(Constants.STREET_INDEX) + ", " + clickedInfo.get(Constants.NUMBER_INDEX) + "-" + clickedInfo.get(Constants.DISTRICT_INDEX));

                }
                if (kind.equals(Constants.PH)) {
                    clickedInfo = phBRData.get(selectedMarker.getSnippet());

                    TextView subtitle1 = (TextView) v.findViewById(R.id.tv_subtitle1);
                    subtitle1.setText(clickedInfo.get(Constants.STREET_INDEX));

                }

                TextView title = (TextView) v.findViewById(R.id.tv_title);
                title.setText(clickedInfo.get(Constants.NAME_INDEX));
                title.setTextColor(ContextCompat.getColor(MapsActivity.this, R.color.orange));

                TextView subtitle2 = (TextView) v.findViewById(R.id.tv_subtitle2);
                subtitle2.setText(clickedInfo.get(Constants.CITY_INDEX) + ", " + clickedInfo.get(Constants.STATE_INDEX));
                // Returning the view containing InfoWindow contents
                return v;

            }

        });
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), MAP_MIN_ZOOM));
        new AccessDataBase().execute(currentLocation);
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

            currentLocation.setLatitude(mMap.getCameraPosition().target.latitude);
            currentLocation.setLongitude(mMap.getCameraPosition().target.longitude);

            new AccessDataBase().execute(currentLocation);


            userGestured = false;
        }
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

        Intent intent = new Intent(MapsActivity.this, DetailsActivity.class);

        String kind = marker.getTitle();
        if (kind.equals(Constants.UPA)) {

            intent.putExtra(Constants.KIND, Constants.UPA);
            intent.putExtra(Constants.STREET, clickedInfo.get(Constants.STREET_INDEX) + ", " + clickedInfo.get(Constants.NUMBER_INDEX));

        }

        if (kind.equals(Constants.PH)) {

            intent.putExtra(Constants.KIND,Constants.PH);
            intent.putExtra(Constants.STREET, clickedInfo.get(Constants.STREET_INDEX));

        }
        intent.putExtra(Constants.NAME, clickedInfo.get(Constants.NAME_INDEX));
        intent.putExtra(Constants.DISTRICT, clickedInfo.get(Constants.DISTRICT_INDEX));
        intent.putExtra(Constants.ZIPCODE, clickedInfo.get(Constants.ZIPCODE_INDEX));
        intent.putExtra(Constants.CITY, clickedInfo.get(Constants.CITY_INDEX));
        intent.putExtra(Constants.STATE, clickedInfo.get(Constants.STATE_INDEX));
        intent.putExtra(Constants.LATITUDE, clickedInfo.get(Constants.LAT_INDEX));
        intent.putExtra(Constants.LONGITUDE, clickedInfo.get(Constants.LONG_INDEX));
        intent.putExtra(Constants.PORT, clickedInfo.get(Constants.PORT_INDEX));
        intent.putExtra(Constants.PHONE, clickedInfo.get(Constants.PHONE_INDEX));

        startActivity(intent);

    }

    public void showList(View button) {
        Intent listIntent = new Intent(this, HealthListActivity.class);
        listIntent.putExtra(Constants.UPA, upaData);
        listIntent.putExtra(Constants.PH, phBRData);

        listIntent.putExtra(Constants.LATITUDE, currentLocation.getLatitude());
        listIntent.putExtra(Constants.LONGITUDE, currentLocation.getLongitude());

        startActivity(listIntent);
    }

    public void ToggleUPA(View v){
        Button btn = (Button) v;
        if (upaSelected){
            mMap.clear();
            if(phSelected){
                drawMarkers(Constants.PH_DATA);
            }
            btn.setTextColor(Color.GRAY);
            btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_hospital_unselected_24dp, 0, 0, 0);
            upaSelected = false;
        }else{
            upaSelected = true;
            drawMarkers(Constants.UPA_DATA);
            btn.setTextColor(ContextCompat.getColor(this, R.color.orange));
            btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_hospital_24dp, 0, 0, 0);
        }
    }

    public void TogglePH(View v){
        Button btn = (Button) v;
        if (phSelected){
            mMap.clear();
            if(upaSelected){
                drawMarkers(Constants.UPA_DATA);
            }
            btn.setTextColor(Color.GRAY);
            btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_pharmacy_unselected_24dp, 0, 0, 0);
            phSelected = false;
        }else{
            phSelected = true;
            drawMarkers(Constants.PH_DATA);
            btn.setTextColor(ContextCompat.getColor(this, R.color.orange));
            btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_pharmacy_24dp, 0, 0, 0);
        }
    }

    public void drawMarkers(int type) {
        String markerTitle = "";
        float color;
        if (type == Constants.UPA_DATA){
            markerTitle = Constants.UPA;
            color = BitmapDescriptorFactory.HUE_BLUE;
        } else if (type == Constants.PH_DATA) {
            markerTitle = Constants.PH;
            color = BitmapDescriptorFactory.HUE_ORANGE;
        } else {
            Log.e(TAG, "Drawing Invalid type of Data");
            return;
        }
        for (Entry<String, ArrayList<String>> entry : data.get(type).entrySet()) {
            String placeID = entry.getKey();
            ArrayList<String> placeData = entry.getValue();

            Float placeLat = Float.valueOf(placeData.get(7));
            Float placeLong = Float.valueOf(placeData.get(8));

            LatLng placeLatLong = new LatLng(placeLat, placeLong);
            mMap.addMarker(new MarkerOptions()
                    .title(markerTitle)
                    .position(placeLatLong)
                    .snippet(placeID)
                    .icon(BitmapDescriptorFactory.defaultMarker(color)));
        }
    }

    public class AccessDataBase extends AsyncTask<Location, String, ArrayList<HashMap<String, ArrayList<String>>>>{
        public AccessDataBase() {}
        @Override
        protected void onPreExecute() {}
        @Override
        protected ArrayList<HashMap<String, ArrayList<String>>> doInBackground(Location... params) {
            double currentLat = params[0].getLatitude();
            double currentLng = params[0].getLongitude();

            upaData = dbHelper.getUPAMainData(currentLat, currentLng);
            phBRData = dbHelper.getPHMainData(currentLat, currentLng);
            data = new ArrayList<>();
            data.add(upaData);
            data.add(phBRData);
            return data;
        }

        protected void onPostExecute(ArrayList<HashMap<String, ArrayList<String>>> data) {
            mMap.clear();
            if(upaSelected){
                drawMarkers(Constants.UPA_DATA);
            }
            if(phSelected){
                drawMarkers(Constants.PH_DATA);
            }
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
            if (data.size() != 0) {
                //Toast.makeText(MapsActivity.this, data.size() + "File is built Successfully!" + "\n", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MapsActivity.this, "File fail to build", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

/*
public void drawUPA(){
        for (Entry<String, ArrayList<String>> entry : data.get(Constants.UPA_DATA).entrySet()) {
            String upaId = entry.getKey();
            ArrayList<String> singleUPAData = entry.getValue();

            Float upaLat = Float.valueOf(singleUPAData.get(7));
            Float upaLong = Float.valueOf(singleUPAData.get(8));

            LatLng upaLatLong = new LatLng(upaLat, upaLong);
            mMap.addMarker(new MarkerOptions()
                    .title(Constants.UPA)
                    .position(upaLatLong)
                    .snippet(upaId)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    public void drawPH(){
        for (Entry<String, ArrayList<String>> entry : data.get(Constants.PH_DATA).entrySet()) {
            String phBRId = entry.getKey();
            ArrayList<String> singlePhBRData = entry.getValue();

            Float upaLat = Float.valueOf(singlePhBRData.get(7));
            Float upaLong = Float.valueOf(singlePhBRData.get(8));

            LatLng phBRLatLong = new LatLng(upaLat, upaLong);
            mMap.addMarker(new MarkerOptions()
                    .title(Constants.PH)
                    .position(phBRLatLong)
                    .snippet(phBRId)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        }
    }

 */



