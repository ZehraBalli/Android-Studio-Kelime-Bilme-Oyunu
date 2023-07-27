package com.example.harfalayim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;

import java.util.ArrayList;
//import android.widget.TextView;

//import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
//import java.util.Random;

public class PlayActivity extends AppCompatActivity {


    private Intent get_intent;
    private int hakSayisi, sonHakSayisi;

    private SQLiteStatement statement;
    private String sqlSorgusu;
    private TextView textViewQuestion, textViewQuest;
    private EditText editTextTahminDegeri;
    private SQLiteDatabase database;
    private Cursor cursor;
    private ArrayList<String> sorularList;
    private ArrayList<String> sorularKodList;
    private ArrayList<String> kelimelerList;
    private Random rndSoru, rndKelime, rndHarf;
    private int rndSoruNumber, rndKelimeNumber, rndHarfNumber;
    private ArrayList<Character> kelimeHarfleri;
    private String kelimeBilgisi = "", rastgeleSoru, rastgeleSoruKodu, rastgeleKelime, textTahminDegeri;
    private int rastgeleBelirlenecekHarfSayisi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        textViewQuestion = (TextView) findViewById(R.id.play_activity_textViewQuestion);
        textViewQuest = (TextView) findViewById(R.id.play_activity_textViewQuest);
        editTextTahminDegeri = (EditText)findViewById(R.id.play_activity_editTextGuess);
        sorularList = new ArrayList<>();
        sorularKodList = new ArrayList<>();
        kelimelerList = new ArrayList<>();
        rndSoru = new Random();
        rndKelime = new Random();
        rndHarf = new Random();


        get_intent =getIntent();
        hakSayisi = get_intent.getIntExtra("heartCount",0);


        for (Map.Entry soru : SplashScreenActivity.sorularHasMap.entrySet()) {
            sorularList.add(String.valueOf(soru.getValue()));
            sorularKodList.add(String.valueOf(soru.getKey()));
        }

        randomSoruGetir();
    }


    @Override
    public void onBackPressed() {

        mainIntent();
    }

    public void btnHarfAl(View v) {
        if (hakSayisi > 0){
            if (kelimeHarfleri.size() > 0) {
            rndHarfNumber = rndHarf.nextInt(kelimeHarfleri.size());
            String[] txtHarfler = textViewQuest.getText().toString().split(" ");
            char[] gelenKelimeHarfler = rastgeleKelime.toCharArray();

            for (int a = 0; a < rastgeleKelime.length(); a++) {
                if (txtHarfler[a].equals("_") && gelenKelimeHarfler[a] == kelimeHarfleri.get(rndHarfNumber)){
                    txtHarfler[a] = String.valueOf(kelimeHarfleri.get(rndHarfNumber));
                    kelimeBilgisi = "";

                    for (int j = 0; j < txtHarfler.length; j++) {
                        if (j < txtHarfler.length - 1)
                            kelimeBilgisi += txtHarfler[j] + " ";
                        else
                            kelimeBilgisi += txtHarfler[j];
                    }
                    break;
                }
            }
            textViewQuest.setText(kelimeBilgisi);
            kelimeHarfleri.remove(rndHarfNumber);
        }
        sonHakSayisi = hakSayisi;
        hakSayisi--;
        kalanHakkiKaydet(hakSayisi,sonHakSayisi);
        }else
            Toast.makeText(getApplicationContext(),"Harf Alabilmek İçin Kalp Sayısı Yetersiz.", Toast.LENGTH_SHORT).show();
    }
    private void kalanHakkiKaydet(int hSayisi, int sonHSayisi){
        try {
            sqlSorgusu ="UPDATE Ayarlar SET k_heart = ? WHERE k_heart =?";
            statement = database.compileStatement(sqlSorgusu);
            statement.bindString(1,String.valueOf(hSayisi));
            statement.bindString(2,String.valueOf(sonHSayisi));
            statement.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void btnTahminEt(View v) {

        textTahminDegeri = editTextTahminDegeri.getText().toString();


        if (!TextUtils.isEmpty(textTahminDegeri)){
                if (textTahminDegeri.matches(rastgeleKelime)) {
                    Toast.makeText(getApplicationContext(), "Tebrikler Doğru Tahminde Bulundunuz.", Toast.LENGTH_SHORT).show();
                    editTextTahminDegeri.setText("");

                    if (kelimelerList.size() > 0)
                        randomKelimeGetir();
                    else {
                        if (sorularList.size() >0)
                            randomSoruGetir();
                        else
                            Toast.makeText(getApplicationContext(), "Sorular Bitti.", Toast.LENGTH_SHORT).show();

                    }

                }else {
                    if (hakSayisi > 0) {
                        sonHakSayisi = hakSayisi;
                        hakSayisi--;
                        Toast.makeText(getApplicationContext(), "Yanlış Tahminde Bulundunuz, Kalp Sayınız Bir Azaldı.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Devam Edebilmek İçin Kalp Sayısı Yetersiz, \nOyun Bitti.", Toast.LENGTH_SHORT).show();

                        new CountDownTimer(1100, 100) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                mainIntent();
                            }
                        }.start();
                    }
                }
        }
    }

    private void mainIntent(){
        Intent mainIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_out_up, R.anim.slide_in_down);
    }

    private void randomSoruGetir(){

        rndSoruNumber = rndSoru.nextInt(sorularKodList.size());
        rastgeleSoru = sorularList.get(rndSoruNumber);
        rastgeleSoruKodu = sorularKodList.get(rndSoruNumber);
        sorularList.remove(rndSoruNumber);
        sorularKodList.remove(rndSoruNumber);

        textViewQuestion.setText(rastgeleSoru);

        try {
            database = this.openOrCreateDatabase("HarfAlayim", MODE_PRIVATE, null);
            cursor = database.rawQuery("SELECT * FROM Kelimeler WHERE kKod = ?", new String[]{rastgeleSoruKodu});

            int kelimeIndex = cursor.getColumnIndex("kelime");

            while (cursor.moveToNext())
                kelimelerList.add(cursor.getString(kelimeIndex));

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
         randomKelimeGetir();
    }
    private void randomKelimeGetir(){

        kelimeBilgisi = "";
        rndKelimeNumber = rndKelime.nextInt(kelimelerList.size());
        rastgeleKelime = kelimelerList.get(rndKelimeNumber);
        kelimelerList.remove(rndKelimeNumber);

        for (int i = 0; i < rastgeleKelime.length(); i++) {
            if (i < rastgeleKelime.length() - 1)
                kelimeBilgisi += "_";
            else
                kelimeBilgisi += "_";
        }

        textViewQuest.setText(kelimeBilgisi);
        System.out.println("Gelen Kelime =" + rastgeleKelime);
        System.out.println("Gelen Kelime Harf Sayısı =" + rastgeleKelime.length());
        kelimeHarfleri = new ArrayList<>();

        for (char harf : rastgeleKelime.toCharArray())
            kelimeHarfleri.add(harf);

        if(rastgeleKelime.length() >= 5 && rastgeleKelime.length() <=7)
            rastgeleBelirlenecekHarfSayisi =1;
        else if (rastgeleKelime.length() >= 8 && rastgeleKelime.length() <=10)
            rastgeleBelirlenecekHarfSayisi = 2;
        else if (rastgeleKelime.length() >= 11 && rastgeleKelime.length() <=14)
            rastgeleBelirlenecekHarfSayisi = 3;
        else if(rastgeleKelime.length() >= 15)
            rastgeleBelirlenecekHarfSayisi = 4;
        else
            rastgeleBelirlenecekHarfSayisi = 0;
    }


}
