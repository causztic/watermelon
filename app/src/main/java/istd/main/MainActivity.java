package istd.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.net.http.HttpResponseCache;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import java.io.File;

import istd.code.FixedLocation;
import istd.code.FixedLocationFactory;

public class MainActivity extends AppCompatActivity {

    // TODO: These are the variables to be accessed by other Activities.
    public static ArrayList<FixedLocation> locationArrayList = new ArrayList<>();
    public static int updatedBudget;

    // Variables used within MainActivity
    private TextView currentLoc;
    private ArrayList<String> locationStringList = new ArrayList<>();
    private ArrayList<String> autocompleteList = new ArrayList<>();
    private String jsonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Change status bar color for SDK21 and above.
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        // Do the location check.
        if (!isLocationEnabled()) {
            // Alert the user to turn on location services.
            showAlert();
        } else {

            // Do a bunch of location stuff
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }

                @Override
                public void onProviderEnabled(String s) {
                }

                @Override
                public void onProviderDisabled(String s) {
                }
            };

            // Check permissions in manifest for accessing location
            // This is required to avoid a crash in case of null return when no permissions
            if (locationManager != null) {
                int MY_PERMISSIONS = 0;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS);
                    return;
                }

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }

            // (Hacky) Get last location from GPS, if failed, then NETWORK.
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location == null) {
                    // if all fail, default to changi airport
                    location = new Location("");
                    location.setLatitude(1.3568);
                    location.setLongitude(103.9891);
                }
            }

            locationArrayList.add(FixedLocationFactory.createFixedLocationFromLocation(location));

            // Edit the text field showing the current location.
            currentLoc = findViewById(R.id.CurLocationText);
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
                if (addressList != null) {
                    String confirmedAddress = addressList.get(0).getAddressLine(0);
                    currentLoc.setText(confirmedAddress);

                } else {
                    currentLoc.setText("Unlisted location");
                }

            } catch (IOException e) {
                // Must catch somehow.
                e.printStackTrace();
            }
        }

        // Run a search for matching names as the user types the desired location.
        // Open the JSON and parse
        Gson gson = new Gson();
        final FixedLocation[] temp = FixedLocationFactory.createLocations(this.getApplicationContext());

        // Update both autocompleteList and locationArrayList with the places picked.
        for (FixedLocation fl: temp) {
            autocompleteList.add(fl.getName());
        }

        // Display the JSON places as autocomplete values
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, autocompleteList);
        final AutoCompleteTextView visitingText = findViewById(R.id.WhereTo);
        visitingText.setAdapter(autoCompleteAdapter);

        visitingText.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String newPlace = (String) adapterView.getItemAtPosition(i);
                locationStringList.add(newPlace);
                ((TextView) findViewById(R.id.PlacesText)).setText(createPlaceMessage(locationStringList));

                // Search for matching string
                for (FixedLocation fl: temp) {
                    if (fl.getName() == newPlace) {
                        locationArrayList.add(fl);
                        break;
                    }
                }
                visitingText.setText("");
            }
        });

        // Update the budget text to reflect the budget slider value.
        SeekBar budgetBar = findViewById(R.id.BudgetBar);
        ((TextView) findViewById(R.id.budgetValue)).setText("$" + Integer.toString(budgetBar.getProgress()));
        budgetBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                updatedBudget = seekBar.getProgress();
                ((TextView) findViewById(R.id.budgetValue)).setText("$" + Integer.toString(updatedBudget));

                if (updatedBudget == 0) {
                    ((TextView) findViewById(R.id.BudgetBlurb)).setText("Oops. Better luck hitching a ride.");
                    Toast.makeText(getBaseContext(), "Oops. Better luck hitching a ride.", Toast.LENGTH_SHORT).show();
                }
                if (updatedBudget > 0 && updatedBudget < 51) {
                    ((TextView) findViewById(R.id.BudgetBlurb)).setText("");
                }
                if (updatedBudget == 51) {
                    ((TextView) findViewById(R.id.budgetValue)).setText("∞");
                    ((TextView) findViewById(R.id.BudgetBlurb)).setText("Do consider donating to watermelon's developers.");
                    Toast.makeText(getBaseContext(), "Do consider donating to watermelon's developers.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Button button = findViewById(R.id.mapButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SolverActivity.class);
                MainActivity.this.startActivity(i);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        // to get locations based on json..
        // create a http cache to hold results
        try {
            File httpCacheDir = new File(this.getBaseContext().getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i("", "HTTP response cache installation failed:" + e);
        }

        TextView settingsChanger = findViewById(R.id.SettingsLink);
        settingsChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

    }

    // Check if location enabled?
    private boolean isLocationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    protected void onStop(){
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    // Alert the user to turn on location settings if not on.
    private void showAlert() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        dialog.setTitle("Enable Location")
                .setMessage("\nYour Location is set to 'Off'.\n\nPlease enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }

    private String createPlaceMessage(ArrayList<String> stringArrayList) {

        String base = "";

        if (stringArrayList.size() == 1) {
            base = "Today I'm going to visit " + stringArrayList.get(0) + "!";
        }

        else if (stringArrayList.size() >= 1) {
            base = "Today I'm going to visit ";
            for (int i = 0; i < stringArrayList.size(); i++) {
                if (i != stringArrayList.size() - 1) {
                    base += stringArrayList.get(i) + ", ";
                } else {
                    base += "and " + stringArrayList.get(i) + "!";
                }
            }
        }

        return base;
    }
}
