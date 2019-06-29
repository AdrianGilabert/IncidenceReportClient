package com.example.tfg.Models;

import java.util.UUID;

public class BadPractise {
    private String id;
    private String titulo, descripcion;

    public BadPractise(String titulo, String descripcion) {
        this.id = UUID.randomUUID().toString();
        ;
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public BadPractise() {

    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BadPractise) {
            if (((BadPractise) o).getId() == this.getId()) return true;
        }
        return false;

    }
}