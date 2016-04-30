package inovapps.upopular;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by kizzyterra on 4/29/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Upopular.db";
    public static final String UPAS_TABLE_NAME = "upas";
    public static final String UPAS_COLUMN_ID = "gid";
    public static final String UPAS_COLUMN_CEP = "cep";
    public static final String UPAS_COLUMN_NAME = "nome_fantasia";
    public static final String UPAS_COLUMN_STREET = "logradouro";
    public static final String UPAS_COLUMN_NUMBER = "numero";
    public static final String UPAS_COLUMN_DISTRICT = "bairro";
    public static final String UPAS_COLUMN_CITY = "cidade";
    public static final String UPAS_COLUMN_STATE = "estado";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + UPAS_TABLE_NAME + " " +
                        "("
                        + UPAS_COLUMN_ID + " integer, "
                        + UPAS_COLUMN_CEP + " text, "
                        + UPAS_COLUMN_NAME + " text, "
                        + UPAS_COLUMN_STREET + " text, "
                        + UPAS_COLUMN_NUMBER + " text, "
                        + UPAS_COLUMN_DISTRICT + " text, "
                        + UPAS_COLUMN_CITY + " text, "
                        + UPAS_COLUMN_STATE + " text)"
        );
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS "+ UPAS_TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insertData(InputStream inStream) {

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        SQLiteDatabase db = this.getWritableDatabase();
        String line = "";
        db.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {
                String[] columns = line.split(",");
                if(!(columns[0].compareTo("gid")==0)){
                    ContentValues cv = new ContentValues();
                    cv.put(UPAS_COLUMN_ID, Integer.valueOf(columns[0].trim()));
                    cv.put(UPAS_COLUMN_NAME, columns[1].trim());
                    cv.put(UPAS_COLUMN_STREET, columns[2].trim());
                    cv.put(UPAS_COLUMN_NUMBER, columns[3].trim());
                    cv.put(UPAS_COLUMN_DISTRICT, columns[4].trim());
                    cv.put(UPAS_COLUMN_CITY, columns[5].trim());
                    cv.put(UPAS_COLUMN_STATE, columns[6].trim());
                    db.insert(UPAS_TABLE_NAME, null, cv);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();

    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from upas where id=" + id + "", null);
        return res;
    }

    public ArrayList<String> getAllData()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ UPAS_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(UPAS_COLUMN_ID)));
            res.moveToNext();
        }
        return array_list;
    }



}

