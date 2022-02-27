package com.example.fast_aid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * TODO: Find a way to start RequestGPS Activity after GPS is turned off at any given moment
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private LatLng position;
    private boolean positionFound = false;
    private final HashMap<String, Double> mCurrentLocation = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance(FirebaseApp.getInstance());
        reference = database.getReference(FirebaseAuth.getInstance().getUid() + "/" + "CurrentLocation/");

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requireActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            //Required to triangulate last known position of the user. ONLY FOR UI PURPOSES
            if(position == null){
                LocationServices.getFusedLocationProviderClient(requireActivity())
                        .getLastLocation()
                        .addOnSuccessListener(loc -> {
                            position = new LatLng(loc.getLatitude(), loc.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,12));
                        });


            }
            googleMap.setMyLocationEnabled(true);

            FusedLocationProviderClient mLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

            LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(100);
            locationRequest.setFastestInterval(100);
            locationRequest.setSmallestDisplacement(5);

            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    position = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    if (!positionFound) {
                        googleMap.animateCamera(CameraUpdateFactory
                                .newLatLngZoom(position, 18), 300, new GoogleMap.CancelableCallback() {
                            @Override
                            public void onCancel() {
                                
                            }

                            @Override
                            public void onFinish() {

                            }
                        });

                        positionFound = true;

                    }
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    try {

                        Address address = geocoder
                                .getFromLocation(position.latitude, position.longitude, 1)
                                .get(0);
                        ((HomeFragment) getParentFragment()).setAddress(address);
                    } catch (IOException e) {
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    mCurrentLocation.put("Latitude", position.latitude);
                    mCurrentLocation.put("Longitude", position.longitude);
                    reference.setValue(mCurrentLocation);

                }
            };
            mLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }


}