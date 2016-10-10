package inovapps.upopular;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    String kind, name, street, district, city, state, latitude, longitude, port, phone;

    private static final String UNKNOWN_DATA = "Não informado";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Intent intent = getIntent();
        kind = intent.getStringExtra(Constants.KIND);
        name = intent.getStringExtra(Constants.NAME);
        street = intent.getStringExtra(Constants.STREET);
        district = intent.getStringExtra(Constants.DISTRICT);
        city =  intent.getStringExtra(Constants.CITY);
        state = intent.getStringExtra(Constants.STATE);
        latitude = intent.getStringExtra(Constants.LATITUDE);
        longitude = intent.getStringExtra(Constants.LONGITUDE);
        port = intent.getStringExtra(Constants.PORT);
        phone = intent.getStringExtra(Constants.PHONE);

        TextView nameField = (TextView) findViewById(R.id.title);
        if(name == null || name.isEmpty()){
            if(kind.equals(Constants.UPA)){
                nameField.setText(Constants.UPA);
            }
            if(kind.equals(Constants.PH)){
                nameField.setText(Constants.UPA);
            }

        }else{
            nameField.setText(name);
        }

        TextView streetField = (TextView) findViewById(R.id.street);
        if(street == null || street.isEmpty()){
            streetField.setText(UNKNOWN_DATA);
        }else{
            streetField.setText(street);
        }

        TextView districtField = (TextView) findViewById(R.id.district);

        TableRow districtTableRow = (TableRow) findViewById(R.id.tableRow2);
        if(district == null || district.isEmpty()){
            if (kind.equals(Constants.PH)){
                districtTableRow.setVisibility(View.GONE);

            }else{
                districtField.setText(UNKNOWN_DATA);
            }

        }else{
            districtField.setText(district);
        }

        TextView cityField = (TextView) findViewById(R.id.city);
        if(city == null || city.isEmpty()){
            cityField.setText(UNKNOWN_DATA);
        }else {
            cityField.setText(city);
        }

        TextView stateField = (TextView) findViewById(R.id.state);
        if(state == null || state.isEmpty()){
            stateField.setText(UNKNOWN_DATA);
        }else {
            stateField.setText(state);
        }

        TextView porteField = (TextView) findViewById(R.id.porte);
        LinearLayoutCompat porteLayout = (LinearLayoutCompat) findViewById(R.id.porte_layout);
        if(port == null || port.isEmpty()){
            if (kind.equals(Constants.PH)){
                porteLayout.setVisibility(View.GONE);
            }else{
                porteField.setText(UNKNOWN_DATA);
            }

        }else {
            porteField.setText(port);
        }

        Button callButton = (Button)findViewById(R.id.callbutton);
        if(phone == null || phone.isEmpty()) {
            callButton.setVisibility(View.GONE);
        }
        else{
            callButton.setVisibility(View.VISIBLE);
            callButton.setText(phone);
        }


    }

    public void callPlace(View button){

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        startActivity(intent);

    }

    public void getRoute(View button){


//        String uriBegin = "geo:0,0";
//        String query = latitude + "," + longitude + "(" + name + ")";
//        String encodedQuery = Uri.encode(query);
//        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16" + "&mode=walk";
        String uriString = "https://maps.google.com/maps?f=d&daddr="+latitude + "," + longitude +"&mode=walking";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void getInfo(View button){
        Intent intent = new Intent(DetailsActivity.this, InfoActivity.class);
        startActivity(intent);
    }
}
