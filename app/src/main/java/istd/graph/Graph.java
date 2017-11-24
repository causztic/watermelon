package istd.graph;

import android.os.AsyncTask;

import com.google.maps.model.Distance;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import istd.code.DistanceSolver;
import istd.code.Location;

/**
 * Created by yaojie on 20/11/17.
 * Creates a Graph object given a budget, a list of Locations, and the current latlng.
 * Passed into DistanceSolver to solve.
 */

public class Graph extends AsyncTask<String, Void, Void> {

    private final String PHANTOM_URL = "http://10.0.2.2:8080/";
    private Vertex root;
    private List<Vertex> vertices;
    private List<Edge> edges;
    private Map<String, Edge> edgeMap; // maps the edge to a specific string for easy lookup
    private int budget;

    public Graph(double[] latlng, List<Location> locations, int budget) throws Exception {

        this.budget = budget;
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.edgeMap = new HashMap<>();

        // create the root vertex and the other locations as vertices.
        root = new Vertex("root", latlng);
        vertices.add(root);
        for (Location location : locations) {
            vertices.add(new Vertex(location.getName()));
        }

        generateAllEdges();

    }

    public Map<String, Edge> getEdgeMap() {
        return edgeMap;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public int getBudget() {
        return budget;
    }

    private void generateAllEdges() {
        this.execute(PHANTOM_URL);
    }

    private Object[] callPhantom(String url, String vertex1, String vertex2, MODE mode) {
        Object[] results = new Object[2];
        HttpURLConnection urlConnection = null;
        String specialMode;

        switch (mode) {
            case PUBLIC:
                specialMode = "pt";
                break;
            case TAXI:
                specialMode = "t";
                break;
            default:
                specialMode = "c";
                break;
        }

        try {
            urlConnection = (HttpURLConnection) new URL(url + "/" + vertex1 + "/" + vertex2 + "/" + specialMode).openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line + "\n");
            }
            System.out.println("Request: " + vertex1 + " to " + vertex2);
            System.out.println("Response: " + builder.toString());
            JSONObject json = new JSONObject(builder.toString());
            results[0] = json.getDouble("price");
            results[1] = json.getInt("time");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return results;
    }

    private void addToEdges(Vertex vertex, Edge edge){
        edges.add(edge);
        edgeMap.put(edge.getIdentifier(), edge);
        vertex.addEdge(edge);

    }

    protected Void doInBackground(String... urls) {
        // for every vertex, link to each other vertex. The result is a graph of undirected edges,
        // as the cost and timing does not change when the direction is reversed.
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);

            for (int j = i+1; j < vertices.size(); j++) {
                Vertex vertex2 = vertices.get(j);

                Object[] publicResults = callPhantom(urls[0], vertex.toString(), vertex2.toString(), MODE.PUBLIC);
                Object[] taxiResults = callPhantom(urls[0], vertex.toString(), vertex2.toString(), MODE.TAXI);
                // create 6 edges. 2 for bidirectional * 3 modes.
                // we don't have accurate walk data, so just multiply the public travel time by 3.

                int publicTravelTime = (int) publicResults[1];
                int taxiTravelTime = (int) taxiResults[1];
                double publicCost = (double) publicResults[0];
                double taxiCost = (double) taxiResults[0];

                int walkingTravelTime = publicTravelTime * 3;

                // call watermelon-phantom to update cost and travelTime.
                if (publicCost < budget) {
                    // only add edges if the budget at least allows this mode of transport.
                    addToEdges(vertex, new Edge(vertex, vertex2, publicTravelTime, publicCost, MODE.PUBLIC));
                    if (!vertex.getName().equals("root"))
                        addToEdges(vertex2, new Edge(vertex2, vertex, publicTravelTime, publicCost, MODE.PUBLIC));
                }

                if (taxiCost < budget) {
                    addToEdges(vertex, new Edge(vertex, vertex2, taxiTravelTime, taxiCost, MODE.TAXI));
                    if (!vertex.getName().equals("root"))
                        addToEdges(vertex2, new Edge(vertex2, vertex, taxiTravelTime, taxiCost, MODE.TAXI));
                }

                addToEdges(vertex, new Edge(vertex, vertex2, walkingTravelTime, 0, MODE.WALK));
                if (!vertex.getName().equals("root"))
                    addToEdges(vertex2, new Edge(vertex2, vertex, walkingTravelTime, 0, MODE.WALK));

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // call distance solver to solve.
        DistanceSolver ds = new DistanceSolver();
        List<Edge> mostEfficient = ds.bruteForce(this);
        List<Edge> mostEfficient2 = ds.smartSolve(this);
        System.out.println("Most efficient: " + Arrays.toString(mostEfficient.toArray()));
        System.out.println("Most efficient smart: " + Arrays.toString(mostEfficient2.toArray()));
    }
}
