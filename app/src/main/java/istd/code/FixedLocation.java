package istd.code;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yaojie on 20/11/17.
 * A Location for an attraction in Singapore.
 */

public class FixedLocation{
    private String name;
    private String category;
    private double lat;
    private double lng;
    private String marker;

    public String getName() {
        return name;
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

    @Override
    public String toString() {
        return String.format("%s [%s]: [%f,%f] - %s", name, category, lat, lng, marker);
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        else
            return name.equals(((FixedLocation) obj).name);
    }
}
