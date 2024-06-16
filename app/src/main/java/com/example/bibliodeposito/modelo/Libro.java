package com.example.bibliodeposito.modelo;

public class Libro {

    //Atributos de la clase libro
    private int id;
    private String titulo, isbn, autor, materia, curso;
    private int anioPublicacion;
    private double danio;
    private int prestado;

    //Constructor vacio
    public Libro(){

    }

    //Constructor con argumentos
    public Libro(int id, String titulo, String isbn, String autor, String materia, String curso, int anioPublicacion, double danio, int prestado) {
        this.id = id;
        this.titulo = titulo;
        this.isbn = isbn;
        this.autor = autor;
        this.materia = materia;
        this.curso = curso;
        this.anioPublicacion = anioPublicacion;
        this.danio = danio;
        this.prestado = prestado;
    }

    //Constructor para el adaptador del recyleview
    public Libro(int id, String titulo, String isbn, String autor) {
        this.id = id;
        this.titulo = titulo;
        this.isbn = isbn;
        this.autor = autor;
    }

    //Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public double getDanio() {
        return danio;
    }

    public void setDanio(double danio) {
        this.danio = danio;
    }

    public int getPrestado() {
        return prestado;
    }

    public void setPrestado(int prestado) {
        this.prestado = prestado;
    }

    //Metodo toString
    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", isbn='" + isbn + '\'' +
                ", autor='" + autor + '\'' +
                ", materia='" + materia + '\'' +
                ", curso='" + curso + '\'' +
                ", anioPublicacion=" + anioPublicacion +
                ", danio=" + danio +
                ", prestado=" + prestado +
                '}';
    }
}
