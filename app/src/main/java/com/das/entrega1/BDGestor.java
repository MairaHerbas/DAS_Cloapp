package com.das.entrega1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class BDGestor extends SQLiteOpenHelper {
    private static final String NOMBRE_BD = "ArmarioBD.db";
    private static final int VERSION_BD = 3; // Subimos la versión
    private static final String TABLA_ROPA = "ropa";
    public BDGestor(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tablas
        String crearTabla = "CREATE TABLE ropa (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, categoria TEXT, uri_foto TEXT)";
        db.execSQL(crearTabla);
        String crearTablaConjuntos = "CREATE TABLE conjuntos (id INTEGER PRIMARY KEY AUTOINCREMENT, id_arriba INTEGER, id_abajo INTEGER, id_calzado INTEGER)";
        db.execSQL(crearTablaConjuntos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS ropa");
        db.execSQL("DROP TABLE IF EXISTS conjuntos"); // NUEVO
        onCreate(db);
    }

    // Actualizar Insertar
    public boolean insertarPrenda(String nombre, String categoria, String uriFoto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("categoria", categoria);
        valores.put("uri_foto", uriFoto); // Guardamos la ruta

        long resultado = db.insert(TABLA_ROPA, null, valores);
        return resultado != -1;
    }

    // Actualizar Obtener
    public ArrayList<Prenda> obtenerTodasLasPrendas() {
        ArrayList<Prenda> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLA_ROPA, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nombre = cursor.getString(1);
                String categoria = cursor.getString(2);
                String uriFoto = cursor.getString(3); // Leemos la ruta
                lista.add(new Prenda(id, nombre, categoria, uriFoto));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    //Borrar una prenda
    public void borrarPrenda(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLA_ROPA, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    //Modificar una prenda existente
    public boolean actualizarPrenda(int id, String nuevoNombre, String nuevaCategoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", nuevoNombre);
        valores.put("categoria", nuevaCategoria);
        // Actualizar
        int filasAfectadas = db.update(TABLA_ROPA, valores, "id=?", new String[]{String.valueOf(id)});
        return filasAfectadas > 0; // Devuelve true si se modificó correctamente
    }

    // Filtrar por categoría
    public ArrayList<Prenda> obtenerPrendasPorCategoria(String categoriaBuscada) {
        ArrayList<Prenda> listaFiltrada = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Consulta SQL para filtrar por categoría
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLA_ROPA + " WHERE categoria = ?", new String[]{categoriaBuscada});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nombre = cursor.getString(1);
                String categoria = cursor.getString(2);
                String uriFoto = cursor.getString(3);
                listaFiltrada.add(new Prenda(id, nombre, categoria, uriFoto));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaFiltrada;
    }

    public boolean insertarConjunto(int idArriba, int idAbajo, int idCalzado) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues valores = new android.content.ContentValues();
        valores.put("id_arriba", idArriba);
        valores.put("id_abajo", idAbajo);
        valores.put("id_calzado", idCalzado);
        return db.insert("conjuntos", null, valores) != -1;
    }

    public Prenda obtenerPrendaPorId(int idBuscado) {
        SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.rawQuery("SELECT * FROM ropa WHERE id = ?", new String[]{String.valueOf(idBuscado)});
        if (cursor.moveToFirst()) {
            return new Prenda(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        }
        cursor.close();
        return null;
    }

    public java.util.ArrayList<Conjunto> obtenerTodosLosConjuntos() {
        java.util.ArrayList<Conjunto> lista = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.rawQuery("SELECT * FROM conjuntos", null);

        if (cursor.moveToFirst()) {
            do {
                int idConjunto = cursor.getInt(0);
                // Buscamos las prendas reales usando los IDs guardados
                Prenda pArriba = obtenerPrendaPorId(cursor.getInt(1));
                Prenda pAbajo = obtenerPrendaPorId(cursor.getInt(2));
                Prenda pCalzado = obtenerPrendaPorId(cursor.getInt(3));

                if(pArriba != null && pAbajo != null && pCalzado != null){
                    lista.add(new Conjunto(idConjunto, pArriba, pAbajo, pCalzado));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public void borrarConjunto(int idConjunto) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("conjuntos", "id = ?", new String[]{String.valueOf(idConjunto)});
    }

    public boolean existeConjunto(int idArriba, int idAbajo, int idCalzado) {
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.rawQuery(
                "SELECT * FROM conjuntos WHERE id_arriba = ? AND id_abajo = ? AND id_calzado = ?",
                new String[]{String.valueOf(idArriba), String.valueOf(idAbajo), String.valueOf(idCalzado)}
        );
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }
}