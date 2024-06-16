package com.example.bibliodeposito.modelo;

import java.util.Arrays;

public class Imagen {
    private int id;
    private byte[] imagen;
    private int id_libro;

    //Constructor con argumentos
    public Imagen(int id, byte[] imagen, int id_libro) {
        this.id = id;
        this.imagen = imagen;
        this.id_libro = id_libro;
    }

    //Constructor vacio
    public Imagen() {
    }

    //Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public int getId_libro() {
        return id_libro;
    }

    public void setId_libro(int id_libro) {
        this.id_libro = id_libro;
    }

    //Metodo toString
    @Override
    public String toString() {
        return "Imagen{" +
                "id=" + id +
                ", imagen=" + Arrays.toString(imagen) +
                ", id_libro=" + id_libro +
                '}';
    }
}
