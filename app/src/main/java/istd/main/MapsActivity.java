package istd.main;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ZoomControls;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //TODO 1.1 - add these instance variables
    private GoogleMap mMap;
    private Marker marker;
    private ZoomControls zoom;
    private Button bMapType;
    private EditText location_tf;
    private Button bSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                    bMapType.setText("Norm");
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                else{
                    bMapType.setText("Sat");
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

        bSearch = findViewById(R.id.Bsearch);
        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = location_tf.getText().toString();
                List<Address> addressList=null;
                if(location!=null &&!location.equals("")){
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try{
                        addressList = geocoder.getFromLocationName(location,1);
                    }catch(IOException ex){
                        ex.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    String locality = address.getLocality();
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    if(marker!=null){
                        marker.remove();
                    }
                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title(locality).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
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
        String sydney = "Sydney";
        List<Address> addressList1 = null;
        // Add a marker in Sydney and move the camera
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        try{
            addressList1 = geocoder.getFromLocationName(sydney,1);
        }catch(IOException ex){
            ex.printStackTrace();
        }
        Address address1 = addressList1.get(0);
        String locality1 = address1.getLocality();
        LatLng latLng1 = new LatLng(address1.getLatitude(),address1.getLongitude());
        marker = mMap.addMarker(new MarkerOptions().position(latLng1).title(locality1).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
    }
}
