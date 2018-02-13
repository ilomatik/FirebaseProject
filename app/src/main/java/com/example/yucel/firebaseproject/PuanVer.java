package com.example.yucel.firebaseproject;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PuanVer extends AppCompatActivity {

    private Button puanKaydetButon,kapatButon;
    private TextView textView;
    private RatingBar ratingBar;
    private String adresPuan,puanDb;
    float toplamPuan3=0;
    private String androidId;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puan_ver);
        database= FirebaseDatabase.getInstance();
        puanKaydetButon= (Button) findViewById(R.id.kaydetPuanBtn);
        kapatButon=(Button)findViewById(R.id.kapatButon);
        textView=(TextView)findViewById(R.id.textView5);
        ratingBar=(RatingBar)findViewById(R.id.ratingBar2);
        Bundle adres2= getIntent().getExtras();
        adresPuan=adres2.getString("adresPuan");


        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int)(width/1.2),(int)(height/2));

        textView.setText("Sectiğiniz Puan : 0");
        puanGetir();

        androidId = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

        puanKaydetButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int toplamPuan= (int) ratingBar.getRating();
                puanVer(toplamPuan);
                puanKaydetButon.setEnabled(false);
                ratingBar.setEnabled(false);
                Toast.makeText(PuanVer.this,"Puanınız Kaydedildi",Toast.LENGTH_SHORT).show();
               // textView.setText("Toplam Puan : "+ (toplamPuan3));
            }
        });




    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            textView.setText("Sectiğiniz Puan : "+String.valueOf(ratingBar.getRating()));
            puanGetir();
        }
    });




        kapatButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PuanVer.this,MainActivity.class);
                intent.putExtra("yerAdi", adresPuan);
                startActivity(intent);
            }
        });


    }




    private void puanVer(int puan){

            DatabaseReference myRef = database.getReference("");
            String key=myRef.push().getKey();

            DatabaseReference rfYeni=database.getReference(adresPuan+"/"+"puan/"+androidId);
            rfYeni.setValue(puan);
           // Toast.makeText(PuanVer.this,"puan verildi..",Toast.LENGTH_LONG).show();
    }





    private void puanGetir(){
        DatabaseReference oku = database.getReference(adresPuan);
        oku.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.child("puan").getChildren();
                int i=0;
                for (DataSnapshot key : keys){
                    i++;
                    toplamPuan3=(Float.parseFloat(key.getValue().toString()));
                    if (key.getKey().equals(androidId)){
                        textView.setText("Puan Verildi");
                        puanKaydetButon.setText("Puan Verdiniz");
                        puanKaydetButon.setEnabled(false);
                        ratingBar.setEnabled(false);
                    }else{
                    }
                }
             //  Toast.makeText(PuanVer.this,"toplam key value Puan3:"+toplamPuan3,Toast.LENGTH_LONG).show();


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}
