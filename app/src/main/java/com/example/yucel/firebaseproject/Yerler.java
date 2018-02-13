package com.example.yucel.firebaseproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;


public class Yerler extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    private TextView mName, mId;
    private TextView mAddress;
    private TextView mAttributions;
    public String adres;
    String yerAdi;

    List<Integer> filterTypes = new ArrayList<Integer>();

    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(38.67050053, 39.21020508), new LatLng(38.67050053, 39.21020508));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yerler);




        mName = (TextView) findViewById(R.id.textView);
        mAddress = (TextView) findViewById(R.id.textView2);
        mAttributions = (TextView) findViewById(R.id.textView3);
        mId = (TextView) findViewById(R.id.textView4);

        Button pickerButton = (Button) findViewById(R.id.pickerButton);

        filterTypes.add(Place.TYPE_CAFE);

        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
            filterTypes.add(Place.TYPE_CAFE);

            Intent intent = intentBuilder.build(Yerler.this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);



        } catch (Exception e) {
            e.printStackTrace();
        }


        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    Intent intent = intentBuilder.build(Yerler.this);




                    startActivityForResult(intent, PLACE_PICKER_REQUEST);




                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this, data);
            filterTypes.add(Place.TYPE_CAFE);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();

            adres = (String) address;
            yerAdi = (String) name;
            //final CharSequence address = place.getId();
            String attributions = (String) place.getAttributions();

            if (attributions == null) {
                attributions = "";
            }
         //   mId.setText("id : " + place.getId() + " adres : " + address);
         //   mName.setText("ad : " + name);
         //   mAddress.setText("oran : " + place.getRating() + " getViewPort :" + place.getViewport());


          //  mAttributions.setText(Html.fromHtml(attributions));

            Intent intent2 = new Intent(Yerler.this, MainActivity.class);
            intent2.putExtra("yerAdi", yerAdi);
            startActivity(intent2);



        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }



    }


}