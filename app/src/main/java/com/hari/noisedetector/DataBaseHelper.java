package com.hari.noisedetector;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hari on 15-12-2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    public  static  final  String DATABASE_NAME ="noise.db";
    public  static  final  String TABLE_NAME ="noise_table";
    //add column names
    public  static  final  String NAME = "name";
    public  static  final  String LATTITUDE ="lattitude";
    public  static  final  String LONGITUDE ="longitude";
    public  static  final  String TIMESTAMP ="timestamp";
    public  static  final  String NOISE ="noise";



    public DataBaseHelper(Context context) {
        super(context,DATABASE_NAME , null, 1);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+TABLE_NAME+"(id INTEGER PRIMARY KEY autoincrement,name TEXT ,lattitude TEXT ,longitude TEXT,timestamp TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //inserting data into the table

    public boolean InsertData(String name ,String lattitude,String longitude, String timestamp,String noise ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME,name);
        cv.put(LATTITUDE,lattitude);
        cv.put(LONGITUDE,longitude);
        cv.put(TIMESTAMP,timestamp);
        cv.put(NOISE,noise);
        long retvar = (long) db.insert(TABLE_NAME,null,cv);
        if (retvar == -1)
            return  false;
        else
            return true;

    }
}
