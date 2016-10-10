package inovapps.upopular;

/**
 * Created by hallpaz on 09/10/2016.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    // Tab Titles
    private String tabtitles[] = new String[] { "UPAs", "Farmácias"};
    private UPAFragmentList upaFragment;
    private PHBrasilFragmentList phFragment;


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void updateUPAs(Map<String, List<String>> data, LatLng latLng){
        upaFragment.setData(data, latLng);
    }

    public void updatePHs(Map<String, List<String>> data, LatLng latLng){
        phFragment.setData(data, latLng);
    }

    @Override
    public int getCount() {
        return tabtitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            // Open HealthPlaceFragmentList.java
            case 0:
                upaFragment = new UPAFragmentList();
                return upaFragment;

            // Open FragmentTab2.java
            case 1:
                phFragment = new PHBrasilFragmentList();
                return phFragment;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}
