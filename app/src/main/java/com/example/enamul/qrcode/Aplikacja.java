package com.example.enamul.qrcode;

import android.app.Application;
import android.content.res.Resources;
import android.content.res.TypedArray;

public class Aplikacja extends Application {

    public static TypedArray IMAGE;
    public static String[] DESCRIPTION;
    public static String[] NAME;
    public static String[] SELL;
    public static String[] BUY;
    public static int SIZE;


    @Override
    public void onCreate(){
        super.onCreate();

        Resources resources = getResources();
        NAME = resources.getStringArray(R.array.names);
        IMAGE = resources.obtainTypedArray(R.array.pictures);
        DESCRIPTION = resources.getStringArray(R.array.descriptions);
        SELL = resources.getStringArray(R.array.sell);
        BUY = resources.getStringArray(R.array.buy);

        int size0 = NAME.length;
        int size1 = IMAGE.length();
        int size2 = DESCRIPTION.length;
        int size3 = SELL.length;
        int size4 = BUY.length;


        if(size0 == size1 && size1 == size2 && size2 == size3 && size3 == size4) SIZE = size0; else SIZE=0;
    }
}
