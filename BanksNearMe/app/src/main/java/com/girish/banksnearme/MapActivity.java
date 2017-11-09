package com.girish.banksnearme;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by girish on 09-10-2017.
 */

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    // Google Map
    private GoogleMap googleMap;
    Double latUser, latBa, longUser, longBa;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        Intent intent = getIntent();
        if(intent != null){
            latUser = intent.getDoubleExtra("user_lat",0.0);
            latBa = intent.getDoubleExtra("ba_lat",0.0);
            longUser = intent.getDoubleExtra("user_long",0.0);
            longBa = intent.getDoubleExtra("ba_long",0.0);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Add a marker in Sydney and move the camera
        LatLng TutorialsPoint = new LatLng(latUser, longUser);
        LatLng TutorialsPoint1 = new LatLng(latBa, longBa);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new
                MarkerOptions().position(TutorialsPoint).title("User Location"));
        googleMap.addMarker(new
                MarkerOptions().position(TutorialsPoint1).title("Destination"));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(latUser, longUser)).zoom(13).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        options.add(TutorialsPoint);
        options.add(TutorialsPoint1);
        Polyline line = googleMap.addPolyline(options);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}