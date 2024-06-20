package com.example.project4;

import java.util.ArrayList;
import java.util.List;
import android.net.Uri;
import android.util.Log;

public class TourUtils {



    public static List<Tour> deserializeTours(String serializedTours) {
        List<Tour> tours = new ArrayList<>();
        if (serializedTours != null && !serializedTours.isEmpty()) {
            Log.d("TourUtils", "Serialized Tours Input: " + serializedTours); // Add this log
            String[] items = serializedTours.split(";");
            for (String item : items) {
                Log.d("TourUtils", "Serialized Tour Item: " + item); // Add this log
                String[] fields = item.split("\\|");
                if (fields.length == 3) {
                    String name = fields[0];
                    String description = fields[1];
                    String video = fields[2];
                    tours.add(new Tour(name, description, video));
                    Log.d("TourUtils", "Deserialized Tour: " + name + ", " + description + ", " + video.toString()); // Add this log
                } else {
                    Log.e("TourUtils", "Invalid Tour Item: " + item); // Log invalid items
                }
            }
        } else {
            Log.e("TourUtils", "Serialized Tours is null or empty"); // Log if input is null or empty
        }
        return tours;
    }

}
