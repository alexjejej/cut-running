package com.raywenderlich.android.rwandroidtutorial.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class BDsqlite extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pasosDatabase";
    private static final String TABLE_NAME = "pasos";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_PASOS_HOY = "pasoshoy";
    private static final String COLUMN_PASOS_TOTALES = "pasostotales";
    private static final String COLUMN_DISTANCIA = "distancia";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NOMBRE + " TEXT PRIMARY KEY, " +
                    COLUMN_PASOS_HOY + " INTEGER, " +
                    COLUMN_PASOS_TOTALES + " INTEGER, " +
                    COLUMN_DISTANCIA + " REAL" + ")";

    public BDsqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void upsertData(String nombre, int pasoshoy, int pasostotales, float distancia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_PASOS_HOY, pasoshoy);
        values.put(COLUMN_PASOS_TOTALES, pasostotales);
        values.put(COLUMN_DISTANCIA, distancia);

        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_NOMBRE + " = ?", new String[]{nombre});
        if (rowsAffected == 0) {
            db.insert(TABLE_NAME, null, values);
        }
        db.close();
    }

    public Cursor getData(String columnName, String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + columnName + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NOMBRE + " = ?", new String[]{nombre});
    }

    public static String getColumnNombre() {
        return COLUMN_NOMBRE;
    }

    public static String getColumnPasosHoy() {
        return COLUMN_PASOS_HOY;
    }

    public static String getColumnPasosTotales() {
        return COLUMN_PASOS_TOTALES;
    }

    public static String getColumnDistancia() {
        return COLUMN_DISTANCIA;
    }
}
