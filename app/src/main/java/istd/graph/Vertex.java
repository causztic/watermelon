package istd.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.*;

/**
 * Created by yaojie on 20/11/17.
 */

public class Vertex {
    private final String name;
    private final double[] latlng;
    private List<Edge> edges;

    Vertex(String name, double[] latlng) {
        this.name = name;
        this.latlng = latlng;
        this.edges = new ArrayList<>();
    }

    Vertex(String name){
        this.name = name;
        this.latlng = null;
        this.edges = new ArrayList<>();
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
        return name.equals("root") ? String.format("%f,%f", latlng[0], latlng[1]) : name;
    }
}