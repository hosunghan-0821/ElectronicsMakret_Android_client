package com.example.electronicsmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Activity_location_map extends AppCompatActivity implements OnMapReadyCallback {



    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Double latitude,longitude;
    private String locationName,locationAddress;
    private ImageView backImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        Intent intent = getIntent();


        backImage=findViewById(R.id.post_read_location_map_back_arrow);
        latitude=intent.getDoubleExtra("latitude",0);
        longitude=intent.getDoubleExtra("longitude",0);
        locationName=intent.getStringExtra("locationName");
        locationAddress=intent.getStringExtra("locationAddress");

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.post_location_map);

        mapFragment.getMapAsync(this);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng location = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(locationName);
        markerOptions.snippet(locationAddress);
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,17));
    }
}