package com.das.entrega1;

public class Prenda {
    private int id; // ¡Nuevo! El número de carnet de identidad de la prenda
    private String nombre;
    private String categoria;

    // Constructor con ID (para cuando leemos de la base de datos)
    public Prenda(int id, String nombre, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
    }

    // Constructor sin ID (para cuando la creamos nueva, la BD le pondrá el ID sola)
    public Prenda(String nombre, String categoria) {
        this.nombre = nombre;
        this.categoria = categoria;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
}