package inovapps.upopular;


import com.google.android.gms.maps.model.LatLng;

/**
 * Created by hallpaz on 12/02/2016.
 */
public class MyAddress {
    private String state;
    private String city;
    private String district;
    private String street;
    private String complement;
    private LatLng location;

    public MyAddress(){

    }

    public MyAddress(String street, String complement, String district, String city, String state) {

        this.street = street;
        this.complement = complement;
        this.district = district;
        this.city = city;
        this.state = state;
    }



    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getStreet() {
        return street;
    }

    public String getComplement() {
        return complement;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }


    @Override
    public String toString(){
        String representation = "";
        boolean shouldPutComma = false;
        if (street != "" && street != null){
            representation += street;
            shouldPutComma = true;
        }
        if (complement != null && !complement.isEmpty()){
            if(shouldPutComma)
                representation += ", ";
            representation += complement;
            shouldPutComma = true;
        }

        if (district != null && !district.isEmpty()){
            if(shouldPutComma)
                representation += ", ";
            representation += district;
            shouldPutComma = true;
        }
        if (city != null && !city.isEmpty()) {
            if (shouldPutComma)
                representation += ", ";
            representation += city;
            shouldPutComma = true;
        }
        if (state != null && !state.isEmpty()) {
            representation += " (" + state + ")";
        }
        return representation;
    }
}
