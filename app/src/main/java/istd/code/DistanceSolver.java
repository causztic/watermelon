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
        // The root does not get added into the list.
        root = new Vertex("root", latlng);
        for (Location location: locations){
            vertices.add(new Vertex(location.getName()));
        }

        // for every vertex, link to each other vertex. No other vertex links back to the root.
        for (Vertex vertex: vertices){
            // add an edge for a MODE.
            for (MODE mode: MODE.values()){
                edges.add(new Edge(root, vertex, 0, 0, mode));
            }
        }

        // for all the vertices that are not root, generate a combination of edges per mode.
        for (Vertex vertex: vertices){
            for (Vertex vertex2: vertices){
                if (!vertex.equals(vertex2)){
                    for (MODE mode: MODE.values()){
                        edges.add(new Edge(vertex, vertex2, 0, 0, mode));
                    }
                }
            }
        }
    }
}
