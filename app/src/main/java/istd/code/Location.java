package istd.code;

/**
 * Created by yaojie on 20/11/17.
 * A Location for an attraction in Singapore.
 */

public class Location {
    private String name;
    private String category;
    private double lat;
    private double lng;
    private String marker;

    @Override
    public String toString() {
        return String.format("%s [%s]: [%f,%f] - %s", name, category, lat, lng, marker);
    }
}
