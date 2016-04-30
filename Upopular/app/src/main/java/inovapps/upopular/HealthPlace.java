package inovapps.upopular;

/**
 * Created by hallpaz on 13/03/2016.
 */
public class HealthPlace {

    //Might be a UPA or Pharmacy
    private String name;
    //private MyAddress address;
    private String address;
    private String phoneNumber;
    //private int port = -1; //pharmacies have port 0. UPAs have port > 0

    public HealthPlace(String name, String address, String phoneNumber) {
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

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
