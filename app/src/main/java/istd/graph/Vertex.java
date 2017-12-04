package istd.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import istd.code.FixedLocation;

import static java.util.Collections.*;

/**
 * Created by yaojie on 20/11/17.
 */

public class Vertex {
    private final String name;
    private final double[] latlng;
    private List<Edge> edges;

    Vertex(String name){
        this.name = name;
        this.latlng = new double[2];
        this.edges = new ArrayList<>();
    }

    public double getLatitude(){
        return latlng[0];
    }

    public double getLongitude(){
        return latlng[1];
    }

    public void addEdge(Edge edge){
        this.edges.add(edge);
    }

    public List<Edge> getEdges(){
        return edges;
    }

    // generates an identifier to find the corresponding edge based on the otherVertex it is beside.
    public String getIdentifier(Vertex otherVertex, MODE mode){
        return name + "->" + otherVertex.getName() + "_" + mode;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        else
            return name.equals(((Vertex) obj).name);
    }

    @Override
    public String toString() {
        if (this != null)
            return name.equals("root") ? String.format("%f,%f", latlng[0], latlng[1]) : name;
        else
            return null;
    }
}