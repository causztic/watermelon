package istd.graph;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import istd.code.Location;

/**
 * Created by yaojie on 20/11/17.
 * Creates a Graph object given a budget, a list of Locations, and the current latlng.
 * Passed into DistanceSolver to solve.
 */

public class Graph {

    private final String PHANTOM_URL = "http://localhost:8080/";
    private Vertex root;
    private List<Vertex> vertices;
    private List<Edge> edges;
    private int budget;

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public int getBudget() {
        return budget;
    }

    private Object[] getPriceAndTime(Vertex v1, Vertex v2, MODE mode) throws IOException, JSONException{
        Object[] results = new Object[2];
        String officialMode;

        switch (mode){
            case TAXI:
                officialMode = "t";
                break;
            case PUBLIC:
                officialMode = "pt";
                break;
            default:
                officialMode = null; // shouldn't reach here.
                break;
        }

        URL phantomUrl = new URL(PHANTOM_URL + v1.toString() + "," + v2.toString() + "," + officialMode);
        HttpURLConnection urlConnection = (HttpURLConnection) phantomUrl.openConnection();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line + "\n");
            }
            System.out.println(builder.toString());
            JSONObject json = new JSONObject(builder.toString());
            results[0] = json.getDouble("price");
            results[1] = json.getInt("time");
        } finally {
            urlConnection.disconnect();
        }
        return results;
    }

    public Graph(double[] latlng, List<Location> locations, int budget) throws IOException, JSONException{

        this.budget = budget;
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();

        // create the root vertex and the other locations as vertices.
        root = new Vertex("root", latlng);
        vertices.add(root);
        for (Location location: locations){
            vertices.add(new Vertex(location.getName()));
        }

        // for every vertex, link to each other vertex. The result is a graph of undirected edges,
        // as the cost and timing does not change when the direction is reversed.
        for (Vertex vertex: vertices){
            for (Vertex vertex2: vertices){
                if (!vertex.equals(vertex2)){ // no edge to itself
                    // create 6 edges. 2 for bidirectional * 3 modes.
                    // we don't have accurate walk data, so just multiply the public travel time by 3.

                    Object[] publicResults = getPriceAndTime(vertex, vertex2, MODE.PUBLIC);
                    Object[] taxiResults = getPriceAndTime(vertex, vertex2, MODE.TAXI);

                    int publicTravelTime = (int) publicResults[1];
                    int taxiTravelTime = (int) taxiResults[1];
                    double publicCost = (double) publicResults[0];
                    double taxiCost = (double) taxiResults[0];

                    int walkingTravelTime = publicTravelTime * 3;

                    // call watermelon-phantom to update cost and travelTime.
                    if (publicCost < budget) {
                        // only add edges if the budget at least allows this mode of transport.
                        edges.add(new Edge(vertex, vertex2, publicTravelTime, publicCost, MODE.PUBLIC));
                        edges.add(new Edge(vertex2, vertex, publicTravelTime, publicCost, MODE.PUBLIC));
                    }

                    if (taxiCost < budget) {
                        edges.add(new Edge(vertex, vertex2, taxiTravelTime, taxiCost, MODE.TAXI));
                        edges.add(new Edge(vertex2, vertex, taxiTravelTime, taxiCost, MODE.TAXI));
                    }

                    edges.add(new Edge(vertex, vertex2, walkingTravelTime,0, MODE.WALK));
                    edges.add(new Edge(vertex2, vertex, walkingTravelTime,0, MODE.WALK));
                }
            }
            // remove first vertex as it is linked to all other vertices already.
            vertices.remove(vertex);
        }
    }
}
