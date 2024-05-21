package com.example.bibliodeposito.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.bibliodeposito.modelo.Libro;

public class DBManager {
    //TABLA LIBROS
    public static final String TABLE_BOOKS = "libros";
    public static final String BOOK_ID = "_id";
    public static final String BOOK_NAME = "libro";

    public static final String TABLE_BOOKS_CREATE = "create table deposito(_id integer not null, libros text not null);";

    private DBConexion conexion;
    private SQLiteDatabase dbdatabase;

    public DBManager(Context context) {
        conexion = new DBConexion(context);
    }

    public DBManager open() throws SQLException {
        dbdatabase = conexion.getWritableDatabase();
        return this;
    }

    public void close() {
        conexion.close();
    }

    public void insertBook(int id, String nombre) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOOK_ID, id);
        contentValues.put(BOOK_NAME, nombre);
        this.dbdatabase.insert(TABLE_BOOKS, null, contentValues);

        Log.d("inserción:", "Correcta");
    }

    public boolean insertModel(Libro libro) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOOK_ID, libro.getId());
        contentValues.put(BOOK_NAME, libro.getNombre());
        long result = dbdatabase.insert(TABLE_BOOKS, null, contentValues);

        if(result == -1) {
            Log.d("inserción:", "Incorrecta");
            return false;
        } else {
            Log.d("inserción:", "Correcta");
            return true;
        }
    }
}
