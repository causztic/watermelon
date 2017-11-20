package istd.graph;

/**
 * Created by yaojie on 20/11/17.
 */

public class Vertex {
    private final String name;
    private final double[] latlng;

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