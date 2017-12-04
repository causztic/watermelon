package istd.graph;

import android.support.annotation.NonNull;

/**
 * Created by yaojie on 20/11/17.
 */

public class Edge implements Comparable {
    private final Vertex source;
    private final Vertex destination;
    private final int travelTime; // travelTime in minutes.
    private final double cost; // cost
    private final MODE mode;

    public Vertex getSource() {
        return source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public int getTravelTime() {
        return travelTime;
    }

    public double getCost() {
        return cost;
    }

    public MODE getMode() {
        return mode;
    }

    // Gets the identifier of the Edge. Used in a HashMap for easy lookup.
    public String getIdentifier(){
        return source.getName() + "->" + destination.getName() + "_" + mode;
    }

    Edge(Vertex source, Vertex destination, int travelTime, double cost, MODE mode){
        this.source = source;
        this.destination = destination;
        this.travelTime = travelTime;
        this.mode = mode;
        this.cost = cost;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        else {
            Edge edge = (Edge) obj;
            return source.equals(edge.getSource())  && destination.equals(edge.getDestination()) && mode.equals(edge.getMode());
        }
    }

    @Override
    public String toString() {
        return source + "->" + destination + "(" + mode + ")";
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return Double.compare(this.getTravelTime(), ((Edge)o).getTravelTime());
    }
}