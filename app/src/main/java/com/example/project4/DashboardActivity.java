package com.example.project4;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


//import com.example.project4.model.LocationViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.project4.databinding.ActivityDashboardBinding;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private ActivityDashboardBinding binding;
    private TextView userDetails;
    SharedPreferences sharedPreferences;
    String name;
    static final int REQUEST_PERMISSIONS = 1;
    static final int REQUEST_VIDEO_CAPTURE = 101;
    private static final int REQUEST_LOCATION_PERMISSIONS = 104;
    private com.example.project4.model.LocationViewModel locationViewModel;


    private String videoUri;

    // edit texts
    EditText tourNameEditText;
    EditText tourDescriptionEditText;


    // buttons
    Button addRecording;
    Button saveTour;
    Button viewTours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getPermissions();

        // initialize variables
        addRecording = findViewById(R.id.add_recording_button);
        saveTour = findViewById(R.id.save_tour_button);
        viewTours = findViewById(R.id.view_tours_button);
        tourNameEditText = findViewById(R.id.tour_name_et);
        tourDescriptionEditText = findViewById(R.id.tour_description_et);


        // initialize location view model
        locationViewModel = new ViewModelProvider(this).get(com.example.project4.model.LocationViewModel.class);

// Observe location data
        locationViewModel.getLocationLiveData().observe(this, location -> {
            if (location != null) {
                updateMapLocation(location);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        checkLocationPermissions();

        // set the welcome text to the user's first name
        userDetails = findViewById(R.id.user_details);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        name = sharedPreferences.getString("_name", "");
        userDetails.setText(String.format("Hi, " + name + "!"));

        // adding recording / audio
        addRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermissions();
                recordVideo(v);
            }
        });

        // saving tours
        saveTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String name = tourNameEditText.getText().toString().trim();
                    String description = tourDescriptionEditText.getText().toString().trim();

                    if (!name.isEmpty() && !description.isEmpty()) {
                            addTour(name, description, videoUri);
                        Toast.makeText(DashboardActivity.this, "Tour saved successfully.", Toast.LENGTH_SHORT).show();
                        Log.d("DASHBOARD_ACTIVITY", "tour added successfully: " + name + ", " + description);

                        // Clear input fields
                        tourNameEditText.setText("");
                        tourDescriptionEditText.setText("");
                        videoUri = null;

                    } else {
                        Toast.makeText(DashboardActivity.this, "Please enter a tour name, description, and record a video.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                    Toast.makeText(DashboardActivity.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // view tours
        viewTours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ToursListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        locationViewModel.startLocationUpdates();
    }




    private void addTour(String name, String description, String videoUri) {
        if (name == null || name.isEmpty() || description == null || description.isEmpty()) {
            Toast.makeText(this, "Name and description cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("DashboardActivity", "Adding Tour: " + name + ", " + description + ", " + videoUri);
        List<Tour> tours = UserFileUtils.loadTours(this);
        tours.add(new Tour(name, description, videoUri));
        UserFileUtils.saveTours(this, tours);
        Log.d("DASHBOARD_ACTIVITY", "Tour added successfully: " + name + ", " + description + ", " + videoUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK && data != null) {
            Uri videoUri = data.getData();
            if (videoUri != null) {
                this.videoUri = videoUri.toString();
                Log.i("DashboardActivity", "Video URI: " + this.videoUri);
                UserFileUtils.saveVideoToInternalStorage(this, videoUri);  // Save the actual Uri object here
                Log.i("DashboardActivity", "Video saved to internal storage");
            } else {
                Log.e("DashboardActivity", "Video URI is null");
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.i("DashboardActivity", "Recording cancelled");
        } else {
            Log.i("DashboardActivity", "Error with recording video");
        }
    }

    public void recordVideo(View view) {
        if(hasCamera()) {
            Log.i("CAMERA_BUTTON", "Camera is detected");
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
        } else {
            Log.i("CAMERA_BUTTON", "No camera detected");
            view.setEnabled(false);
        }
    }

    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            return false;
        }
    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO
                    },
                    REQUEST_PERMISSIONS
            );
        }
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enable location layer on the map (if permissions are granted)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setScrollGesturesEnabledDuringRotateOrZoom(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setAllGesturesEnabled(true);

        // Set map click listener
        mMap.setOnMapClickListener(this);
    }

    private void updateMapLocation(Location location) {
        if (mMap != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();  // Clear existing markers
            mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission is required to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationViewModel.stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationViewModel.startLocationUpdates();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // Add a marker at the clicked location
        mMap.addMarker(new MarkerOptions().position(latLng).title("Custom Marker"));
        Log.d("DashboardActivity", "Marker added at: " + latLng.toString());
    }
}