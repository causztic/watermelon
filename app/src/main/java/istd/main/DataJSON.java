package istd.main;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DataJSON implements Serializable {

    private ArrayList<PlaceJSON> places = new ArrayList<PlaceJSON>();

    DataJSON(ArrayList<PlaceJSON> ls) {
        places = ls;
    }

    public List<PlaceJSON> getPlaceList() {
        return places;
    }

}

class PlaceJSON implements Serializable {

    private String name;
    private String category;
    private double lat;
    private double lng;

    public String getName() {
        return name;
    }

    public String getCategory() { return category; }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

}
