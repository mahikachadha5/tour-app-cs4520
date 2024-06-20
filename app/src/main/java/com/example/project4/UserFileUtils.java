package com.example.project4;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserFileUtils {

    private static final String USER_PREFS = "UserPrefs";
    private static final String TOURS_FILE_NAME = "tours.txt";

    public static String getCurrentUsername(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString("currentUsername", "");
    }

    public static File getUserDirectory(Context context, String username) {
        File userDir = new File(context.getFilesDir(), username);
        if (!userDir.exists()) {
            userDir.mkdir();
        }
        return userDir;
    }

    public static void saveTours(Context context, List<Tour> tours) {
        String serializedTours = serializeTours(tours);
        String username = getCurrentUsername(context);
        if (username.isEmpty()) {
            Toast.makeText(context, "Username not found. Cannot save tours.", Toast.LENGTH_SHORT).show();
            return;
        }
        File userDir = getUserDirectory(context, username);
        File tourFile = new File(userDir, TOURS_FILE_NAME);
        try (FileOutputStream fos = new FileOutputStream(tourFile)) {
            fos.write(serializedTours.getBytes());
            fos.flush();
            Log.d("UserFileUtils", "Tours saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("UserFileUtils", "Error saving tours", e);
            Toast.makeText(context, "Error saving tours. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public static List<Tour> loadTours(Context context) {
        List<Tour> tours = new ArrayList<>();
        String username = getCurrentUsername(context);
        if (username.isEmpty()) {
            Toast.makeText(context, "Username not found. Cannot load tours.", Toast.LENGTH_SHORT).show();
            return tours;
        }
        File userDir = getUserDirectory(context, username);
        File tourFile = new File(userDir, TOURS_FILE_NAME);

        if (!tourFile.exists()) {
            try {
                tourFile.createNewFile();
                Log.d("UserFileUtils", "Tours file created: " + tourFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("UserFileUtils", "Error creating tours file", e);
                Toast.makeText(context, "Error creating tours file. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }

        try (FileInputStream fis = new FileInputStream(tourFile)) {
            byte[] data = new byte[(int) fis.available()];
            fis.read(data);
            String serializedTours = new String(data);
            tours = deserializeTours(serializedTours);
            Log.d("UserFileUtils", "Tours loaded: " + tours.size());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("UserFileUtils", "Error loading tours", e);
        }
        return tours;
    }

    public static void saveVideoToInternalStorage(Context context, Uri videoUri) {
        String username = getCurrentUsername(context);
        if (username.isEmpty()) {
            Toast.makeText(context, "Username not found. Cannot save video.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (videoUri == null) {
            Log.e("UserFileUtils", "Video URI is null, cannot save video.");
            return;
        }

        File userDir = getUserDirectory(context, username);
        String fileName = "video_" + System.currentTimeMillis() + ".mp4";
        File videoFile = new File(userDir, fileName);

        Log.i("UserFileUtils", "file string name is " + fileName);
        Log.i("UserFileUtils", "VIDEO directory: " + userDir);
        Log.i("UserFileUtils", "VIDEO file: " + videoFile);

        try (InputStream inputStream = context.getContentResolver().openInputStream(videoUri);
             OutputStream outputStream = new FileOutputStream(videoFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            Log.i("UserFileUtils", "Video was saved.");
            Toast.makeText(context, "Video saved successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("UserFileUtils", "Failed to save video.");
        }
    }



    public static String serializeTours(List<Tour> tours) {
        StringBuilder sb = new StringBuilder();
        for (Tour tour : tours) {
            String videoUri = tour.getVideoUri() != null ? tour.getVideoUri().toString() : "";
            sb.append(tour.getName()).append("|")
                    .append(tour.getDescription()).append("|")
                    .append(videoUri).append(";");
            Log.d("UserFileUtils", "Serializing Tour: " + tour.getName() + ", " + tour.getDescription() + ", " + videoUri);

        }
        String serializedTours = sb.toString();
        Log.d("UserFileUtils", "Serialized Tours: " + serializedTours);
        return serializedTours;
    }

    private static List<Tour> deserializeTours(String serializedTours) {
        List<Tour> tours = new ArrayList<>();
        if (serializedTours != null && !serializedTours.isEmpty()) {
            Log.d("UserFileUtils", "Serialized Tours Input: " + serializedTours);
            String[] items = serializedTours.split(";");
            for (String item : items) {
                Log.d("UserFileUtils", "Serialized Tour Item: " + item);
                String[] fields = item.split("\\|");
                if (fields.length == 3) {
                    String name = fields[0];
                    String description = fields[1];
                    String video = fields[2];
                    tours.add(new Tour(name, description, video));
                    Log.d("UserFileUtils", "Deserialized Tour: " + name + ", " + description + ", " + video);
                } else {
                    Log.e("UserFileUtils", "Invalid Tour Item: " + item);
                }
            }
        } else {
            Log.e("UserFileUtils", "Serialized Tours is null or empty");
        }
        return tours;
    }
}
