package com.example.project4.model;

import android.app.Application;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationViewModel extends AndroidViewModel {
    private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>();
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationCallback locationCallback;

    public LocationViewModel(@NonNull Application application) {
        super(application);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(application);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    locationLiveData.setValue(location);
                }
            }
        };
    }

    public LiveData<Location> getLocationLiveData() {
        return locationLiveData;
    }

    public void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (SecurityException e) {
            // Handle exception (permissions not granted)
        }
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void getLastKnownLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        locationLiveData.setValue(location);
                    }
                }
            });
        } catch (SecurityException e) {
            // Handle exception (permissions not granted)
        }
    }
}
