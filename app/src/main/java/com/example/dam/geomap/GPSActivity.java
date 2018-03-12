package com.example.dam.geomap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.AndroidSupport;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class GPSActivity extends AppCompatActivity {

    private static final int PERMISO_LOCATION = 1;
    private static final int RESOLVE_RESULT = 2;
    private static final String TAG = "xyzyx";

    private String latitud;
    private String longitud;

    private FusedLocationProviderClient clienteLocalizacion;
    private LocationCallback callbackLocalizacion;
    private LocationRequest peticionLocalizacion;
    private LocationSettingsRequest ajustesPeticionLocalizacion;
    private SettingsClient ajustesCliente;


    private boolean checkPermissions() {
        int estadoPermisos = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return estadoPermisos == PackageManager.PERMISSION_GRANTED;
    }

    private void init() {
        if(checkPermissions()) {
            //lanzar servicio
            //startService(new Intent(this, LocationService.class));
            startLocations();
        } else {
            requestPermissions();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESOLVE_RESULT:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.v(TAG, "Permiso ajustes localización");
                        startLocations();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.v(TAG, "Sin permiso ajustes localización");
                        break;
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        init();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == PERMISO_LOCATION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocations();
            }
        }
    }

    private void requestPermissions() {
        boolean solicitarPermiso = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (solicitarPermiso) {
            Log.v(TAG, "Explicación racional del permiso");
            showSnackbar(R.string.app_name, android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(GPSActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISO_LOCATION);
                }
            });
        } else {
            Log.v(TAG, "Solicitando permiso");
            ActivityCompat.requestPermissions(GPSActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISO_LOCATION);
        }
    }

    private void showSnackbar(final int idTexto, final int textoAccion, View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(idTexto),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(textoAccion), listener).show();
    }

    @SuppressLint("MissingPermission")
    private void startLocations() {

        clienteLocalizacion = LocationServices.getFusedLocationProviderClient(this);
        ajustesCliente = LocationServices.getSettingsClient(this);
        //mientras se carga la nueva localizacion mostrar la ultima guardada
        clienteLocalizacion.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    Log.v(TAG, "última localización: " + location.toString());
                    latitud = String.valueOf(location.getLatitude());
                    longitud = String.valueOf(location.getLongitude());
                } else {
                    Log.v(TAG, "no hay última localización");
                }
            }
        });
        callbackLocalizacion = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location localizacion = locationResult.getLastLocation();
                Log.v(TAG, "Nueva localizacion: " + localizacion.toString());

            }
        };
        //intervalos de refresco de la localizacion
        peticionLocalizacion = new LocationRequest();
        peticionLocalizacion.setInterval(10000);
        peticionLocalizacion.setFastestInterval(5000);
        peticionLocalizacion.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//alta precision
        //Lanzar el cliente de peticiones.
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(peticionLocalizacion);
        //
        ajustesPeticionLocalizacion = builder.build();
        ajustesCliente.checkLocationSettings(ajustesPeticionLocalizacion)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.v(TAG, "Se cumplen todos los requisitos");
                        //todo ha ido bien y empiezo a geolocalizar
                        clienteLocalizacion.requestLocationUpdates(peticionLocalizacion, callbackLocalizacion, null);

                        Intent intent = new Intent(GPSActivity.this, Db4oActivity.class);
                        intent.putExtra("latitud", latitud);
                        intent.putExtra("longitud", longitud);
                        startActivity(intent);
                        //

                    }
                })
                //si da algún error entro aquí
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            //el usuario puede arreglarlo
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.v(TAG, "Falta algún requisito, intento de adquisición");
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(GPSActivity.this, RESOLVE_RESULT);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.v(TAG, "No se puede adquirir.");
                                }
                                break;
                            //si no es subsanable por el usuario entro aqui
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Log.v(TAG, "Falta algún requisito, que no se puede adquirir.");
                        }
                    }
                });
    }
}
