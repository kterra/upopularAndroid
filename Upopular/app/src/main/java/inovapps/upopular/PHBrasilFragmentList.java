package inovapps.upopular;

import android.os.Bundle;
import android.view.View;

/**
 * Created by hallpaz on 10/10/2016.
 */
public class PHBrasilFragmentList extends HealthPlaceFragmentList {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData(myDataHolder.uploadPHBrasilData(), myDataHolder.uploadLatLng());
    }
}
