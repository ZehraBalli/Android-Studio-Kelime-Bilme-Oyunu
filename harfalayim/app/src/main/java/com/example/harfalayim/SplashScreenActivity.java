package com.example.harfalayim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;


public class SplashScreenActivity extends AppCompatActivity {
    //Sorular Liste
    private String[] sorularList = {"A harfi ile başlayan iller hangileridir?","Türkiyenin başkenti neresidir?","Mutfakta kullanılan eşyalar nelerdir?"};
    private String[] sorularKodList = {"illerS1","baskentS1","mutfakS1"};
    //Kelimeler Liste
    private String[] kelimelerList ={"Adana","Adıyaman","Afyonkarahisar","Artvin","Amasya","Ankara","Buzdolabı","Rende","Süzgeç","Çamaşır Makinesi","Bulaşık Makinesi","Tencere","Ocak","Masa"};
    private String[] kelimelerKodList = {"illerS1","illerS1","illerS1","illerS1","illerS1","baskentS1","mutfakS1","mutfakS1","mutfakS1","mutfakS1","mutfakS1","mutfakS1","mutfakS1"};

    private ProgressBar mProgress;
    private TextView mTextView;
    private SQLiteDatabase database;
    private Cursor cursor;
    private float maksimumProgres = 100f,artacakProgress,progresMiktarı=0;
    static public HashMap<String,String>sorularHasMap;
    private String sqlSorgusu;
    private SQLiteStatement statement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mProgress = (ProgressBar)findViewById(R.id.splash_screen_activity_progressBar);
        mTextView = (TextView)findViewById(R.id.splash_screen_activity_textViewState);
        sorularHasMap = new HashMap<>();


        try {
            database = this.openOrCreateDatabase("HarfAlayim", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS Ayarlar (k_adi VARCHAR, k_heart VARCHAR, k_image BLOB)");
            cursor = database.rawQuery("SELECT * FROM Ayarlar",null );

            if (cursor.getCount()<1)
                database.execSQL("INSERT INTO Ayarlar (k_heart) VALUES('0')");


            database.execSQL("CREATE TABLE IF NOT EXISTS Sorular (id INTEGER PRIMARY KEY, sKod VARCHAR UNIQUE, soru VARCHAR)");
            database.execSQL("DELETE FROM Sorular");
            sqlSorularıEkle();

            database.execSQL("CREATE TABLE IF NOT EXISTS Kelimeler (kKod VARCHAR, kelime VARCHAR, FOREIGN KEY (kKod) REFERENCES Sorular (sKod))");
            database.execSQL("DELETE FROM Kelimeler");
            sqlKelimeleriEkle();



            cursor = database.rawQuery("SELECT * FROM Sorular",null);
            artacakProgress = maksimumProgres / cursor.getCount();

            int sKodIndex = cursor.getColumnIndex("sKod");
            int soruIndex = cursor.getColumnIndex("soru");

            mTextView.setText("Sorular Yükleniyor...");

            while (cursor.moveToNext()){
                sorularHasMap.put(cursor.getString(sKodIndex),cursor.getString(soruIndex));
                progresMiktarı += artacakProgress;
                mProgress.setProgress((int)progresMiktarı);
            }
            mTextView.setText("Sorular Alındı, Uygulama Başlatılıyor...");
            cursor.close();

            new CountDownTimer(1200,1100){

                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    Intent mainIntent = new Intent(SplashScreenActivity.this,MainActivity.class);
                    finish();
                    startActivity(mainIntent);
                }
            }.start();


        } catch (Exception e) {
            e.printStackTrace();
        }




    }
    private void sqlSorularıEkle(){
        try {
            for (int s = 0; s < sorularList.length; s++) {
                sqlSorgusu = "INSERT INTO Sorular (sKod, soru) VALUES (?,?)";
                statement = database.compileStatement(sqlSorgusu);
                statement.bindString(1,sorularKodList[s]);
                statement.bindString(2,sorularList[s]);
                statement.execute();
            }

        }catch (Exception e){
            e.printStackTrace();

        }

    }
    private void sqlKelimeleriEkle(){
        try {
            for (int k = 0; k < kelimelerList.length; k++){
                sqlSorgusu = "INSERT INTO Kelimeler(kKod, kelime) VALUES(?,?)";
                statement = database.compileStatement(sqlSorgusu);
                statement.bindString(1,kelimelerKodList[k]);
                statement.bindString(2,kelimelerList[k]);
                statement.execute();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}