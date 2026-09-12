package ar.edu.utn.frba.biblioteca.service;

import ar.edu.utn.frba.biblioteca.dto.CrearLibroRequest;
import ar.edu.utn.frba.biblioteca.exception.RecursoNoEncontradoException;
import ar.edu.utn.frba.biblioteca.model.Libro;
import ar.edu.utn.frba.biblioteca.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public Libro crear(CrearLibroRequest request) {
        Libro libro = new Libro(null, request.titulo(), request.autor(), request.stock());
        return libroRepository.save(libro);
    }

    public Collection<Libro> listar() {
        return libroRepository.findAll();
    }

    public Libro buscarPorId(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el libro con id " + id));
    }
}
