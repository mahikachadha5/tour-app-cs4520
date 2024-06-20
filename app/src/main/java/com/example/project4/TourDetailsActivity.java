package com.example.project4;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.Nullable;


import java.util.List;

public class TourDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView tourNameTv;
    TextView tourDescriptionTv;
    Button editTour;
    Button shareTour;
    ImageButton backButton;
    VideoView videoView;
    private GoogleMap mMap;
    List<Tour> tours;
    private int position;
    private static final int REQUEST_PERMISSIONS = 103;
    String videoString;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tour_details_activity);

        editTour = findViewById(R.id.edit_tour_button);
        shareTour = findViewById(R.id.share_tour_button);
        backButton = findViewById(R.id.back_button);
        tourNameTv = findViewById(R.id.details_name);
        tourDescriptionTv = findViewById(R.id.details_description);
        videoView = findViewById(R.id.videoView);

        Intent intent = getIntent();
        String tourName = intent.getStringExtra("NAME");
        String tourDescription =  intent.getStringExtra("DESCRIPTION");
        videoString = intent.getStringExtra("VIDEO");
        position = intent.getIntExtra("POSITION", -1);

        Log.i("TourDetailsActivity", "description of tour is " + tourDescription);

        Log.i("TourDetailsActivity", "string of video is " + videoString);

        // set text views to the clicked tour's name and description
        tourNameTv.setText(tourName);
        tourDescriptionTv.setText(tourDescription);


        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        // Check permissions before playing the video
        checkPermissionsAndPlayVideo();


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(TourDetailsActivity.this, EditTourActivity.class);
                editIntent.putExtra("NAME", tourNameTv.getText().toString().trim());
                editIntent.putExtra("DESCRIPTION", tourDescriptionTv.getText().toString().trim());
                editIntent.putExtra("VIDEO", videoString);
                editIntent.putExtra("POSITION", position);
                startActivityForResult(editIntent, 1);
            }
        });

        shareTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = tourNameTv.getText().toString().trim();
                String description = tourDescriptionTv.getText().toString().trim();

                if (name != null && description != null) {
                    String shareMessage = "Tour Name: " + name + "\nDescription: " + description;
                    Log.i("ShareMessage", shareMessage);

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);

                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoString));
                    shareIntent.setType("video/mp4");

                    startActivity(Intent.createChooser(shareIntent, "Share Tour via"));
                } else {
                    Log.i("ShareMessage", "Name or descrition is null");

                }
            }
        });
    }

    private void checkPermissionsAndPlayVideo() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS
            );
        } else {
            playVideo();
        }
    }

    private void playVideo() {

        if (videoString != null && !videoString.isEmpty()) {
            Uri videoUri = Uri.parse(videoString);
            videoView.setVideoURI(videoUri);
            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            mediaController.setAnchorView(videoView);
            videoView.start();
        } else {
            Log.e("TourDetailsActivity", "Video URI is null or empty");
            videoView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                playVideo();
            } else {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String name = data.getStringExtra("NAME");
            String description = data.getStringExtra("DESCRIPTION");
            String video = data.getStringExtra("VIDEO");
            position = data.getIntExtra("POSITION", -1);

            tourNameTv.setText(name);
            tourDescriptionTv.setText(description);

            if (video != null && !video.isEmpty()) {
                Uri videoUri = Uri.parse(video);
                videoView.setVideoURI(videoUri);
                videoView.setZOrderOnTop(true);
                videoView.requestFocus();
                videoView.start();
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("NAME", name);
            resultIntent.putExtra("DESCRIPTION", description);
            resultIntent.putExtra("VIDEO", video);
            resultIntent.putExtra("POSITION", position);
            setResult(RESULT_OK, resultIntent);
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(latLng -> {
            mMap.addMarker(new MarkerOptions().position(latLng).title("New Marker"));
            Log.i("TourDetailsActivity", "Marker added at: " + latLng);
        });
    }
}

