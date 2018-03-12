package com.example.dam.geomap;

public class Localizacion {

    private String longitud;
    private String latitud;
    private String fecha;

    public Localizacion() {
    }

    public Localizacion(String longitud, String latitud, String fecha) {
        this.longitud = longitud;
        this.latitud = latitud;
        this.fecha = fecha;
    }

    public Localizacion(String longitud, String latitud) {
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Localizacion{" +
                "longitud='" + longitud + '\'' +
                ", latitud='" + latitud + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}