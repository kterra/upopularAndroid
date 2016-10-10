package inovapps.upopular;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kizzyterra on 4/29/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public final static String TAG = "DBHELPER";

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Upopular.db";



    private static final String VIRTUAL_TABLE_COLUMN_ID = "rowid";
    private static final String UPAS_TABLE_NAME = "upas";
    private static final String UPAS_VIRTUAL_TABLE_NAME = "fts_upas";


    private static final String UPAS_TABLE_CREATE = "create table " + UPAS_TABLE_NAME + " " +
            "("
            + Constants.ID + " integer, "
            + Constants.ZIPCODE + " text, "
            + Constants.STATE + " text, "
            + Constants.CITY + " text, "
            + Constants.NAME + " text, "
            + Constants.DISTRICT + " text, "
            + Constants.NUMBER + " text, "
            + Constants.STREET + " text, "
            + Constants.PHONE + " text, "
            + Constants.PORT + " text, "
            + Constants.LATITUDE + " double, "
            + Constants.LONGITUDE + " double) ";

    private static final String UPAS_VIRTUAL_CREATE = "create virtual table " + UPAS_VIRTUAL_TABLE_NAME
            + " using fts4(content=" + UPAS_TABLE_NAME + ", "
            + Constants.ZIPCODE + ", "
            + Constants.STATE + ", "
            + Constants.CITY + ", "
            + Constants.NAME + ", "
            + Constants.DISTRICT + ", "
            + Constants.NUMBER + ", "
            + Constants.STREET + ", "
            + Constants.PHONE + ", "
            + Constants.PORT + ", "
            + Constants.LATITUDE + ", "
            + Constants.LONGITUDE + ") ";
    private static final String VIRTUAL_UPAS_TABLE_REBUILD = "INSERT INTO "
            + UPAS_VIRTUAL_TABLE_NAME + " (" + UPAS_VIRTUAL_TABLE_NAME + ") VALUES('rebuild')";



    private static final String PHBRASIL_NAME = "FARMÁCIA POPULAR DO BRASIL";
    private static final String PHBRASIL_NUMBER = "";
    private static final String PHBRASIL_DISTRICT = "";
    private static final String PHBRASIL_PORTE = "";
    private static final String PHBRASIL_PHONE = "";

    private static final String PHBRASIL_TABLE_NAME = "farmacia_popular_brasil";
    private static final String PHBRASIL_VIRTUAL_TABLE_NAME = "fts_phbrasil";
    private static final String PHBRASIL_TABLE_CREATE = "create table " + PHBRASIL_TABLE_NAME + " " +
            "("
            + Constants.ID + " integer, "
            + Constants.ZIPCODE + " text, "
            + Constants.STATE + " text, "
            + Constants.CITY + " text, "
            + Constants.STREET + " text, "
            + Constants.LATITUDE + " double, "
            + Constants.LONGITUDE + " double) ";
    private static final String PHBRASIL_VIRTUAL_CREATE = "create virtual table " + PHBRASIL_VIRTUAL_TABLE_NAME +
            " using fts4(content=" + PHBRASIL_TABLE_NAME + ", "
            + Constants.ZIPCODE + ", "
            + Constants.STATE + ", "
            + Constants.CITY + ", "
            + Constants.STREET + ", "
            + Constants.LATITUDE + ", "
            + Constants.LONGITUDE + ") ";

    private static final String VIRTUAL_PHBRASIL_TABLE_REBUILD = "INSERT INTO "
            + PHBRASIL_VIRTUAL_TABLE_NAME + " (" + PHBRASIL_VIRTUAL_TABLE_NAME + ") VALUES('rebuild')";

    private static final int UPA_FILE_ID_INDEX = 0;
    private static final int UPA_FILE_ZIPCODE_INDEX = 1;
    private static final int UPA_FILE_STATE_INDEX = 2;
    private static final int UPA_FILE_CITY_INDEX = 3;
    private static final int UPA_FILE_NAME_INDEX = 4;
    private static final int UPA_FILE_DISTRICT_INDEX = 5;
    private static final int UPA_FILE_NUMBER_INDEX = 6;
    private static final int UPA_FILE_STREET_INDEX = 7;
    private static final int UPA_FILE_PHONE_INDEX = 8;
    private static final int UPA_FILE_PORT_INDEX = 12;
    private static final int UPA_FILE_LATIDTUDE_INDEX = 13;
    private static final int UPA_FILE_LONGITUDE_INDEX = 14;

    private static final int PHBRASIL_FILE_ID_INDEX = 0;
    private static final int PHBRASIL_FILE_LATIDTUDE_INDEX = 1;
    private static final int PHBRASIL_FILE_LONGITUDE_INDEX = 2;
    private static final int PHBRASIL_FILE_STREET_INDEX = 5;
    private static final int PHBRASIL_FILE_ZIPCODE_INDEX = 6;
    private static final int PHBRASIL_FILE_STATE_INDEX = 7;
    private static final int PHBRASIL_FILE_CITY_INDEX = 8;





    public  InputStream inputUPAStream;
    public  InputStream inputPHStream;


    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.e(TAG, "before input");
        inputUPAStream = context.getResources().openRawResource(R.raw.upa_funcionamento_georref);
        inputPHStream = context.getResources().openRawResource(R.raw.farmacia_popular_brasil_original);

    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UPAS_TABLE_CREATE);
        Log.e(TAG, "Criando a tabela: " + UPAS_VIRTUAL_TABLE_NAME);
        db.execSQL(UPAS_VIRTUAL_CREATE);
        insertUPAData(inputUPAStream, db);

        db.execSQL(PHBRASIL_TABLE_CREATE);
        Log.e(TAG, "Criando a tabela: " + PHBRASIL_VIRTUAL_TABLE_NAME);
        db.execSQL(PHBRASIL_VIRTUAL_CREATE);
        insertPHData(inputPHStream, db);
    }

    // TODO: Review this for our case
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over

        db.execSQL("DROP TABLE IF EXISTS "+ UPAS_TABLE_NAME);
        onCreate(db);
    }

    // TODO: review this for our case
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insertUPAData(InputStream inStream, SQLiteDatabase db) {

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
       // SQLiteDatabase db = this.getWritableDatabase();
        String line = "";
        db.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(",");
                if(!(columns[0].equals(Constants.ID))){
                    ContentValues cv = new ContentValues();
                    cv.put(Constants.ID, Integer.valueOf(columns[UPA_FILE_ID_INDEX].trim()));
                    cv.put(Constants.ZIPCODE, columns[UPA_FILE_ZIPCODE_INDEX].trim());
                    cv.put(Constants.STATE, columns[UPA_FILE_STATE_INDEX].trim());
                    cv.put(Constants.CITY, columns[UPA_FILE_CITY_INDEX].trim());
                    cv.put(Constants.NAME, columns[UPA_FILE_NAME_INDEX].trim());
                    cv.put(Constants.DISTRICT, columns[UPA_FILE_DISTRICT_INDEX].trim());
                    cv.put(Constants.NUMBER, columns[UPA_FILE_NUMBER_INDEX].trim());
                    cv.put(Constants.STREET, columns[UPA_FILE_STREET_INDEX].trim());
                    cv.put(Constants.PHONE, columns[UPA_FILE_PHONE_INDEX].trim());
                    cv.put(Constants.PORT, columns[UPA_FILE_PORT_INDEX].trim());

                    double latitude = Double.valueOf(columns[UPA_FILE_LATIDTUDE_INDEX].trim());
                    double longitude = Double.valueOf(columns[UPA_FILE_LONGITUDE_INDEX].trim());

                    cv.put(Constants.LATITUDE, latitude);
                    cv.put(Constants.LONGITUDE, longitude);

//                    HashMap<String, Double> preCalculatedValues = preCalculateDistanceValues(latitude, longitude);
//
//                    cv.put(UPAS_COLUMN_COSLAT, preCalculatedValues.get(UPAS_COLUMN_COSLAT));
//                    cv.put(UPAS_COLUMN_SINLAT, preCalculatedValues.get(UPAS_COLUMN_SINLAT));
//                    cv.put(UPAS_COLUMN_COSLONG, preCalculatedValues.get(UPAS_COLUMN_COSLONG));
//                    cv.put(UPAS_COLUMN_SINLONG, preCalculatedValues.get(UPAS_COLUMN_SINLONG));

                    db.insert(UPAS_TABLE_NAME, null, cv);
                }

            }
            //Updates all values in the FTS_UPA table
            Log.d(TAG, "Will rebuild UPA Virtual table");
            db.execSQL(VIRTUAL_UPAS_TABLE_REBUILD);
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();

    }

    public void insertPHData(InputStream inStream, SQLiteDatabase db) {

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        //SQLiteDatabase db = this.getWritableDatabase();
        String line;
        db.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(",");
                if(!(columns[0].equals(Constants.ID))){
                    ContentValues cv = new ContentValues();
                    cv.put(Constants.ID, Integer.valueOf(columns[PHBRASIL_FILE_ID_INDEX].trim().replace("\"", "")));
                    cv.put(Constants.STREET, columns[PHBRASIL_FILE_STREET_INDEX].trim().replace("\"", ""));
                    cv.put(Constants.ZIPCODE, columns[PHBRASIL_FILE_ZIPCODE_INDEX].trim().replace("\"", ""));
                    cv.put(Constants.STATE, columns[PHBRASIL_FILE_STATE_INDEX].trim().replace("\"", ""));
                    cv.put(Constants.CITY, columns[PHBRASIL_FILE_CITY_INDEX].trim().replace("\"", ""));


                    double latitude = Double.valueOf(columns[PHBRASIL_FILE_LATIDTUDE_INDEX].trim().replace("\"", ""));
                    double longitude = Double.valueOf(columns[PHBRASIL_FILE_LONGITUDE_INDEX].trim().replace("\"", ""));

                    cv.put(Constants.LATITUDE, latitude);
                    cv.put(Constants.LONGITUDE, longitude);

//                    HashMap<String, Double> preCalculatedValues = preCalculateDistanceValues(latitude, longitude);
//
//                    cv.put(UPAS_COLUMN_COSLAT, preCalculatedValues.get(UPAS_COLUMN_COSLAT));
//                    cv.put(UPAS_COLUMN_SINLAT, preCalculatedValues.get(UPAS_COLUMN_SINLAT));
//                    cv.put(UPAS_COLUMN_COSLONG, preCalculatedValues.get(UPAS_COLUMN_COSLONG));
//                    cv.put(UPAS_COLUMN_SINLONG, preCalculatedValues.get(UPAS_COLUMN_SINLONG));

                    db.insert(PHBRASIL_TABLE_NAME, null, cv);
                }
            }
            Log.d(TAG, "Will rebuild UPA Virtual table");
            db.execSQL(VIRTUAL_PHBRASIL_TABLE_REBUILD);
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();

    }


    public HashMap<String,ArrayList<String>> getUPAMainData(double CUR_lat, double CUR_lng)
    {
        HashMap<String,ArrayList<String>> upaData = new HashMap<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select "+ Constants.ID + ","+ Constants.ZIPCODE + "," + Constants.NAME + "," +
                Constants.STREET + "," + Constants.NUMBER + "," +  Constants.DISTRICT + "," +
                Constants.CITY + "," + Constants.STATE + "," + Constants.LATITUDE + "," + Constants.LONGITUDE + "," +
                Constants.PORT + "," + Constants.PHONE + " from "+ UPAS_TABLE_NAME +
                " ORDER BY abs("+ Constants.LATITUDE +" - " + CUR_lat + ") + abs("+ Constants.LONGITUDE +" - "+ CUR_lng + ") LIMIT 20;", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            ArrayList<String> singleUPA = new ArrayList<>();

            singleUPA.add(res.getString(res.getColumnIndex(Constants.NAME)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.STREET)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.NUMBER)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.DISTRICT)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.ZIPCODE)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.CITY)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.STATE)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.LATITUDE)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.LONGITUDE)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.PORT)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.PHONE)));

            upaData.put(res.getString(res.getColumnIndex(Constants.ID)), singleUPA);
            res.moveToNext();
        }


        return upaData;
    }

    public HashMap<String,ArrayList<String>> getPHMainData(double CUR_lat, double CUR_lng)
    {
        HashMap<String,ArrayList<String>> phBrasilData = new HashMap<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select "+ Constants.ID + ","+ Constants.STREET + "," + Constants.ZIPCODE + "," +
                Constants.CITY + "," + Constants.STATE + "," + Constants.LATITUDE + "," + Constants.LONGITUDE + " from "+ PHBRASIL_TABLE_NAME +
                " ORDER BY abs("+ Constants.LATITUDE +" - " + CUR_lat + ") + abs("+ Constants.LONGITUDE +" - "+ CUR_lng + ") LIMIT 20;", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            ArrayList<String> singlePHBRASIL = new ArrayList<>();
            singlePHBRASIL.add(PHBRASIL_NAME);
            singlePHBRASIL.add(res.getString(res.getColumnIndex(Constants.STREET)));
            singlePHBRASIL.add(PHBRASIL_NUMBER);
            singlePHBRASIL.add(PHBRASIL_DISTRICT);
            singlePHBRASIL.add(res.getString(res.getColumnIndex(Constants.ZIPCODE)));
            singlePHBRASIL.add(res.getString(res.getColumnIndex(Constants.CITY)));
            singlePHBRASIL.add(res.getString(res.getColumnIndex(Constants.STATE)));
            singlePHBRASIL.add(res.getString(res.getColumnIndex(Constants.LATITUDE)));
            singlePHBRASIL.add(res.getString(res.getColumnIndex(Constants.LONGITUDE)));
            singlePHBRASIL.add(PHBRASIL_PORTE);
            singlePHBRASIL.add(PHBRASIL_PHONE);


            phBrasilData.put(res.getString(res.getColumnIndex(Constants.ID)), singlePHBRASIL);
            res.moveToNext();
        }
        return phBrasilData;
    }

    public Map<String, List<String>> getUpaByQuery(String query, LatLng currentPos){

        Map<String,List<String>> upaData = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select "+ Constants.ID + ","+ Constants.ZIPCODE + "," + Constants.NAME + "," +
                Constants.STREET + "," + Constants.NUMBER + "," +  Constants.DISTRICT + "," +
                Constants.CITY + "," + Constants.STATE + "," + Constants.LATITUDE + "," + Constants.LONGITUDE + "," +
                Constants.PORT + "," + Constants.PHONE + " from "+ UPAS_VIRTUAL_TABLE_NAME +
                " ORDER BY abs("+ Constants.LATITUDE +" - " + currentPos.latitude + ") + abs("+ Constants.LONGITUDE +" - "+ currentPos.longitude + ") LIMIT 20;", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            ArrayList<String> singleUPA = new ArrayList<>();
            singleUPA.add(res.getString(res.getColumnIndex(Constants.NAME)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.STREET)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.NUMBER)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.DISTRICT)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.ZIPCODE)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.CITY)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.STATE)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.LATITUDE)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.LONGITUDE)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.PORT)));
            singleUPA.add(res.getString(res.getColumnIndex(Constants.PHONE)));

            upaData.put(res.getString(res.getColumnIndex(VIRTUAL_TABLE_COLUMN_ID )), singleUPA);
            res.moveToNext();
        }
        return upaData;
    }


    public Map<String, List<String>> getPHByQuery(String query, LatLng currentPos){

        Map<String, List<String>> phBrasilData = new HashMap<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select "+ VIRTUAL_TABLE_COLUMN_ID + ","+ Constants.STREET + "," + Constants.ZIPCODE + "," +
                Constants.CITY + "," + Constants.STATE + "," + Constants.LATITUDE + "," + Constants.LONGITUDE + " from "+ PHBRASIL_VIRTUAL_TABLE_NAME +
                " ORDER BY abs("+ Constants.LATITUDE +" - " +  currentPos.latitude  + ") + abs("+ Constants.LONGITUDE +" - "+ currentPos.longitude + ") LIMIT 20;", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            ArrayList<String> singlePHBRASIL = new ArrayList<>();
            singlePHBRASIL.add(PHBRASIL_NAME);
            singlePHBRASIL.add(res.getString(res.getColumnIndex(Constants.STREET)));
            singlePHBRASIL.add(PHBRASIL_NUMBER);
            singlePHBRASIL.add(PHBRASIL_DISTRICT);
            singlePHBRASIL.add(res.getString(res.getColumnIndex(Constants.ZIPCODE)));
            singlePHBRASIL.add(res.getString(res.getColumnIndex(Constants.CITY)));
            singlePHBRASIL.add(res.getString(res.getColumnIndex(Constants.STATE)));
            singlePHBRASIL.add(res.getString(res.getColumnIndex(Constants.LATITUDE)));
            singlePHBRASIL.add(res.getString(res.getColumnIndex(Constants.LONGITUDE)));
            singlePHBRASIL.add(PHBRASIL_PORTE);
            singlePHBRASIL.add(PHBRASIL_PHONE);

            phBrasilData.put(res.getString(res.getColumnIndex(VIRTUAL_TABLE_COLUMN_ID )), singlePHBRASIL);
            res.moveToNext();
        }
        return phBrasilData;
    }

//
//    private  HashMap<String, Double> preCalculateDistanceValues(double latitude, double longitude){
//
//        HashMap<String, Double> preCalculatedValues =  new HashMap<>();
//
//
//        preCalculatedValues.put(UPAS_COLUMN_COSLAT, Math.cos(MathUtil.deg2rad(latitude)));
//        preCalculatedValues.put(UPAS_COLUMN_SINLAT, Math.sin(MathUtil.deg2rad(latitude)));
//        preCalculatedValues.put(UPAS_COLUMN_COSLONG, Math.cos(MathUtil.deg2rad(longitude)));
//        preCalculatedValues.put(UPAS_COLUMN_SINLONG, Math.sin(MathUtil.deg2rad(longitude)));
//
//
//        return  preCalculatedValues;
//    }



}

