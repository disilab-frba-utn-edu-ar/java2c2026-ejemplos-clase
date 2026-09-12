package ar.edu.utn.frba.biblioteca.repository;

import ar.edu.utn.frba.biblioteca.model.Libro;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LibroRepository {

    private final Map<Long, Libro> libros = new ConcurrentHashMap<>();
    private final AtomicLong secuenciaId = new AtomicLong(0);

    public Libro save(Libro libro) {
        if (libro.getId() == null) {
            libro.setId(secuenciaId.incrementAndGet());
        }
        libros.put(libro.getId(), libro);
        return libro;
    }

    public Collection<Libro> findAll() {
        return libros.values();
    }

    public Optional<Libro> findById(Long id) {
        return Optional.ofNullable(libros.get(id));
    }
}
