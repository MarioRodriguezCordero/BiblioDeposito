package com.example.bibliodeposito.DB;

import com.example.bibliodeposito.modelo.Imagen;
import com.example.bibliodeposito.modelo.Libro;

import java.util.List;

public interface ILibroDB {
    Libro elemento(int id); //Devuelve el elemento dado su id
    Libro elementoTitulo(String title); //Devuelve el elemento dado su titulo exacto
    Imagen elementoImagen(int id);
    Imagen elementoImagenIdLibro(int id_libro, boolean separar);

    List<Libro> lista(); //Devuelve una lista con todos los elementos registrados

    void agregar(Libro book); //Añade el elemento indicado
    void actualizar(int id, Libro libro); //Actualiza datos del elemento dado su id
    void agregarFoto(Imagen img);
    void actualizarFoto(int id, Imagen img);

    void borrar(int id); //Elimina el elemento indicado con el id
    void borrarFoto(int id);
}
