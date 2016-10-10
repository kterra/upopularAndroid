package inovapps.upopular;

/**
 * Created by hallpaz on 09/10/2016.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentTab2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragmenttab, container, false);
        //TextView textView = (TextView) getView().findViewById(R.id.name_textview);
        //textView.setText("Do tipo Fragmento 2");
        return view;
    }

}
