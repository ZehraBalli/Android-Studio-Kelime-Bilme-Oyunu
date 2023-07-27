package com.example.harfalayim;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase database;
    private Cursor cursor;
    private TextView txtUserHeartCount;
    private AlertDialog.Builder alertBuilder;
    private int heartIndex, heartCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtUserHeartCount = (TextView) findViewById(R.id.main_activity_textViewUserHeartCount);

       try {
           database =this.openOrCreateDatabase("HarfAlayim",MODE_PRIVATE,null);
           cursor = database.rawQuery("SELECT k_HEART FROM Ayarlar",null);

           heartIndex = cursor.getColumnIndex("k_heart");
           cursor.moveToFirst();

           heartCount = Integer.valueOf(cursor.getString(heartIndex));
           txtUserHeartCount.setText("+" + heartCount);

           cursor.close();
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private void uygulamadanCik(){
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public void mainBtnClick(View v){
        switch (v.getId()){
            case R.id.main_activity_btn_hemenoyna2:
                Intent playIntent = new Intent(this,PlayActivity.class);
                finish();
                playIntent.putExtra("heartCount",heartCount);
                startActivity(playIntent);
                overridePendingTransition(R.anim.slide_out_up,R.anim.slide_in_down);
                break;

            case R.id.main_activity_btn_market2:
                break;

            case R.id.main_activity_btn_cikis2:
                uygulamadanCik();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        //AlertDialog Açıyorum
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Harf Alayım");

        alert.setMessage("Uygulamadan Çıkmak İstediğinize Emin Misiniz?");
        alert.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uygulamadanCik();
            }
        });

        alert.show();
    }
}