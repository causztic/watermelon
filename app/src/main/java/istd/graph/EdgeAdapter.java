package istd.graph;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
        // Populate the data into the template view using the data object
        edgeName.setText(edge.toString());
        // Return the completed view to render on screen
        return convertView;
    }
}