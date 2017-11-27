package istd.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataJSON implements Serializable {

    private List<PlaceJSON> places = new ArrayList<PlaceJSON>();

    public List<PlaceJSON> getPlaceList() {
        return places;
    }

}

class PlaceJSON implements Serializable {

    private String name;
    private double lat;
    private double lng;

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

}
