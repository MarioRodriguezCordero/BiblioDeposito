package com.example.bibliodeposito.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.bibliodeposito.modelo.Imagen;
import com.example.bibliodeposito.modelo.Libro;

import java.util.ArrayList;

public class DBConexion extends SQLiteOpenHelper implements ILibroDB{

    Context contexto;
    private ArrayList<Libro> libroList;

    public static final String DB_NAME = "deposito";
    public static final String TABLE_NAME="deposito";
    public static final int VERSION = 1;

    //Constructor de la base de datos
    public DBConexion(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
        this.contexto = context;
    }

    //Creacion de la base de datos
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT NOT NULL, " +
                "isbn TEXT NOT NULL, " +
                "autor TEXT NOT NULL, " +
                "materia TEXT, " +
                "curso TEXT, " +
                "anioPublicacion INTEGER, " +
                "danio REAL, " +
                "prestado INTEGER DEFAULT 0)";
        db.execSQL(sql);

        String img = "CREATE TABLE imagen (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "img BLOB NOT NULL, " +
                "id_libro INTEGER NOT NULL)";
        db.execSQL(img);

        //Inserccion libro de prueba
        String insert = "INSERT INTO deposito VALUES (null, " +
                "'Libro de prueba', " +
                "'123456789', " +
                "'Yo mismo', " +
                "'Programacion', " +
                "'1 de primaria', " +
                "2024, " +
                "0, " +
                "0)";
        db.execSQL(insert);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);

        String qQuery = "DROP TABLE IF EXISTS imagen";
        db.execSQL(qQuery);
    }

    //Buscar un libro por id
    @Override
    public Libro elemento(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM deposito WHERE _id=" + id, null);
        try {
            if(cursor.moveToNext()){
                return extraerLibro(cursor);
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d("TAG", "Error elemento(id) DBConexion" + e.getMessage());
            throw e;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    //Extraer los datos del libro encontrados
    private Libro extraerLibro(Cursor cursor) {
        Libro libro = new Libro();

        libro.setId(cursor.getInt(0));
        libro.setTitulo(cursor.getString(1));
        libro.setIsbn(cursor.getString(2));
        libro.setAutor(cursor.getString(3));
        libro.setMateria(cursor.getString(4));
        libro.setCurso(cursor.getString(5));
        libro.setAnioPublicacion(cursor.getInt(6));
        libro.setDanio(cursor.getDouble(7));
        libro.setPrestado(cursor.getInt(8));

        return libro;
    }

    //Extraer la imagen
    private Imagen extraerImagen(Cursor cursor) {
        Imagen img = new Imagen();

        img.setId(cursor.getInt(0));
        img.setImagen(cursor.getBlob(1));
        img.setId_libro(cursor.getInt(2));

        return img;
    }

    //Extraer el libro a partir del titulo
    @Override
    public Libro elementoTitulo(String title) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM deposito WHERE titulo='" + title + "'", null);
        try {
            if(cursor.moveToNext()){
                return extraerLibro(cursor);
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d("TAG", "Error elemento(title) DBConexion" + e.getMessage());
            throw e;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //Extraer la imagen a traves de su id
    @Override
    public Imagen elementoImagen(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM imagen WHERE _id=" + id, null);
        try {
            if(cursor.moveToNext()) {
                return extraerImagen(cursor);
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d("TAG", "Error ElementoImagen DBConexion" + e.getMessage());
            throw e;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //Extraer la imagen relacionada de un libro
    @Override
    public Imagen elementoImagenIdLibro(int id_libro, boolean separar) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM imagen WHERE id_libro=" + id_libro, null);
        try {
            if(cursor.moveToNext()) {
                return extraerImagen(cursor);
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d("TAG", "Error ElementoImagen DBConexion" + e.getMessage());
            throw e;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //Lista de libros con todos sus datos
    @Override
    public ArrayList<Libro> lista() {
        SQLiteDatabase db = getReadableDatabase();
        libroList = new ArrayList<>();
        String sql = "SELECT * FROM deposito ORDER BY titulo ASC";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()) {
            do {
                libroList.add(
                        new Libro(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getString(5),
                                cursor.getInt(6),
                                cursor.getDouble(7),
                                cursor.getInt(8)
                        )
                );
            } while (cursor.moveToNext());
        }
        cursor.close();
        return libroList;
    }

    //Añadir un libro a la base de datos
    @Override
    public void agregar(Libro book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("titulo", book.getTitulo());
        values.put("isbn", book.getIsbn());
        values.put("autor", book.getAutor());
        values.put("materia", book.getMateria());
        values.put("curso", book.getCurso());
        values.put("anioPublicacion", book.getAnioPublicacion());
        values.put("danio", book.getDanio());
        values.put("prestado", book.getPrestado());

        db.insert("deposito", null, values);
    }

    //añadir la foto de un libro a la base de datos
    @Override
    public void agregarFoto(Imagen img) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("img", img.getImagen());
        values.put("id_libro", img.getId_libro());

        db.insert("imagen", null, values);
    }

    //actualizar la informacion de un libro en la base de datos
    @Override
    public void actualizar(int id, Libro libro) {
        SQLiteDatabase db = getWritableDatabase();
        String[] parametros = {String.valueOf(id)};
        ContentValues values = new ContentValues();

        values.put("titulo", libro.getTitulo());
        values.put("isbn", libro.getIsbn());
        values.put("autor", libro.getAutor());
        values.put("materia", libro.getMateria());
        values.put("curso", libro.getCurso());
        values.put("anioPublicacion", libro.getAnioPublicacion());
        values.put("danio", libro.getDanio());
        values.put("prestado", libro.getPrestado());

        db.update("deposito", values, "_id=?", parametros);
    }

    //actualizar una foto en la base de datos
    @Override
    public void actualizarFoto(int id, Imagen img) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] parametros = {String.valueOf(id)};
        ContentValues values = new ContentValues();

        values.put("img", img.getImagen());
        values.put("id_libro", img.getId_libro());

        db.update("imagen", values, "_id=?", parametros);
    }

    //borrar un libro de la base de datos
    @Override
    public void borrar(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] parametros = {String.valueOf(id)};

        db.delete("deposito", "_id=?", parametros);
    }

    //borrar una foto de la base de datos
    @Override
    public void borrarFoto(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] parametros = {String.valueOf(id)};

        db.delete("imagen", "id_libro=?", parametros);
    }
}
