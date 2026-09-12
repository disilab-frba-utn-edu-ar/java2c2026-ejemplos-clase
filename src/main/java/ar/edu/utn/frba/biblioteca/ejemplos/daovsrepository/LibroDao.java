package ar.edu.utn.frba.biblioteca.ejemplos.daovsrepository;

import ar.edu.utn.frba.biblioteca.model.Libro;

import java.util.List;

// No forma parte del flujo de la app: es solo para comparar contra LibroRepository en clase.
public interface LibroDao {

    void insertar(Libro libro);

    void actualizar(Libro libro);

    void eliminar(long id);

    Libro buscarPorId(long id);

    List<Libro> listarTodos();
}
