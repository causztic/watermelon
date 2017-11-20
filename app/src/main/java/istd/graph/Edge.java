package istd.graph;

/**
 * Created by yaojie on 20/11/17.
 */

public class Edge {
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