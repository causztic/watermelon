package istd.code;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yaojie on 20/11/17.
 * Creates a list of Locations based on JSON data.
 */

public class LocationFactory {
    public static Location[] createLocations(Context c){
        try {
            InputStream is = c.getAssets().open("data.json");
            Gson gson = new Gson();
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            Location[] locations = gson.fromJson(json, Location[].class);
            // add marker data here into the array of locations
            return locations;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
