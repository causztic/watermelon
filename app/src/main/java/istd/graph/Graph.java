package istd.graph;

import java.util.ArrayList;
import java.util.List;

import istd.code.Location;

/**
 * Created by yaojie on 20/11/17.
 * Creates a Graph object given a budget, a list of Locations, and the current latlng.
 * Passed into DistanceSolver to solve.
 */

public class Graph {

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

    public Graph(double[] latlng, Location[] locations, int budget){

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
                    edges.add(new Edge(vertex, vertex2, 0,0, MODE.PUBLIC));
                    edges.add(new Edge(vertex, vertex2, 0,0, MODE.TAXI));
                    edges.add(new Edge(vertex, vertex2, 0,0, MODE.WALK));

                    edges.add(new Edge(vertex2, vertex, 0,0, MODE.PUBLIC));
                    edges.add(new Edge(vertex2, vertex, 0,0, MODE.TAXI));
                    edges.add(new Edge(vertex2, vertex, 0,0, MODE.WALK));
                }
            }
            // remove first vertex as it is linked to all other vertices already.
            vertices.remove(vertex);
        }
    }
}
