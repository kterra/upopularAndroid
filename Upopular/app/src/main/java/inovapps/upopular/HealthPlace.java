package inovapps.upopular;

/**
 * Created by hallpaz on 13/03/2016.
 */
public class HealthPlace {

    //Might be a UPA or Pharmacy
    private String name;
    private MyAddress address;

    public HealthPlace(String name, MyAddress address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MyAddress getAddress() {
        return address;
    }

    public void setAddress(MyAddress address) {
        this.address = address;
    }
}
