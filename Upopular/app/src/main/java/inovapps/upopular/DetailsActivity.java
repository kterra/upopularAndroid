package inovapps.upopular;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    String kind, name, street, district, city, state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        kind = intent.getStringExtra("tipo");
        name = intent.getStringExtra("nome");
        street = intent.getStringExtra("logradouro");
        district = intent.getStringExtra("bairro");
        city =  intent.getStringExtra("cidade");
        state = intent.getStringExtra("estado");

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
            streetField.setText("Logradouro: Não informado");
        }else{
            streetField.setText("Logradouro: " + street);
        }

        TextView districtField = (TextView) findViewById(R.id.district);
        if(district.isEmpty()){
            districtField.setText("Bairro: Não informado");
        }else{
            districtField.setText("Bairro: " + street);
        }

        TextView cityField = (TextView) findViewById(R.id.city);
        if(city.isEmpty()){
            cityField.setText("Cidade: Não informada");
        }else {
            cityField.setText("Cidade: " + city);
        }

        TextView stateField = (TextView) findViewById(R.id.state);
        if(state.isEmpty()){
            stateField.setText("Estado: Não informado");
        }else {
            stateField.setText("Estado: " + state);
        }

    }
}
