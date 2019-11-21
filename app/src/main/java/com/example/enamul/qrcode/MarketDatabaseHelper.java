package com.example.enamul.qrcode;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MarketDatabaseHelper extends SQLiteOpenHelper {
    private static final String DBNAME="STAND"; //Nazwa bazy
    private static final int DBVER=1;           //Wersja bazy

    public MarketDatabaseHelper(Context context){

        super(context,DBNAME,null,DBVER);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Utworzenie pustej tabeli, pierwsze uruchomienie aplikacji
        String sqlString=
                "CREATE TABLE "+ DBNAME +" (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "NAME TEXT, QUANTITY INTEGER, MONEY TEXT)";

        sqLiteDatabase.execSQL(sqlString);

        ContentValues itemValues = new ContentValues();

        for(int i=0; i< AktywnoscOpis.tabelaNazw.length; i++){
            itemValues.clear();
            itemValues.put("NAME",AktywnoscOpis.tabelaNazw[i]);
            itemValues.put("QUANTITY",0);
            itemValues.put("MONEY",AktywnoscOpis.tabelaPieniadze[0]);

            sqLiteDatabase.insert(DBNAME,null,itemValues); //-1 error
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DBNAME);
        onCreate(sqLiteDatabase);
    }


} //class MarketDatabaseHelper

