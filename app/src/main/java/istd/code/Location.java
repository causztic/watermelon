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

    public String getName(){
        return name;
    }
    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        else
            return name.equals(((Location) obj).name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }
}
