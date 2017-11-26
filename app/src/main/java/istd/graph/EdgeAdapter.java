package istd.graph;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import istd.main.R;

/**
 * Created by yaojie on 26/11/17.
 */

public class EdgeAdapter extends ArrayAdapter<Edge> {
    public EdgeAdapter(Context context, List<Edge> edges) {
        super(context, 0, edges);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Edge edge = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_edge, parent, false);
        }
        // Lookup view for data population
        TextView edgeName = (TextView) convertView.findViewById(R.id.edgeName);
        ImageView edgeMode = (ImageView) convertView.findViewById(R.id.edgeMode);
        // Populate the data into the template view using the data object
        switch (edge.getMode()){
            case PUBLIC:
                edgeMode.setImageResource(R.drawable.ic_directions_bus_black_24dp);
                break;
            case TAXI:
                edgeMode.setImageResource(R.drawable.ic_local_taxi_black_24dp);
                break;
            case WALK:
                edgeMode.setImageResource(R.drawable.ic_directions_walk_black_24dp);
                break;
            default:
                System.out.println("should not happen");
                break;
        }
        edgeMode.setColorFilter(R.color.colorPrimary);

        edgeName.setText(edge.getDestination().getName() + " (" + edge.getTravelTime() + "mins)");
        // Return the completed view to render on screen
        return convertView;
    }
}