package com.example.enamul.qrcode;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AktywnoscOpis extends AppCompatActivity {

    //Asortyment
    public static String[] tabelaNazw,tabelaKupna,tabelaSprzedazy,tabelaPieniadze;
    Integer currentItemQuantity = null;
    String currentItemName = null;
    String currentMoney = null;
    String currentSell = null;
    String currentBuy = null;
    String konto = null;


    //Kontrolki potrzebne w wiecej niz jednej metodzie
    TextView stateTV=null;
    EditText changeET=null;

    TextView nazwa,opis,kasa,kupno,sprzedaz;
    ImageView obraz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktywnosc_opis);

        nazwa = (TextView) findViewById(R.id.nazwa);
        opis = (TextView) findViewById(R.id.opis);
        obraz = (ImageView) findViewById(R.id.obraz);
        kupno = (TextView) findViewById(R.id.buy);
        sprzedaz = (TextView) findViewById(R.id.sell);
        kasa = (TextView) findViewById(R.id.money);

        final int id =(int) getIntent().getLongExtra("id",0);

        nazwa.setText(Aplikacja.NAME[id]);
        opis.setText(Aplikacja.DESCRIPTION[id]);
        kupno.setText(Aplikacja.BUY[id] + "zł");
        sprzedaz.setText(Aplikacja.SELL[id] + "zł");
        obraz.setImageResource(Aplikacja.IMAGE.getResourceId(id,0));

