package ar.edu.utn.frba.biblioteca.ejemplos.daovsrepository;

import ar.edu.utn.frba.biblioteca.model.Libro;

import java.util.ArrayList;
import java.util.List;

public class LibroDaoEnMemoria implements LibroDao {

    private final List<Libro> libros = new ArrayList<>();

    @Override
    public void insertar(Libro libro) {
        libros.add(libro);
    }

    @Override
    public void actualizar(Libro libro) {
        eliminar(libro.getId());
        libros.add(libro);
    }

    @Override
    public void eliminar(long id) {
        libros.removeIf(libro -> libro.getId() == id);
    }

    @Override
    public Libro buscarPorId(long id) {
        return libros.stream()
                .filter(libro -> libro.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Libro> listarTodos() {
        return new ArrayList<>(libros);
    }
}
