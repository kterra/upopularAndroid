package inovapps.upopular;

/**
 * Created by hallpaz on 14/03/2016.
 * Copied from geofire-java (MIT-License): https://github.com/firebase/geofire-java/blob/master/src/main/java/com/firebase/geofire/util/Constants.java
 */
public class Constants {
    // Length of a degree latitude at the equator
    public static final double METERS_PER_DEGREE_LATITUDE = 110574;
    // The equatorial circumference of the earth in meters
    public static final double EARTH_MERIDIONAL_CIRCUMFERENCE = 40007860;
    // The equatorial radius of the earth in meters
    public static final double EARTH_EQ_RADIUS = 6378137;
    // The meridional radius of the earth in meters
    public static final double EARTH_POLAR_RADIUS = 6357852.3;

    /* The following value assumes a polar radius of
     * r_p = 6356752.3
     * and an equatorial radius of
     * r_e = 6378137
     * The value is calculated as e2 == (r_e^2 - r_p^2)/(r_e^2)
     * Use exact value to avoid rounding errors
     */
    public static final double EARTH_E2 =  0.00669447819799;

    // Cutoff for floating point calculations
    public static final double EPSILON = 1e-12;

    public static  final String UPA = "UPA";
    public static  final String PH = "PH";
    public static  final int UPA_DATA = 0;
    public static  final int PH_DATA = 1;

    public static final String ID = "gid";
    public static final String ZIPCODE = "cep";
    public static final String PHONE = "telefone";
    public static final String PORT = "porte";
    public static final String NAME = "nome_fantasia";
    public static final String STREET = "logradouro";
    public static final String NUMBER = "numero";
    public static final String DISTRICT = "bairro";
    public static final String CITY = "cidade";
    public static final String STATE = "estado";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String KIND = "tipo";

    public static final int NAME_INDEX = 0;
    public static final int STREET_INDEX = 1;
    public static final int NUMBER_INDEX = 2;
    public static final int DISTRICT_INDEX = 3;
    public static final int ZIPCODE_INDEX = 4;
    public static final int CITY_INDEX = 5;
    public static final int STATE_INDEX = 6;
    public static final int LAT_INDEX = 7;
    public static final int LONG_INDEX = 8;
    public static final int PORT_INDEX = 9;
    public static final int PHONE_INDEX = 10;
}
