package com.fakit.healthdatatracking;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "healthdata";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String WearData_TABLE = "weardata";

    private static final String UserData_TABLE = "userdata";

    private static final String CoronaNotification_TABLE="coronanotification";

    private static final String DateTime_COL = "time";

    // below variable is for our Temperature column.
    private static final String Temp_COL = "temperature";

    // below variable is for our  Heartbeat column
    private static final String Heart_COL = "heartbeat";

    // below variable for our course Hbeat column.
    private static final String Pressure_COL = "pressure";

    // below variable id for our Light column.
    private static final String Light_COL = "light";

    // below variable for our course Accelero column.
    private static final String Accelero_COL = "accelero";

    // below variable for our course Accelero column.
    private static final String LinerarAccelero_COL = "linearaccelero";

    // below variable for our course Rhumid column.
    private static final String Rhumid_COL = "rhumid";

    private static final String CoronaLevel_COL= "coronalevel";

    private static final String Age_COL= "age";

    private static final String Sex_COL= "sex";

    private static final String Weight_COL= "weight";

    private static final String Illness_TABLE = "illness";

    private static final String Herzerkrankungen_COL = "herzerkrankungen";

    private static final String Lungenerkrankungen_COL = "lungenerkrankungen";

    private static final String Diabetesmellitus_COL = "diabetesmellitus";

    private static final String Nierenerkrankungen_COL = "nierenerkrankungen";

    private static final String Malignom_COL  = "malignom";

    private static final String MainSymptomes_TABLE = "mainsymptomes";

    private static final String GeruchsGechmack_COL = "geruchsgechmack";

    private static final String Schnupfen_COL = "schnupfen";

    private static final String Husten_COL = "husten";

    private static final String OderSymptomes_TABLE = "odersymptomes";

    private static final String MuskelundGliederschmerzen_COL = "muskelundgliederschmerzen";

    private static final String Bauchschmerzen_COL = "bauchschmerzen";

    private static final String Bindehautentzuendung_COL = "bindehautentzuendung";

    private static final String StarkeBindehautentzuendung_COL = "starkebindehautentzuendung";

    private static final String Hautausschlag_COL = "hautausschlag";

    private static final String leichterespiratorischeSymptome_COL  = "leichterespiratorischeSymptome";

    private static final String Atemnot_COL  = "atemnot";

    private static final String HalsundKopfschmerzen_COL  = "halsundkopfschmerzen";

    private static final String Schuettelfrost_COL  = "schuettelfrost";

    private static final String uebelkeitErbrechen_COL  = "uebelkeiterbrechen";

    private static final String Durchfall_COL = "durchfall";

    private static final String Appetitlosigkeit_COL  = "appetitlosigkeit";

    private static final String Lymphknotenschwellung_COL = "lymphknotenschwellung";

    private static final String Apathie_COL  = "apathie";

    private static final String Benommenheit_COL  = "benommenheit";

    private static final String LeichteErkaeltungssymptome_COL  = "leichteerkaeltungssymptome";

    private static final String Pneumonie1Grad_COL = "pneumonie1grad";

    private static final String Pneumonie2Grad_COL  = "pneumonie2grad";;

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + MainSymptomes_TABLE + " ("
                + DateTime_COL + " String,"
                + GeruchsGechmack_COL + " Boolean,"
                + Schnupfen_COL + " Boolean,"
                + Husten_COL   + " Boolean)";

        db.execSQL(query);
         query = "CREATE TABLE " + OderSymptomes_TABLE + " ("
                + DateTime_COL + " String,"
                + MuskelundGliederschmerzen_COL + " Boolean,"
                + Bauchschmerzen_COL + " Boolean,"
                + Bindehautentzuendung_COL + " Boolean,"
                + StarkeBindehautentzuendung_COL + " Boolean,"
                + Hautausschlag_COL + " Boolean,"
                + leichterespiratorischeSymptome_COL + " Boolean,"
                + Atemnot_COL + " Boolean,"
                + HalsundKopfschmerzen_COL + " Boolean,"
                + Schuettelfrost_COL + " Boolean,"
                + uebelkeitErbrechen_COL + " Boolean,"
                + Durchfall_COL + " Boolean,"
                + Appetitlosigkeit_COL + " Boolean,"
                + Lymphknotenschwellung_COL + " Boolean,"
                + Apathie_COL + " Boolean,"
                + Benommenheit_COL + " Boolean,"
                + LeichteErkaeltungssymptome_COL + " Boolean,"
                + Pneumonie1Grad_COL + " Boolean,"
                + Pneumonie2Grad_COL  + " Boolean)";

        db.execSQL(query);
         query = "CREATE TABLE " + WearData_TABLE + " ("
                + DateTime_COL + " String,"
                + Temp_COL + " Float,"
                + Heart_COL + " Float,"
                + Pressure_COL + " Float,"
                + Light_COL + " Float,"
                + LinerarAccelero_COL + " Float,"
                + Rhumid_COL + " Float)";

        db.execSQL(query);
         query = "CREATE TABLE " + Illness_TABLE + " ("
                 + DateTime_COL + " String,"
                + Herzerkrankungen_COL + " Boolean,"
                + Lungenerkrankungen_COL + " Boolean,"
                + Diabetesmellitus_COL + " Boolean,"
                + Nierenerkrankungen_COL + " Boolean,"
                + Malignom_COL  + " Boolean)";

        db.execSQL(query);
         query = "CREATE TABLE " + UserData_TABLE + " ("
                + DateTime_COL + " String,"
                + Age_COL + " Float,"
                + Weight_COL + " Float,"
                + Sex_COL + " Float)";

        // method to execute above sql query
        db.execSQL(query);
         query = "CREATE TABLE " + CoronaNotification_TABLE + " ("
                + DateTime_COL + " TEXT,"
                + CoronaLevel_COL + " TEXT)";

        // method to execute above sql query
        db.execSQL(query);
       /* query = " INSERT INTO " + CoronaNotification_TABLE + " ( "
                + DateTime_COL + " , "
                + CoronaLevel_COL + " ) VALUES ( "+new Date().toString()+" , 1 )";

        // method to execute above sql query
        db.execSQL(query);*/



    }

    public  ArrayList<Double> getPlotData(String ColName){

        SQLiteDatabase db = this.getReadableDatabase();

        String query ="SELECT "  + ColName + " FROM "+WearData_TABLE+" ORDER BY ROWID DESC Limit 100;";
        Cursor cursor = db.rawQuery(query,null);
        ArrayList<Double> mArrayList = new ArrayList<Double>();
        while(cursor.moveToNext()) {mArrayList.add(cursor.getDouble(cursor.getColumnIndexOrThrow(ColName)));}

        return mArrayList;
    }
    private Date getlastCoronaNotificationDate() {

        SQLiteDatabase db = this.getReadableDatabase();
        Date date =new Date();
        try {
            String query ="SELECT * FROM "+CoronaNotification_TABLE+" ORDER BY ROWID DESC LIMIT 1;";
            Cursor cursor = db.rawQuery(query,null);
            if (cursor!=null)  cursor.moveToFirst();


            date = new Date(cursor.getString(0));
        }catch (Exception e){}
        return date;

    }
    public int getCoronaLevel(){


        SQLiteDatabase db = this.getReadableDatabase();
        int level =0;
        try {
        String query ="SELECT * FROM "+CoronaNotification_TABLE+" ORDER BY ROWID DESC LIMIT 1;";
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();


            level = cursor.getInt(1);
        }catch (Exception e){}
        return level;

    }

    // this method is use to add new course to our sqlite database.
    public void AddWearData(String DateTime, Float Temp, Float Heart, Float Pressure, Float Light,  Float LinearAccelero,Float Rhumid) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.

        values.put(DateTime_COL, DateTime);
        values.put(Temp_COL, Temp);
        values.put(Heart_COL, Heart);
        values.put(Pressure_COL, Pressure);
        values.put(Light_COL, Light);
        values.put(LinerarAccelero_COL, LinearAccelero);
        values.put(Rhumid_COL, Rhumid);

        // after adding all values we are passing
        // content values to our table.
        db.insert(WearData_TABLE, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();




    }
    // this method is use to add new course to our sqlite database.
    public void AddUserData(String DateTime, int age, int weight, String sex) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.

        values.put(DateTime_COL, DateTime);
        values.put(Age_COL, age);
        values.put(Sex_COL, sex);
        values.put(Weight_COL, weight);


        // after adding all values we are passing
        // content values to our table.
        db.insert(UserData_TABLE, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();




    }
    public void AddIllness(String DateTime, Boolean Herzerkrankungen,Boolean Lungenerkrankungen,
                           Boolean Diabetesmellitus,Boolean Nierenerkrankungen,Boolean Malignom) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.

        values.put(DateTime_COL, DateTime);
        values.put(Herzerkrankungen_COL, Herzerkrankungen);
        values.put(Lungenerkrankungen_COL, Lungenerkrankungen);
        values.put(Diabetesmellitus_COL, Diabetesmellitus);
        values.put(Nierenerkrankungen_COL, Nierenerkrankungen);
        values.put(Malignom_COL, Malignom);


        // after adding all values we are passing
        // content values to our table.
        db.insert(Illness_TABLE, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();




    }
    public void AddOderSymptomes(String DateTime,
            Boolean MuskelundGliederschmerzen,
            Boolean Bauchschmerzen,
            Boolean Bindehautentzuendung,
            Boolean StarkeBindehautentzuendung,
            Boolean Hautausschlag,
            Boolean leichterespiratorischeSymptome,
            Boolean Atemnot,
            Boolean HalsundKopfschmerzen,
            Boolean Schuettelfrost,
            Boolean uebelkeitErbrechen,
            Boolean Durchfall,
            Boolean Appetitlosigkeit,
            Boolean Lymphknotenschwellung,
            Boolean Apathie,
            Boolean Benommenheit,
            Boolean LeichteErkaeltungssymptome,
            Boolean Pneumonie1Grad,
            Boolean Pneumonie2Grad) {

        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        values.put(DateTime_COL, DateTime);
        values.put(MuskelundGliederschmerzen_COL, MuskelundGliederschmerzen);
        values.put(Bauchschmerzen_COL, Bauchschmerzen);
        values.put(Bindehautentzuendung_COL, Bindehautentzuendung);
        values.put(StarkeBindehautentzuendung_COL, StarkeBindehautentzuendung);
        values.put(Hautausschlag_COL, Hautausschlag);
        values.put(leichterespiratorischeSymptome_COL, leichterespiratorischeSymptome);
        values.put(Atemnot_COL, Atemnot);
        values.put(HalsundKopfschmerzen_COL, HalsundKopfschmerzen);
        values.put(Schuettelfrost_COL, Schuettelfrost);
        values.put(uebelkeitErbrechen_COL, uebelkeitErbrechen);
        values.put(Durchfall_COL, Durchfall);
        values.put(Appetitlosigkeit_COL, Appetitlosigkeit);
        values.put(Lymphknotenschwellung_COL, Lymphknotenschwellung);
        values.put(Apathie_COL, Apathie);
        values.put(Benommenheit_COL, Benommenheit);
        values.put(LeichteErkaeltungssymptome_COL, LeichteErkaeltungssymptome);
        values.put(Pneumonie1Grad_COL, Pneumonie1Grad);
        values.put(Pneumonie2Grad_COL, Pneumonie2Grad);

        // after adding all values we are passing
        // content values to our table.
        db.insert(OderSymptomes_TABLE, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }
    public void AddMainSymptomes(String DateTime,Boolean GeruchsGechmack, Boolean Schnupfen,Boolean Husten) {

        SQLiteDatabase db = this.getWritableDatabase();

        // variable for content values.
        ContentValues values = new ContentValues();

        values.put(DateTime_COL, DateTime);
        values.put(GeruchsGechmack_COL, GeruchsGechmack);
        values.put(Schnupfen_COL, Schnupfen);
        values.put(Husten_COL, Husten);

        // content values to our table.
        db.insert(MainSymptomes_TABLE, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }
    public void AddCoronaNotification(String DateTime, String CoronaLevel) {
        //Date LastDate= getlastCoronaNotificationDate();
        //if (timeSpan_seconds(LastDate,new Date())>5){
        SQLiteDatabase db = this.getWritableDatabase();

        // variable for content values.
        ContentValues values = new ContentValues();

        values.put(DateTime_COL, DateTime);

        int level = -1;
        if (CoronaLevel.toLowerCase(Locale.ROOT).contains("mit erhöhtem risiko") ||
                CoronaLevel.toLowerCase(Locale.ROOT).contains("mit erhoehtem risiko") ) level=3;
        else if(CoronaLevel.toLowerCase(Locale.ROOT).contains("begegnung mit"))  level=2;
        else if(CoronaLevel.toLowerCase(Locale.ROOT).contains("covid")||
                CoronaLevel.toLowerCase(Locale.ROOT).contains("begegnung")) level=1;
        values.put(CoronaLevel_COL, level);

        // after adding all values we are passing
        // content values to our table.

        if (level>0) db.insert(CoronaNotification_TABLE, null, values);
        db.close();
       // }
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + MainSymptomes_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + OderSymptomes_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Illness_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + UserData_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + WearData_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CoronaNotification_TABLE);
        onCreate(db);
    }
    public  ArrayList<Boolean> getSymptomes(String dbName) {

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + dbName +" ORDER BY ROWID DESC LIMIT 1;";
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Boolean> mArrayList = new ArrayList<Boolean>();
        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 1; i < cursor.getColumnCount(); i++) {
                if (cursor.getCount()>0) mArrayList.add(cursor.getInt(i) == 1);
                else mArrayList.add(false);
              }

        }
        cursor.close();
        db.close();
        return mArrayList;

    }
    public WearDataCollection getLastWearData() {
        WearDataCollection WDC= new WearDataCollection("",0.0f,0.0f,
                0.0f,0.0f,0.0f,0.0f);
        try{
        SQLiteDatabase db = this.getReadableDatabase();

        String query ="SELECT * FROM "+WearData_TABLE+" ORDER BY ROWID DESC LIMIT 1;";
        Cursor cursor = db.rawQuery(query,null);

        if (cursor != null) {
            cursor.moveToFirst();
            WDC = new WearDataCollection(cursor.getString(0),
                    cursor.getFloat(1), cursor.getFloat(2),
                    cursor.getFloat(3), cursor.getFloat(4),
                    cursor.getFloat(5), cursor.getFloat(6));
        }
        cursor.close();
        db.close();}
        catch (Exception e){}
           return WDC;
    }

    public User getLastUser() {
        User US= new User("0",0,0,"");
        try{
        SQLiteDatabase db = this.getReadableDatabase();

        String query ="SELECT * FROM "+UserData_TABLE+" ORDER BY ROWID DESC LIMIT 1;";
        Cursor cursor = db.rawQuery(query,null);

        if (cursor != null) {
            cursor.moveToFirst();
            US= new User(cursor.getString(0),cursor.getInt(1),
                    cursor.getInt(2), cursor.getString(3));
        }
    }catch (Exception e){}
           return US;
    }

    public Vorerkrankungen getVorerkrankungen() {

        SQLiteDatabase db = this.getReadableDatabase();

        String query ="SELECT * FROM "+Illness_TABLE+" ORDER BY ROWID DESC LIMIT 1;";
        Cursor cursor = db.rawQuery(query,null);
        Vorerkrankungen VE= new Vorerkrankungen("0",false,false,
                false,false,false);
        if (cursor != null) {
            cursor.moveToFirst();
            VE= new Vorerkrankungen(cursor.getString(0),
                    cursor.getInt(1)==1, cursor.getInt(2)==1, cursor.getInt(3)==1,
                    cursor.getInt(4)==1, cursor.getInt(5)==1);
        }
           return VE;
    }
    public int timeSpan_seconds(Date olddate,  Date newdate){

        long diff = newdate.getTime() - olddate.getTime();
        int seconds = (int) (diff / 1000);
        int minutes =(int) ( seconds / 60);
        int hours = (int) (minutes / 60);
        int days = (int) (hours / 24);
        return seconds;
    }
}

