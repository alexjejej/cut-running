package com.raywenderlich.android.rwandroidtutorial.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class BDsqlite extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "pasosDatabase";
    public static final String TABLE_NAME = "pasos";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_PASOS_HOY = "pasoshoy";
    public static final String COLUMN_PASOS_TOTALES = "pasostotales";
    public static final String COLUMN_DISTANCIA = "distancia";
    public static final String COLUMN_PESO = "peso";
    public static final String COLUMN_EDAD = "edad";
    public static final String COLUMN_ESTATURA = "estatura";
    public static final String COLUMN_CENTRO_UNIVERSITARIO = "centro_universitario";
    public static final String COLUMN_CARRERA = "carrera";



    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NOMBRE + " TEXT PRIMARY KEY, " +
                    COLUMN_PASOS_HOY + " INTEGER, " +
                    COLUMN_PASOS_TOTALES + " INTEGER, " +
                    COLUMN_DISTANCIA + " REAL, " +
                    COLUMN_EDAD + " INTEGER, " +
                    COLUMN_ESTATURA + " INTEGER, " +
                    COLUMN_PESO + " REAL, " +
                    COLUMN_CENTRO_UNIVERSITARIO + " TEXT, " +
                    COLUMN_CARRERA + " TEXT" + ")";



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
    //Método Auxiliar para Datos de Tipo int
    public int getIntData(String nombre, String columnName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int data = 0; // Valor predeterminado

        try {
            cursor = db.rawQuery("SELECT " + columnName + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NOMBRE + " = ?", new String[]{nombre});
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(columnName);
                if (columnIndex != -1) {
                    data = cursor.getInt(columnIndex);
                }
            }
        } catch (Exception e) {
            // Si ocurre un error (como que la columna no existe), se mantiene el valor predeterminado -1
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return data;
    }


    //Método Auxiliar para Datos de Tipo String
    public String getStringData(String nombre, String columnName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String data = null; // Valor predeterminado

        try {
            cursor = db.rawQuery("SELECT " + columnName + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NOMBRE + " = ?", new String[]{nombre});
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(columnName);
                if (columnIndex != -1) {
                    data = cursor.getString(columnIndex);
                }
            }
        } catch (Exception e) {
            // Si ocurre un error (como que la columna no existe), se mantiene el valor predeterminado null
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return data;
    }

    //Método para insertar
    public void insertOrUpdate(String nombre, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Filtrar valores no significativos
        ContentValues filteredValues = new ContentValues();
        for (String key : values.keySet()) {
            Object value = values.get(key);
            if (value instanceof Integer && (Integer) value == 0) continue; // Ignorar enteros que son cero
            if (value instanceof Float && (Float) value == 0.0f) continue; // Ignorar floats que son cero
            if (value == null) continue; // Ignorar valores nulos
            filteredValues.put(key, value.toString()); // Añadir valores significativos
        }

        // Solo proceder si hay valores significativos
        if (filteredValues.size() > 0) {
            int rowsAffected = db.update(TABLE_NAME, filteredValues, COLUMN_NOMBRE + " = ?", new String[]{nombre});
            if (rowsAffected == 0) {
                db.insert(TABLE_NAME, null, filteredValues);
            }
        }

        db.close();
    }





}


