package com.das.entrega1;

public class Prenda {
    private int id;
    private String nombre;
    private String categoria;
    private String uriFoto;
    public Prenda(int id, String nombre, String categoria, String uriFoto) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.uriFoto = uriFoto;
    }

    // Constructor sin ID (Para prendas nuevas)
    public Prenda(String nombre, String categoria, String uriFoto) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.uriFoto = uriFoto;
    }

    //Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getUriFoto() { return uriFoto; } // ¡NUEVO!
    @Override
    public String toString() {
        return nombre;
    }
}