package com.das.entrega1;

public class Conjunto {
    private int id;
    private Prenda arriba;
    private Prenda abajo;
    private Prenda calzado;

    public Conjunto(int id, Prenda arriba, Prenda abajo, Prenda calzado) {
        this.id = id;
        this.arriba = arriba;
        this.abajo = abajo;
        this.calzado = calzado;
    }

    //Getters
    public int getId() { return id; }
    public Prenda getArriba() { return arriba; }
    public Prenda getAbajo() { return abajo; }
    public Prenda getCalzado() { return calzado; }
}