package com.example.yucel.firebaseproject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;
    double latitude=38.67050053, longitude= 39.21020508;
    double end_latitude, end_longitude;

    private static final int PLACE_PICKER_REQUEST = 1;
    private TextView mName, mId;
    private TextView mAddress;
    private TextView mAttributions,mesafeTv,zamanTv;
    public String adres;
    private Button git,mesafeZamanBtn;
    LatLng ilkKonum;
    String yerAdi;
    List<Integer> filterTypes = new ArrayList<Integer>();
    ArrayList<LatLng> markerPoints;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(38.67050053, 39.21020508), new LatLng(38.67050053, 39.21020508));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        git=(Button)findViewById(R.id.git);
        mesafeZamanBtn=(Button)findViewById(R.id.mesafeZamanBtn);
        markerPoints = new ArrayList<LatLng>();
       // zamanTv=(TextView)findViewById(R.id.zamanTv);
       // mesafeTv=(TextView)findViewById(R.id.mesafeTv);

       // mesafeZamanBtn.setVisibility();
        mesafeZamanBtn.setVisibility(View.INVISIBLE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MenuActivity.this);




        git.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                markerPoints.set(0,ilkKonum);
                if(markerPoints.size() >= 2){

                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }


/*
                Object dataTransfer[] = new Object[2];
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(MenuActivity.this);
                String url = null;

                dataTransfer = new Object[3];
                String url4;
                url4= getDirectionsUrl();
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url4;
                dataTransfer[2] = new LatLng(end_latitude, end_longitude);
                getDirectionsData.execute(dataTransfer);*/
            }
        });





    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();


        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(MenuActivity.this);
        String url = null;


        if (id == R.id.kafeler) {

            mMap.clear();
            String hospital = "cafe";
            url = getUrl(latitude, longitude, hospital);
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;
            getNearbyPlacesData.execute(dataTransfer);
            Toast.makeText(MenuActivity.this, "Yakınınızdaki Kafeler", Toast.LENGTH_SHORT).show();



        } else if (id == R.id.restaurant) {

            mMap.clear();
            String hospital = "restaurant";
            url = getUrl(latitude, longitude, hospital);
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;
            getNearbyPlacesData.execute(dataTransfer);
            Toast.makeText(MenuActivity.this, "Yakınınızdaki Restaurantlar", Toast.LENGTH_SHORT).show();


        } else if (id == R.id.konaklama) {

            mMap.clear();
            String hospital = "lodging";
            url = getUrl(latitude, longitude, hospital);
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;
            getNearbyPlacesData.execute(dataTransfer);
            Toast.makeText(MenuActivity.this, "Yakınınızdaki Konaklama Merkezleri", Toast.LENGTH_SHORT).show();


        }  else if (id == R.id.otopark) {

            mMap.clear();
            String hospital = "parking";
            url = getUrl(latitude, longitude, hospital);
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;
            getNearbyPlacesData.execute(dataTransfer);
            Toast.makeText(MenuActivity.this, "Yakınınızdaki Otopark Alanları", Toast.LENGTH_SHORT).show();


        } else if (id == R.id.muzeler) {

            mMap.clear();
            String hospital = "museum";
            url = getUrl(latitude, longitude, hospital);
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;
            getNearbyPlacesData.execute(dataTransfer);
            Toast.makeText(MenuActivity.this, "Yakınınızdaki Müzeler", Toast.LENGTH_SHORT).show();


        } else if (id == R.id.hastane) {

            mMap.clear();
            String hospital = "hospital";
            url = getUrl(latitude, longitude, hospital);
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;
            getNearbyPlacesData.execute(dataTransfer);
            Toast.makeText(MenuActivity.this, "Yakınınızdaki Sağlık Merkezleri", Toast.LENGTH_SHORT).show();


        } else if (id == R.id.otobusduraklari) {

            mMap.clear();
            String hospital = "bus_station";
            url = getUrl(latitude, longitude, hospital);
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;
            getNearbyPlacesData.execute(dataTransfer);
            Toast.makeText(MenuActivity.this, "Yakınınızdaki Otobüs Durakları", Toast.LENGTH_SHORT).show();


        } else if (id == R.id.arama) {
            try {
                PlacePicker.IntentBuilder intentBuilder =
                        new PlacePicker.IntentBuilder();
                intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                filterTypes.add(Place.TYPE_CAFE);
                Intent intent = intentBuilder.build(MenuActivity.this);
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
            }



        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private String getDirectionsUrl()
    {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+latitude+","+longitude);
        googleDirectionsUrl.append("&destination="+end_latitude+","+end_longitude);
        googleDirectionsUrl.append("&key="+"AIzaSyCAcfy-02UHSu2F6WeQ1rhQhkCr51eBL9g");

        return googleDirectionsUrl.toString();
    }


    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyBP23oPU1T6vPWGTlHnfHCHvun6zNIN6yM");

        Log.d("MainActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }






        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if(markerPoints.size()>1){
                    markerPoints.clear();
                   // mMap.clear();
                    markerPoints.add(marker.getPosition());
                }

                // Adding new item to the ArrayList
                markerPoints.add(marker.getPosition());

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(marker.getPosition());

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if(markerPoints.size()==2){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                }


                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured





                return false;
            }
        });






        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                mesafeZamanBtn.setVisibility(View.INVISIBLE);

                if(markerPoints.size()>1){
                    markerPoints.clear();
                    mMap.clear();
                    markerPoints.add(latLng);
                }

                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
               if(markerPoints.size()==2){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }


                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured

            }
        });





    }


    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(MenuActivity.this).addOnConnectionFailedListener(MenuActivity.this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;



        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(15));

       ilkKonum= latLng;
        markerPoints.add(latLng);
        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, (LocationListener) this);
        }
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {

    }


    public void onProviderEnabled(String s) {

    }


    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, (LocationListener) this);
        }
    }


    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
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

            Intent intent2 = new Intent(MenuActivity.this, MainActivity.class);
            intent2.putExtra("yerAdi", yerAdi);
            startActivity(intent2);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }












    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            //Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";



            if(result.size()<1){
                Toast.makeText(getBaseContext(), "Seçim Yapılmadı", Toast.LENGTH_SHORT).show();
                return;
            }


            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){	// Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);

            }

         //   tvDistanceDuration.setText("Distance:"+distance + ", Duration:"+duration);
                //Toast.makeText(MenuActivity.this,distance+"-"+duration,Toast.LENGTH_LONG).show();
            // Drawing polyline in the Google Map for the i-th route
          //  mesafeTv.setText("Uzaklık : "+ distance);
          //  zamanTv.setText("Zaman : "+ duration);
            mesafeZamanBtn.setVisibility(View.VISIBLE);
            String mesafeZamanBilgisi="Uzaklık : "+distance+"\n"+"Zaman   : "+duration;
            mesafeZamanBtn.setText(mesafeZamanBilgisi);
            mMap.addPolyline(lineOptions);
        }
    }





















}
