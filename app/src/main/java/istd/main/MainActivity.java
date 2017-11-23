package istd.main;

import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import istd.code.DistanceSolver;
import istd.code.Location;
import istd.code.LocationFactory;
import istd.graph.Graph;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button button = (Button) findViewById(R.id.mapButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        // to get locations based on json..
        Location[] locations = LocationFactory.createLocations(getApplicationContext());
        double[] latlng = {1.3732980,103.9608250};

        // create a http cache to hold results
        try {
            File httpCacheDir = new File(this.getBaseContext().getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i("", "HTTP response cache installation failed:" + e);
        }

        try {
            Graph graph = new Graph(latlng, Arrays.asList(locations).subList(1, 3), 10 );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop(){
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            this.startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