//Od magazynu

        stateTV = (TextView) findViewById(R.id.stateTextView); //XML->Java
        changeET = (EditText) findViewById(R.id.editText); //XML->Java

        tabelaNazw = getResources().getStringArray(R.array.names);
        tabelaKupna = getResources().getStringArray(R.array.buy);
        tabelaSprzedazy = getResources().getStringArray(R.array.sell);
        tabelaPieniadze = getResources().getStringArray(R.array.money);

        //Dostep do bazy
        final SQLiteOpenHelper DBHelper = new MarketDatabaseHelper(this);

                currentItemName = tabelaNazw[id]; //Nazwa wskazana przez spinner
                currentBuy = tabelaKupna[id];
                currentSell = tabelaSprzedazy[id];
                currentMoney = String.valueOf(tabelaPieniadze[0]);
                konto = tabelaNazw[0];

                //Realizujemy polecenie SQL
                //SELECT QUANTITY FROM STAND WHERE NAME=currentItemName
                try
                {
                    SQLiteDatabase DB = DBHelper.getReadableDatabase();
                    //Pobranie informacji o ilosci przedmiotu na magazynie
                    Cursor cursor = DB.query(
                            "STAND",
                            new String[] {"QUANTITY"},
                            "NAME = ?",
                            new String[]{currentItemName},
                            null,null,null);

                    cursor.moveToFirst();//Kursor zawsze trzeba ustawic
                    cursor.moveToPosition(0);
                    currentItemQuantity = cursor.getInt(0);
                    cursor.close();

                    //Pobranie informacji o dostepnych pieniadzach
                    Cursor cursor2 = DB.query(
                            "STAND",
                            new String[] {"MONEY"},
                            "NAME = ?",
                            new String[]{konto},
                            null,null,null);
                    cursor2.moveToFirst();
                    cursor2.moveToPosition(0);
                    currentMoney = cursor2.getString(0);
                    cursor2.close();
                    DB.close();
                }
                catch(SQLiteException e)
                {
                    Toast.makeText(AktywnoscOpis.this,
                            "EXCEPTION: SPINNER", Toast.LENGTH_SHORT).show();
                }

                stateTV.setText("Stan magazynu dla " +currentItemName+ " :"+currentItemQuantity );
                kasa.setText("Aktualny stan konta: " + currentMoney+  "zł");

           // }
        //});

        //Przycisk "Skladuj"
        Button setButton = (Button) findViewById(R.id.setButton);

        setButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view) { //Dodanie do bazy

                EditText etErrorMesA = (EditText) findViewById(R.id.editText);
                String strErrorMesA  = etErrorMesA.getText().toString();

                if(TextUtils.isEmpty(strErrorMesA )) {
                    etErrorMesA.setError("Wprowadz liczbę sztuk!");
                    return;
                }
                else{
                    Integer changeItemQuantity = Integer.parseInt(changeET.getText().toString());
                    Integer tranzakcja = changeItemQuantity * (Integer.parseInt(currentBuy));
                    Integer newBudget = (Integer.parseInt(currentMoney)) - tranzakcja;
                        if(newBudget<0){
                            etErrorMesA.setError("Brak środków na zakup tylu sztuk!");
                            return;
                        }
                    Integer newItemQuantity=currentItemQuantity+changeItemQuantity;

                    //Realizujemy polecenie SQL
                    //UPDATE STAND SET QUANTITY=newItemQuantity WHERE NAME=currentItemName
                    try
                    {
                        //Uaktualnienie informacji o ilosci przedmiotu na magazynie
                        SQLiteDatabase DB = DBHelper.getWritableDatabase();
                        ContentValues itemValues = new ContentValues();
                        itemValues.put("QUANTITY",newItemQuantity.toString());
                        DB.update("STAND", itemValues, "NAME=?", new String[] {currentItemName});
                        DB.close();

                        //Uaktualnienie informacji o dostepnych pieniadzach
                        SQLiteDatabase DB2 = DBHelper.getWritableDatabase();
                        ContentValues itemValues2 = new ContentValues();
                        itemValues2.put("MONEY",newBudget.toString());
                        DB2.update("STAND",itemValues2,"NAME=?", new String[] {konto});
                        DB2.close();
                    }
                    catch (SQLiteException e)
                    {
                        Toast.makeText(AktywnoscOpis.this,"EXCEPTION:SET",
                                Toast.LENGTH_SHORT).show();
                    }
                    stateTV.setText("Stan magazynu dla " +currentItemName+" : "+newItemQuantity);
                    changeET.setText("");
                    currentMoney = newBudget.toString();
                    kasa.setText("Aktualny stan konta: " + currentMoney + "zł");
                    currentItemQuantity=newItemQuantity;
                }
                }

        });


        //Przycisk "Wydaj"
        Button getButton = (Button) findViewById(R.id.getButton);

        getButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                EditText etErrorMesA = (EditText) findViewById(R.id.editText);
                String strErrorMesA  = etErrorMesA.getText().toString();

                if(TextUtils.isEmpty(strErrorMesA )) {
                    etErrorMesA.setError("Wprowadz liczbę sztuk!");
                    return;
                }

                Integer changeItemQuantity = Integer.parseInt(changeET.getText().toString());
                Integer tranzakcja = changeItemQuantity * (Integer.parseInt(currentSell));
                Integer newBudget = (Integer.parseInt(currentMoney)) + tranzakcja;

                Integer newItemQuantity=currentItemQuantity-changeItemQuantity;

                if(newItemQuantity < 0 ) {
                    etErrorMesA.setError("Nie wystarczajaca liczba sztuk na magazynie!");
                   // newItemQuantity=currentItemQuantity;
                    return;
                }

                else
                //UPDATE STAND SET QUANTITY=newItemQuantity WHERE NAME=currentItemName
                try
                {

                    //Uaktualnienie informacji o dostepnym przedmiocie na magazynie
                    SQLiteDatabase DB = DBHelper.getWritableDatabase();
                    ContentValues itemValues = new ContentValues();
                    itemValues.put("QUANTITY",newItemQuantity.toString());
                    DB.update("STAND", itemValues, "NAME=?", new String[] {currentItemName});
                    DB.close();

                    //Uaktualnienie informacji o dostepnych pieniadzach
                    SQLiteDatabase DB2 = DBHelper.getWritableDatabase();
                    ContentValues itemValues2 = new ContentValues();
                    itemValues2.put("MONEY",newBudget.toString());
                    DB2.update("STAND",itemValues2,"NAME=?", new String[] {konto});
                    DB2.close();
                }
                catch (SQLiteException e)
                {
                    Toast.makeText(AktywnoscOpis.this,"EXCEPTION: GET",
                            Toast.LENGTH_SHORT).show();
                }

                stateTV.setText("Stan magazynu dla " +currentItemName+" :"+newItemQuantity);
                changeET.setText("");
                currentMoney = newBudget.toString();
                kasa.setText("Aktualny stan konta: " + currentMoney + "zł");
                currentItemQuantity=newItemQuantity;
            }
        });
    }
}
