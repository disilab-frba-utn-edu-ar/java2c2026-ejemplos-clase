package ar.edu.utn.frba.biblioteca.repository;

import ar.edu.utn.frba.biblioteca.model.Prestamo;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PrestamoRepository {

    private final Map<Long, Prestamo> prestamos = new ConcurrentHashMap<>();
    private final AtomicLong secuenciaId = new AtomicLong(0);

    public Prestamo save(Prestamo prestamo) {
        if (prestamo.getId() == null) {
            prestamo.setId(secuenciaId.incrementAndGet());
        }
        prestamos.put(prestamo.getId(), prestamo);
        return prestamo;
    }

    public Collection<Prestamo> findAll() {
        return prestamos.values();
    }

    public Optional<Prestamo> findById(Long id) {
        return Optional.ofNullable(prestamos.get(id));
    }
}
