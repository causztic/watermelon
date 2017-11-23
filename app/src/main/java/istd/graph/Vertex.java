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