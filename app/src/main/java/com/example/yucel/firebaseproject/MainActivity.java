package com.example.yucel.firebaseproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText kelime;
    private TextView toplamTv,ustBaslik,yorumSayisiTextView,puanTv;
    private Button kaydet,listeleButon,yerlerBtn;
    private Button puanVerButon;
    private String androidId;
    private float toplamPuanTv,puanVerenSayisi;
    public int yorumSayisi=0;
    private ListView yorumListesi;
    ArrayList<String> yorumlar = new ArrayList();

    String gelenAdres="",puanDb2;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidId = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

        database=FirebaseDatabase.getInstance();

        kelime= (EditText) findViewById(R.id.editText);
        kaydet=(Button)findViewById(R.id.button);
        yerlerBtn= (Button) findViewById(R.id.yerlerBtn);
        toplamTv= (TextView) findViewById(R.id.toplamPuanTv);
        ustBaslik= (TextView) findViewById(R.id.ustBaslik);
        yorumListesi= (ListView) findViewById(R.id.listViewYorum);
        yorumSayisiTextView= (TextView) findViewById(R.id.yorumSayisiTextView);
        puanVerButon= (Button) findViewById(R.id.puanVerButon);
        puanTv= (TextView) findViewById(R.id.puanTv);

        Bundle gelenVeriler=getIntent().getExtras();
        gelenAdres=gelenVeriler.getString("yerAdi");

        ustBaslik.setText(gelenAdres);
        bilgileriGetir();
        puanGetir();



        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //ekle(kelime.getText().toString());

                YorumYap(String.valueOf(kelime.getText()));
                kelime.setText("");
            }
        });




        yerlerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,MenuActivity.class);
                startActivity(intent);
            }
        });



        puanVerButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,PuanVer.class);
                intent.putExtra("adresPuan",gelenAdres);
                startActivity(intent);
            }
        });

    }





    private void YorumYap(String metin){
        if(metin.equals("") || metin==null){
            Toast.makeText(MainActivity.this,"Lütfen boş bırakmayınız..",Toast.LENGTH_LONG).show();
        }else{
            DatabaseReference myRef = database.getReference("");
            //String key=myRef.push().getKey();
            DatabaseReference rfYeni=database.getReference(gelenAdres+"/"+"yorum"+"/"+androidId);
            rfYeni.setValue(metin);
        }
    }


    private void bilgileriGetir(){
        DatabaseReference oku = database.getReference(gelenAdres);
        oku.addValueEventListener(new ValueEventListener() {
            ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, yorumlar);
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yorumlar.clear();
                Iterable<DataSnapshot> keys=dataSnapshot.child("yorum").getChildren();
                int i=0;
                for (DataSnapshot key : keys){
                    i++;
                    if (key.getKey().equals(androidId)){
                        yorumlar.add(i+" . "+key.getValue().toString()+" ( Sizin Yorumunuz )");
                        kelime.setHint("Yorumunuzu Güncelleyiniz");
                        kelime.setText(key.getValue().toString());
                        kaydet.setText("Güncelle");
                    }else{
                        yorumlar.add(i+" . "+key.getValue().toString());
                    }
                }
                yorumListesi.setAdapter(adapter);
                yorumSayisi=i;
                yorumSayisiTextView.setText("Yapılan Yorumlar ("+yorumSayisi+")");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }







    private void puanGetir(){
        DatabaseReference oku = database.getReference(gelenAdres);
        oku.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                Iterable<DataSnapshot> keys=dataSnapshot.child("puan").getChildren();
                toplamPuanTv=0;
                puanVerenSayisi=0;
                for (DataSnapshot key : keys){
                    i++;
                    toplamPuanTv= toplamPuanTv+ Integer.parseInt(key.getValue().toString());
                }
                puanVerenSayisi=i;
                float ortalamaPuan=toplamPuanTv/puanVerenSayisi;
                //Toast.makeText(MainActivity.this,"ort puan: "+ortalamaPuan,Toast.LENGTH_LONG).show();
                puanTv.setText(String.valueOf((new DecimalFormat("##.##").format(ortalamaPuan))));
                toplamTv.setText(String.valueOf((new DecimalFormat("##").format(toplamPuanTv))));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //geri tuşuna basılma durumunu yakalıyoruz
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Ana Ekrana Dönmek İstiyor Musunuz");
            alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    //evet seçilmesi durumunda yapılacak işlemler
                    // finish ile activity'i sonlandırıyoruz.
                    //finish();
                    Intent menuyeGit=new Intent(MainActivity.this,MenuActivity.class);
                    startActivity(menuyeGit);

                }
            });
            alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    //hayır seçildiginde yapılacak işlemler
                }
            });

            alert.show();
        }
        return super.onKeyDown(keyCode, event);
    }





}
