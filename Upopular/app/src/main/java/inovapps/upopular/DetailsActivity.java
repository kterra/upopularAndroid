package inovapps.upopular;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    String kind, name, street, district, city, state, slatitude, slongitude, porte, telefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Intent intent = getIntent();
        kind = intent.getStringExtra("tipo");
        name = intent.getStringExtra("nome");
        street = intent.getStringExtra("logradouro");
        district = intent.getStringExtra("bairro");
        city =  intent.getStringExtra("cidade");
        state = intent.getStringExtra("estado");
        slatitude = intent.getStringExtra("lat");
        slongitude = intent.getStringExtra("long");
        porte = intent.getStringExtra("porte");
        telefone = intent.getStringExtra("telefone");

        TextView nameField = (TextView) findViewById(R.id.title);
        if(name.isEmpty()){
            if(kind.equals("UPA")){
                nameField.setText("UPA");
            }

        }else{
            nameField.setText(name);
        }

        TextView streetField = (TextView) findViewById(R.id.street);
        if(street.isEmpty()){
            streetField.setText("Não informado");
        }else{
            streetField.setText(street);
        }

        TextView districtField = (TextView) findViewById(R.id.district);
        if(district == null || district.isEmpty()){
            districtField.setText("Não informado");
        }else{
            districtField.setText(district);
        }

        TextView cityField = (TextView) findViewById(R.id.city);
        if(city == null || city.isEmpty()){
            cityField.setText("Não informada");
        }else {
            cityField.setText(city);
        }

        TextView stateField = (TextView) findViewById(R.id.state);
        if(state == null || state.isEmpty()){
            stateField.setText("Não informado");
        }else {
            stateField.setText(state);
        }

        TextView porteField = (TextView) findViewById(R.id.porte);
        if(porte.isEmpty()){
            porteField.setText("Não informado");
        }else {
            porteField.setText(porte);
        }

        Button callButton = (Button)findViewById(R.id.callbutton);
        callButton.setText(telefone );

    }

    public void callPlace(View button){

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telefone));
        startActivity(intent);

    }

    public void getRoute(View button){

        Float latitude = Float.valueOf(slatitude);
        Float longitude= Float.valueOf(slongitude);
        String uriBegin = "geo:0,0";
        String query = latitude + "," + longitude + "(" + name + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
