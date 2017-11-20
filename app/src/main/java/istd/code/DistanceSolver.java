package istd.code;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yaojie on 20/11/17.
 * Distance solver given a budget, a list of Locations, and the current latlng.
 */

public class DistanceSolver {

    // Implementation of Djikstra's Algorithm.
    class Vertex {
        private final String name;
        private final double[] latlng; // unused if there is a name. only for current location.

        Vertex(String name, double[] latlng) {
            this.name = name;
            this.latlng = latlng;
        }

        Vertex(String name){
            this.name = name;
            this.latlng = null;
        }

        @Override
        public boolean equals(Object obj) {
            if (getClass() != obj.getClass())
                return false;
            else
                return name.equals(((Vertex) obj).name);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    class Edge {
        private final Vertex source;
        private final Vertex destination;
        private final int travelTime; // travelTime in minutes.
        private final double cost; // cost
        private final MODE mode;

        Edge(Vertex source, Vertex destination, int travelTime, double cost, MODE mode){
            this.source = source;
            this.destination = destination;
            this.travelTime = travelTime;
            this.mode = mode;
            this.cost = cost;
        }

        @Override
        public String toString() {
            return source + " " + destination;
        }
    }

    private Vertex root;
    private List<Vertex> vertices;
    private List<Edge> edges;

    public DistanceSolver(double[] latlng, Location[] locations, int budget){

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
