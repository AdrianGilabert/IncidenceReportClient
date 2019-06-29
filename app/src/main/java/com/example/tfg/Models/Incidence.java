package com.example.tfg.Models;

import java.util.Date;
import java.util.UUID;

public class Incidence implements Comparable<Incidence> {
    private String id;
    private String matricula, descripcion, url;
    private BadPractise mala_practica;
    private Date fecha;
    private double latitud, longitud;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    private String user;

    public Incidence(String matricula, BadPractise mala_practica, String descripcion, Date fecha, String user, Double latitud, Double longitud) {
        this.matricula = matricula;
        this.descripcion = descripcion;
        this.mala_practica = mala_practica;
        this.fecha = fecha;
        this.user = user;
        this.id = UUID.randomUUID().toString();
        this.latitud = latitud;
        this.longitud = longitud;
        this.url = "null";
    }

    public Incidence() {

    }

    public String getUrl() {
        url = "audios/" + this.id + ".3gp";
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BadPractise getMala_practica() {
        return mala_practica;
    }

    public void setMala_practica(BadPractise mala_practica) {
        this.mala_practica = mala_practica;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public int compareTo(Incidence o) {
        return o.getFecha().compareTo(this.fecha);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
