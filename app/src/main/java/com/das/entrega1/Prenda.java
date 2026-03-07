package com.das.entrega1;

public class Prenda {
    private int id;
    private String nombre;
    private String categoria;
    private String uriFoto; // ¡NUEVO!

    // Constructor con ID y Foto
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

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getUriFoto() { return uriFoto; } // ¡NUEVO!
    // ¡NUEVO! Truco para que el Spinner muestre el nombre de la prenda directamente
    @Override
    public String toString() {
        return nombre;
    }
}