package com.das.entrega1;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RopaProvider extends ContentProvider {
    public static final String AUTHORITY = "com.das.entrega1.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/prendas");

    // Códigos para el UriMatcher
    private static final int PRENDAS = 1;
    private static final int PRENDA_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, "prendas", PRENDAS);
        uriMatcher.addURI(AUTHORITY, "prendas/#", PRENDA_ID);
    }

    private BDGestor bdGestor;

    @Override
    public boolean onCreate() {
        bdGestor = new BDGestor(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = bdGestor.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case PRENDAS:
                cursor = db.query("ropa", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRENDA_ID:
                String id = uri.getLastPathSegment();
                cursor = db.query("ropa", projection, "id=?", new String[]{id}, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = bdGestor.getWritableDatabase();
        long id = db.insert("ropa", null, values);

        if (id > 0) {
            Uri uriNueva = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(uriNueva, null);
            return uriNueva;
        }
        throw new android.database.SQLException("Fallo al insertar fila en " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = bdGestor.getWritableDatabase();
        int filasBorradas;

        switch (uriMatcher.match(uri)) {
            case PRENDAS:
                filasBorradas = db.delete("ropa", selection, selectionArgs);
                break;
            case PRENDA_ID:
                String id = uri.getLastPathSegment();
                filasBorradas = db.delete("ropa", "id=?", new String[]{id});
                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }

        if (filasBorradas > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return filasBorradas;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = bdGestor.getWritableDatabase();
        int filasActualizadas;

        switch (uriMatcher.match(uri)) {
            case PRENDAS:
                filasActualizadas = db.update("ropa", values, selection, selectionArgs);
                break;
            case PRENDA_ID:
                String id = uri.getLastPathSegment();
                filasActualizadas = db.update("ropa", values, "id=?", new String[]{id});
                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }

        if (filasActualizadas > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return filasActualizadas;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}