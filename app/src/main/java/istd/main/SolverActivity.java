package istd.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import java.util.Arrays;

import istd.code.Location;
import istd.code.LocationFactory;
import istd.graph.Graph;

public class SolverActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solver);
        progressBar = findViewById(R.id.progressBar);
        double[] latlng = {1.3732980,103.9608250};
        Location[] locations = LocationFactory.createLocations(getApplicationContext());
        try {
            Graph graph = new Graph(latlng, Arrays.asList(locations).subList(1, 3), 10, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
