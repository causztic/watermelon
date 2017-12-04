package istd.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import java.util.List;

import istd.code.FixedLocation;
import istd.graph.Graph;

public class SolverActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solver);
        progressBar = findViewById(R.id.progressBar);
        List<FixedLocation> locations = MainActivity.locationArrayList;
        try {
            Graph graph = new Graph(locations, MainActivity.updatedBudget, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
