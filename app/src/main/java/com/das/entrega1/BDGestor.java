package com.das.entrega1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class BDGestor extends SQLiteOpenHelper {

    // Datos de nuestra Base de Datos
    private static final String NOMBRE_BD = "ArmarioBD.db";
    private static final int VERSION_BD = 1;
    private static final String TABLA_ROPA = "ropa";

    public BDGestor(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    // Se ejecuta la primera vez que instalas la app. Aquí creamos la tabla.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String crearTabla = "CREATE TABLE " + TABLA_ROPA + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "categoria TEXT)";
        db.execSQL(crearTabla);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_ROPA);
        onCreate(db);
    }

    // --- MÉTODOS PARA USAR EN NUESTRA APP ---

    // 1. Guardar una prenda nueva
    public boolean insertarPrenda(String nombre, String categoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("categoria", categoria);

        long resultado = db.insert(TABLA_ROPA, null, valores);
        return resultado != -1; // Devuelve true si se guardó bien
    }

    // 2. Leer todas las prendas para mostrarlas en la lista
    public ArrayList<Prenda> obtenerTodasLasPrendas() {
        ArrayList<Prenda> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consultamos toda la tabla
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLA_ROPA, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nombre = cursor.getString(1);
                String categoria = cursor.getString(2);
                lista.add(new Prenda(id, nombre, categoria));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    // 3. Borrar una prenda usando su ID
    public void borrarPrenda(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLA_ROPA, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // 4. Modificar una prenda existente
    public boolean actualizarPrenda(int id, String nuevoNombre, String nuevaCategoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", nuevoNombre);
        valores.put("categoria", nuevaCategoria);

        // Actualizamos la fila que coincida con el ID
        int filasAfectadas = db.update(TABLA_ROPA, valores, "id=?", new String[]{String.valueOf(id)});
        return filasAfectadas > 0; // Devuelve true si se modificó correctamente
    }
}