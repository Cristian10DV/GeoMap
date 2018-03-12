package com.example.dam.geomap;

import android.content.Intent;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.AndroidSupport;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Db4oActivity extends AppCompatActivity {

    private static final String TAG = "xyzyv";
    private String fechaActual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    private String latitudFinal, longitudFinal;
    private boolean noExiste = false;

    private ObjectContainer objectContainer;

    public EmbeddedConfiguration getDb4oConfig() throws IOException {
        EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
        configuration.common().add(new AndroidSupport());
        configuration.common().objectClass(Localizacion.class).
                objectField("fecha").indexed(true);
        return configuration;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db4o);

        Button btEnviar = findViewById(R.id.btEnviar);
        final EditText etFecha = findViewById(R.id.etFecha);
        final TextView tvFechaIncorrecta = findViewById(R.id.tvFechaIncorrecta);

        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "FECHA SELECCIONADA:" + etFecha.getText().toString());
                objectContainer = openDataBase("ejemplo.db4o");

                //recoger datos
                String latitud = getIntent().getStringExtra("latitud");
                String longitud = getIntent().getStringExtra("longitud");


                Localizacion loc = new Localizacion();
                objectContainer.store(loc);
                objectContainer.commit();

                loc = new Localizacion(longitud, latitud);
                objectContainer.store(loc);
                objectContainer.commit();

                loc = new Localizacion(longitud, latitud, fechaActual);
                objectContainer.store(loc);
                objectContainer.commit();

                /*Query consulta = objectContainer.query();
                consulta.constrain(Localizacion.class);
                ObjectSet<Localizacion> localizaciones = consulta.execute();
                for(Localizacion localizacion: localizaciones){
                    Log.v(TAG, "1: " + localizacion.toString());
                }*/
                ObjectSet<Localizacion> locs = objectContainer.query(
                        new Predicate<Localizacion>() {
                            @Override
                            public boolean match(Localizacion loc) {
                                Log.v("CRISTIAN", loc.getFecha().equals(etFecha.getText().toString()) + " HOLA");
                                return loc.getFecha().equals(etFecha.getText().toString());
                            }
                        });
                for(Localizacion localizacion: locs){
                    noExiste = true;
                    Log.v(TAG, "2: " + localizacion.toString());
                    latitudFinal = localizacion.getLatitud();
                    Log.v(TAG, "latitud: " + latitudFinal);
                    longitudFinal = localizacion.getLongitud();
                    Log.v(TAG, "longitud: " + longitudFinal);
                }

                if (noExiste == false) {
                    Log.v(TAG, "no existe esa fecha");
                    latitudFinal = "";
                    longitudFinal = "";
                    Log.v(TAG, "Latitud final:" +  latitudFinal);
                    Log.v(TAG, "Longitud final:" +  longitudFinal);

                }else{
                    Log.v(TAG, "Latitud final:" +  latitudFinal);
                    Log.v(TAG, "Longitud final:" +  longitudFinal);
                }



                if(latitudFinal.equals("") || latitudFinal == null || longitudFinal.equals("") || longitudFinal == null){
                    Log.v(TAG, "latitud o longitud nula");
                    tvFechaIncorrecta.setVisibility(View.VISIBLE);

                }else{
                    tvFechaIncorrecta.setVisibility(View.INVISIBLE);
                    //pasamos datos a el mapa
                    Intent intent = new Intent(Db4oActivity.this, MapsActivity.class);
                    intent.putExtra("latitud", latitudFinal);
                    intent.putExtra("longitud", longitudFinal);
                    startActivity(intent);
                    Log.v(TAG, "Pasando datos");
                }


                objectContainer.close();
            }
        });



    }

    private ObjectContainer openDataBase(String archivo) {
        ObjectContainer objectContainer = null;
        try {
            String name = getExternalFilesDir(null) + "/" + archivo;
            objectContainer = Db4oEmbedded.openFile(getDb4oConfig(), name);
        } catch (IOException e) {
            Log.v(TAG, e.toString());
        }
        return objectContainer;
    }

}
