package ar.edu.utn.frba.biblioteca.model;

public class Libro {

    private Long id;
    private String titulo;
    private String autor;
    private int stock;

    public Libro() {
    }

    public Libro(Long id, String titulo, String autor, int stock) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
