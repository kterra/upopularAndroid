package inovapps.upopular;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    public static final String UPAS_TABLE_NAME = "upas";
    public static final String UPAS_VIRTUAL_NAME = "fts_upas";
    public static final String UPAS_COLUMN_ID = "gid";
    public static final String UPAS_COLUMN_CEP = "cep";
    public static final String UPAS_COLUMN_PHONE = "telefone";
    public static final String UPAS_COLUMN_PORTE = "porte";
    public static final String UPAS_COLUMN_NAME = "nome_fantasia";
    public static final String UPAS_COLUMN_STREET = "logradouro";
    public static final String UPAS_COLUMN_NUMBER = "numero";
    public static final String UPAS_COLUMN_DISTRICT = "bairro";
    public static final String UPAS_COLUMN_CITY = "cidade";
    public static final String UPAS_COLUMN_STATE = "estado";
    public static final String UPAS_COLUMN_LAT = "latitude";
    public static final String UPAS_COLUMN_COSLAT = "cos_lat";
    public static final String UPAS_COLUMN_SINLAT = "sin_lat";
    public static final String UPAS_COLUMN_LONG = "longitude";
    public static final String UPAS_COLUMN_COSLONG = "cos_long";
    public static final String UPAS_COLUMN_SINLONG = "sin_long";
    public static final String UPAS_TABLE_CREATE = "create table " + UPAS_TABLE_NAME + " " +
            "("
            + UPAS_COLUMN_ID + " integer, "
            + UPAS_COLUMN_CEP + " text, "
            + UPAS_COLUMN_STATE + " text, "
            + UPAS_COLUMN_CITY + " text, "
            + UPAS_COLUMN_NAME + " text, "
            + UPAS_COLUMN_DISTRICT + " text, "
            + UPAS_COLUMN_NUMBER + " text, "
            + UPAS_COLUMN_STREET + " text, "
            + UPAS_COLUMN_PHONE + " text, "
            + UPAS_COLUMN_PORTE + " text, "
            + UPAS_COLUMN_LAT + " double, "
            + UPAS_COLUMN_LONG + " double) ";

    public static final String UPAS_VIRTUAL_CREATE = "create virtual table " + UPAS_VIRTUAL_NAME
            + " using fts4(content=" + UPAS_TABLE_NAME + ", "
            + UPAS_COLUMN_CEP + ", "
            + UPAS_COLUMN_STATE + ", "
            + UPAS_COLUMN_CITY + ", "
            + UPAS_COLUMN_NAME + ", "
            + UPAS_COLUMN_DISTRICT + ", "
            + UPAS_COLUMN_NUMBER + ", "
            + UPAS_COLUMN_STREET + ", "
            + UPAS_COLUMN_PHONE + ", "
            + UPAS_COLUMN_PORTE + ", "
            + UPAS_COLUMN_LAT + ", "
            + UPAS_COLUMN_LONG + ") ";
    public static final String VIRTUAL_UPAS_TABLE_REBUILD = "INSERT INTO "
            + UPAS_VIRTUAL_NAME + " (" + UPAS_VIRTUAL_NAME + ") VALUES('rebuild')";

    public static final String PHBRASIL_TABLE_NAME = "farmacia_popular_brasil";
    public static final String PHBRASIL_COLUMN_ID = "gid";
    public static final String PHBRASIL_COLUMN_CEP = "nu_cep_farmacia";
    public static final String PHBRASIL_COLUMN_ADDRESS = "ds_endereco_farmacia";
    public static final String PHBRASIL_COLUMN_CITY = "cidade";
    public static final String PHBRASIL_COLUMN_STATE = "uf";
    public static final String PHBRASIL_COLUMN_LAT = "lat";
    public static final String PHBRASIL_COLUMN_LONG = "long";
    public static final String PHBRASIL_TABLE_CREATE = "create table " + PHBRASIL_TABLE_NAME + " " +
            "("
            + PHBRASIL_COLUMN_ID + " integer, "
            + PHBRASIL_COLUMN_CEP + " text, "
            + PHBRASIL_COLUMN_STATE + " text, "
            + PHBRASIL_COLUMN_CITY + " text, "
            + PHBRASIL_COLUMN_ADDRESS + " text, "
            + PHBRASIL_COLUMN_LAT + " double, "
            + PHBRASIL_COLUMN_LONG + " double) ";
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
        Log.e(TAG, "Criando a tabela: " + UPAS_VIRTUAL_NAME);
        db.execSQL(UPAS_VIRTUAL_CREATE);
        insertUPAData(inputUPAStream, db);

        db.execSQL(PHBRASIL_TABLE_CREATE);
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
                if(!(columns[0].compareTo("gid")==0)){
                    ContentValues cv = new ContentValues();
                    cv.put(UPAS_COLUMN_ID, Integer.valueOf(columns[0].trim()));
                    cv.put(UPAS_COLUMN_CEP, columns[1].trim());
                    cv.put(UPAS_COLUMN_STATE, columns[2].trim());
                    cv.put(UPAS_COLUMN_CITY, columns[3].trim());
                    cv.put(UPAS_COLUMN_NAME, columns[4].trim());
                    cv.put(UPAS_COLUMN_DISTRICT, columns[5].trim());
                    cv.put(UPAS_COLUMN_NUMBER, columns[6].trim());
                    cv.put(UPAS_COLUMN_STREET, columns[7].trim());
                    cv.put(UPAS_COLUMN_PHONE, columns[8].trim());
                    cv.put(UPAS_COLUMN_PORTE, columns[12].trim());

                    double latitude = Double.valueOf(columns[13].trim());
                    double longitude = Double.valueOf(columns[14].trim());

                    cv.put(UPAS_COLUMN_LAT, latitude);
                    cv.put(UPAS_COLUMN_LONG, longitude);

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
        String line = "";
        db.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(",");
                if(!(columns[0].compareTo("gid")==0)){
                    ContentValues cv = new ContentValues();
                    cv.put(PHBRASIL_COLUMN_ID, Integer.valueOf(columns[0].trim().replace("\"","")));
                    cv.put(PHBRASIL_COLUMN_ADDRESS, columns[5].trim().replace("\"", ""));
                    cv.put(PHBRASIL_COLUMN_CEP, columns[6].trim().replace("\"", ""));
                    cv.put(PHBRASIL_COLUMN_STATE, columns[7].trim().replace("\"", ""));
                    cv.put(PHBRASIL_COLUMN_CITY, columns[8].trim().replace("\"", ""));


                    double latitude = Double.valueOf(columns[1].trim().replace("\"", ""));
                    double longitude = Double.valueOf(columns[2].trim().replace("\"",""));

                    cv.put(PHBRASIL_COLUMN_LAT, latitude);
                    cv.put(PHBRASIL_COLUMN_LONG, longitude);

//                    HashMap<String, Double> preCalculatedValues = preCalculateDistanceValues(latitude, longitude);
//
//                    cv.put(UPAS_COLUMN_COSLAT, preCalculatedValues.get(UPAS_COLUMN_COSLAT));
//                    cv.put(UPAS_COLUMN_SINLAT, preCalculatedValues.get(UPAS_COLUMN_SINLAT));
//                    cv.put(UPAS_COLUMN_COSLONG, preCalculatedValues.get(UPAS_COLUMN_COSLONG));
//                    cv.put(UPAS_COLUMN_SINLONG, preCalculatedValues.get(UPAS_COLUMN_SINLONG));

                    db.insert(PHBRASIL_TABLE_NAME, null, cv);
                }

            }
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

        Cursor res =  db.rawQuery( "select gid, nome_fantasia, logradouro, numero, bairro, cidade, estado, latitude, longitude, porte, telefone from "+ UPAS_TABLE_NAME +
                " ORDER BY abs(latitude - " + CUR_lat + ") + abs(longitude - "+ CUR_lng + ") LIMIT 20;", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            ArrayList<String> singleUPA = new ArrayList<>();

            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_NAME)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_STREET)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_NUMBER)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_DISTRICT)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_CITY)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_STATE)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_LAT)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_LONG)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_PORTE)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_PHONE)));

            upaData.put(res.getString(res.getColumnIndex(UPAS_COLUMN_ID)), singleUPA);
            res.moveToNext();
        }


        return upaData;
    }

    public HashMap<String,ArrayList<String>> getPHMainData(double CUR_lat, double CUR_lng)
    {
        HashMap<String,ArrayList<String>> phBrasilData = new HashMap<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select gid, ds_endereco_farmacia, nu_cep_farmacia, cidade, uf, lat, long from "+ PHBRASIL_TABLE_NAME +
               " ORDER BY abs(lat - " + CUR_lat + ") + abs(long - "+ CUR_lng + ") LIMIT 20;", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            ArrayList<String> singlePHBRASIL = new ArrayList<>();
            singlePHBRASIL.add(res.getString(res.getColumnIndex(PHBRASIL_COLUMN_ADDRESS)));
            singlePHBRASIL.add(res.getString(res.getColumnIndex(PHBRASIL_COLUMN_CEP)));
            singlePHBRASIL.add(res.getString(res.getColumnIndex(PHBRASIL_COLUMN_STATE)));
            singlePHBRASIL.add(res.getString(res.getColumnIndex(PHBRASIL_COLUMN_LAT)));
            singlePHBRASIL.add(res.getString(res.getColumnIndex(PHBRASIL_COLUMN_LONG)));


            phBrasilData.put(res.getString(res.getColumnIndex(PHBRASIL_COLUMN_ID)), singlePHBRASIL);
            res.moveToNext();
        }
        return phBrasilData;
    }

    public Map<String, List<String>> getUpaByQuery(String query){

        Map<String,List<String>> upaData = new HashMap<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select rowid, nome_fantasia, logradouro, numero, bairro, cidade, estado, latitude, longitude, porte, telefone from "+ UPAS_VIRTUAL_NAME + " WHERE " + UPAS_VIRTUAL_NAME + " MATCH '" + query + "'" +
                " ORDER BY abs(latitude) + abs(longitude) LIMIT 50;", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            ArrayList<String> singleUPA = new ArrayList<>();
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_NAME)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_STREET)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_NUMBER)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_DISTRICT)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_CITY)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_STATE)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_LAT)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_LONG)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_PORTE)));
            singleUPA.add(res.getString(res.getColumnIndex(UPAS_COLUMN_PHONE)));

            upaData.put(res.getString(res.getColumnIndex("rowid")), singleUPA);
            res.moveToNext();
        }
        return upaData;
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

