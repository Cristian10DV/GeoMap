package com.example.dam.geomap;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private static final String TAG = "xyzyx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        //recoger datos
        String latitud = getIntent().getStringExtra("latitud");
        String longitud = getIntent().getStringExtra("longitud");

        if (latitud != "" && longitud != ""){
            Log.v(TAG, "Ubicacion de tal dia, latitud: " + latitud + " longitud: " + longitud);
            this.googleMap = googleMap;
            LatLng granada = new LatLng(Double.valueOf(latitud),Double.valueOf(longitud));
            this.googleMap.addMarker(new MarkerOptions().position(granada).title("IZV"));
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(granada));
            this.googleMap.moveCamera(CameraUpdateFactory.zoomTo(17));
            Polyline polyLinea = googleMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(37.1608, -3.5911),
                            new LatLng(37.1618, -3.5911),
                            new LatLng(37.1620, -3.5926),
                            new LatLng(37.1628, -3.5926)));
        }else {
            //no llegan los valores
            Log.v(TAG, "No han llegado los valores por lo tanto se quedan los de por defecto");

            this.googleMap = googleMap;
            LatLng granada = new LatLng(37.1608,3.5911);
            this.googleMap.addMarker(new MarkerOptions().position(granada).title("IZV"));
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(granada));
            this.googleMap.moveCamera(CameraUpdateFactory.zoomTo(17));
            Polyline polyLinea = googleMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(37.1608, -3.5911),
                            new LatLng(37.1618, -3.5911),
                            new LatLng(37.1620, -3.5926),
                            new LatLng(37.1628, -3.5926)));
        }

    }


}
