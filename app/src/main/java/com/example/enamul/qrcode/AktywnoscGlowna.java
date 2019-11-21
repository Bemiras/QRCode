package com.example.enamul.qrcode;


import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class AktywnoscGlowna extends AppCompatActivity {
    Button btnScan;
    Button button;
    TextView tv_qr_readTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktywnosc_glowna);

        ViewPager viewPager = findViewById(R.id.viewPager);
        ImageAdapter adapter = new ImageAdapter(this);
        viewPager.setAdapter(adapter);
        btnScan = (Button)findViewById(R.id.Scan_Button);
        tv_qr_readTxt = (TextView) findViewById(R.id.tv_qr_readTxt);


        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(AktywnoscGlowna.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Zeskanuj kod QR produktu");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();

            }
        });
    }

    public void buttonClickFunction(View v)
    {
        Intent intent = new Intent(getApplicationContext(), AktywnoscArtykuly.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Anulowanie skanowanie");

            } else {
                Log.e("Scan", "Wyszukany produkt: ");

                tv_qr_readTxt.setText(result.getContents());
                Toast.makeText(this, "Wyszukany produkt: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {

            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
