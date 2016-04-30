package inovapps.upopular;

/**
 * Created by hallpaz on 13/03/2016.
 */
public class HealthPlace {

    //Might be a UPA or Pharmacy
    private String name;
    private MyAddress address;
    private String phoneNumber;
    private int port = -1; //pharmacies have port 0. UPAs have port > 0

    public HealthPlace(String name, MyAddress address, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullAddress() {
        return address.toString();
    }

    public String getStreet() {
        return address.getStreet();
    }

    public String getComplement() {
        return address.getComplement();
    }

    public String getDistrict() {
        return address.getDistrict();
    }

    public String getCity() {
        return address.getCity();
    }

    public String getState() {
        return address.getState();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(MyAddress address) {
        this.address = address;
    }

    public String getType() {
        if (port > 0) {
            return "UPA";
        }
        return "Farmácia";
    }
}
