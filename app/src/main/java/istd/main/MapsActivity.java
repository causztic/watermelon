package istd.main;


import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jsoup.Jsoup;
import org.jsoup.select.Collector;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import istd.code.Location;
import istd.code.LocationFactory;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Marker marker;
    private ZoomControls zoom;
    private Button bMapType;
    private EditText location_tf;
    private Button bSearch;
    private Location[] locationsArray;
    private HashMap<String,Location> lStringHashMap;
    private Location address;
    private String encoding = "UTF-8";
    private String location ="";
    private MarkerOptions markerOptions;
    private TextView snippet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationsArray = LocationFactory.createLocations(MapsActivity.this);
        lStringHashMap = new HashMap<>();
        for (int i = 0; i<locationsArray.length;i++){
            lStringHashMap.put(locationsArray[i].getName(),locationsArray[i]);
        }

        location_tf = findViewById(R.id.TFaddress);

        zoom = findViewById(R.id.zoom);
        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
        zoom.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        bMapType = findViewById(R.id.Bmaptype);
        bMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap.getMapType()==GoogleMap.MAP_TYPE_NORMAL){
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                else{
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

        bSearch = findViewById(R.id.Bsearch);
        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textInput = location_tf.getText().toString();
                //String score =  FuzzySearch.extractSorted(textInput,lStringHashMap.keySet()).toString();
                List<ExtractedResult> extractedResultList = FuzzySearch.extractSorted(textInput,lStringHashMap.keySet(),60);

                if(!extractedResultList.isEmpty()){
                    ExtractedResult extractedResult = extractedResultList.get(0);
                    location = extractedResult.getString();

                    address = lStringHashMap.get(location);
                    LatLng latLng = new LatLng(address.getLat(),address.getLng());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    markerOptions = new MarkerOptions().position(latLng).title(address.getName());
                    markerOptions.snippet("");

                    new GetWikiTask().execute();

                    if (marker != null)
                        marker.remove();

                    switch (address.getCategory()){
                        case "nature":
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_terrain_black_24dp));
                            break;
                        case "worship":
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_brightness_7_black_24dp));
                            break;
                        case "museum":
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_account_balance_black_24dp));
                            break;
                        case "party":
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_local_bar_black_24dp));
                            break;
                        case "arts":
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_palette_black_24dp));
                            break;
                        case "food":
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_local_dining_black_24dp));
                            break;
                        case "attraction":
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_local_see_black_24dp));
                            break;
                    }
                    marker = mMap.addMarker(markerOptions);
                }
                else{
                    List<Address> addressList = null;
                    if (textInput!=null&&!textInput.isEmpty()){
                        Geocoder geocoder = new Geocoder((MapsActivity.this));
                        try {
                            addressList = geocoder.getFromLocationName(textInput,1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Address address = addressList.get(0);
                        //Log.i("yf",address.toString());
                        String name = address.getFeatureName();
                        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        if (address.getCountryName().equals("Singapore")){
                            if(marker!=null){
                                marker.remove();
                            }
                            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(name).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_local_see_black_24dp)));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                        }
                        else{
                            Toast.makeText(MapsActivity.this, "Please enter a location in Singapore",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String singapore = "Singapore";
        List<Address> addressList1 = null;
        // Add a marker in Sydney and move the camera
        if (mMap!=null){
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v =  getLayoutInflater().inflate(R.layout.info_nature,null);

                    ImageView img = v.findViewById(R.id.info_nature);
                    TextView name = v.findViewById(R.id.nature_name);
                    snippet = v.findViewById(R.id.nature_snippet);
                    if (address!=null) {
                        if (address.getCategory().equals("nature")){
                            img.setImageResource(R.mipmap.ic_terrain_black_24dp);
                        }
                        if (address.getCategory().equals("worship")){
                            img.setImageResource(R.mipmap.ic_brightness_7_black_24dp);
                        }
                        if (address.getCategory().equals("museum")){
                            img.setImageResource(R.mipmap.ic_account_balance_black_24dp);
                        }
                        if (address.getCategory().equals("party")){
                            img.setImageResource(R.mipmap.ic_local_bar_black_24dp);
                        }
                        if (address.getCategory().equals("arts")){
                            img.setImageResource(R.mipmap.ic_palette_black_24dp);
                        }
                        if (address.getCategory().equals("food")){
                            img.setImageResource(R.mipmap.ic_local_dining_black_24dp);
                        }
                        if (address.getCategory().equals("attraction")){
                            img.setImageResource(R.mipmap.ic_local_see_black_24dp);
                        }
                    }
                    name.setText(marker.getTitle());
                    snippet.setText(marker.getSnippet());
                    return  v;
                }
            });
        }
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        try{
            addressList1 = geocoder.getFromLocationName(singapore,1);
        }catch(IOException ex){
            ex.printStackTrace();
        }
        Address address1 = addressList1.get(0);
        String locality1 = address1.getLocality();
        LatLng latLng1 = new LatLng(address1.getLatitude(),address1.getLongitude());
        marker = mMap.addMarker(new MarkerOptions().position(latLng1).title(locality1));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1,15));
    }

    public class GetWikiTask extends AsyncTask<URL,Void,String>{

        @Override
        protected String doInBackground(URL... urls) {
            try{
                String searchText = location+"Wikipedia";
                org.jsoup.nodes.Document google = Jsoup.connect("https://www.google.com/search?q=" + URLEncoder.encode(searchText, encoding)).userAgent("Mozilla/5.0").get();
                String wikipediaURL = google.getElementsByTag("cite").get(0).text();
                String wikipediaApiJSON = "https://www.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles="
                        + URLEncoder.encode(wikipediaURL.substring(wikipediaURL.lastIndexOf("/") + 1, wikipediaURL.length()), encoding);
                HttpURLConnection httpcon = (HttpURLConnection) new URL(wikipediaApiJSON).openConnection();
                httpcon.addRequestProperty("User-Agent", "Mozilla/5.0");
                BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                String responseSB = in.lines().collect(Collectors.joining());
                in.close();
                String result = responseSB.split("extract\":\"")[1];
                return result;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {

            address = lStringHashMap.get(location);
            LatLng latLng = new LatLng(address.getLat(),address.getLng());

            markerOptions.snippet(result);
            marker.remove();
            marker = mMap.addMarker(markerOptions);
        }
    }
}

