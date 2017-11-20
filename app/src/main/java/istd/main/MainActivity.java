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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    // Variables to be accessed by other activities
    public double[] currentLatLng; // {lat, lng}
    public Place[] placeList;

    // Variables used within MainActivity
    private TextView currentLoc;
    private TextView visitingText;
    private List<String> listOfStrings = new ArrayList<>();

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
            }

            // Edit the text field showing the current location.
            currentLoc = findViewById(R.id.CurLocationText);
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            //currentLatLng = new double[] {currentLatitude, currentLongitude};

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


        // Manage the list of entered locations.

        ImageView addLocationButton = findViewById(R.id.EnterLocationButton);
        visitingText = findViewById(R.id.VisitingText);


        // Run a search for matching Places as the user types the desired location.

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                listOfStrings.add(String.valueOf(place.getName()));
                StringBuilder sentence = new StringBuilder();
                for (int i = 0; i < listOfStrings.size(); i++) {

                    // if one word
                    if (listOfStrings.size() == 1) {
                        sentence.append(listOfStrings.get(i));
                        break;
                    }

                    // if not last word
                    if (i != listOfStrings.size()-1) {
                        sentence.append(listOfStrings.get(i));
                        sentence.append(", ");
                    }
                    // if last word
                    else {
                        sentence.append("and ");
                        sentence.append(listOfStrings.get(i));
                    }
                }
                visitingText.setText("Today I'll explore " + sentence.toString() + "!");
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Joel", "An error occurred: " + status);
            }
        });


        addLocationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // add selected verified location to list of locations
            }
        });



        // Update the budget text to reflect the budget slider value.
        SeekBar budgetBar = findViewById(R.id.BudgetBar);
        ((TextView) findViewById(R.id.budgetValue)).setText("$" + Integer.toString(budgetBar.getProgress()));
        budgetBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                int updatedBudget = seekBar.getProgress();
                ((TextView) findViewById(R.id.budgetValue)).setText("$" + Integer.toString(updatedBudget));

                if (updatedBudget == 0) {
                    ((TextView) findViewById(R.id.BudgetBlurb)).setText("Oops. Better luck hitching a ride.");
                }
                if (updatedBudget > 0 && updatedBudget < 51) {
                    ((TextView) findViewById(R.id.BudgetBlurb)).setText("");
                }
                if (updatedBudget == 51) {
                    ((TextView) findViewById(R.id.budgetValue)).setText("∞");
                    ((TextView) findViewById(R.id.BudgetBlurb)).setText("Do consider donating to watermelon's developers.");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Button button = (Button) findViewById(R.id.mapButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

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
}
