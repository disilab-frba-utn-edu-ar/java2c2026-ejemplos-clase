package ar.edu.utn.frba.biblioteca.repository;

import ar.edu.utn.frba.biblioteca.model.Usuario;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UsuarioRepository {

    private final Map<Long, Usuario> usuarios = new ConcurrentHashMap<>();
    private final AtomicLong secuenciaId = new AtomicLong(0);

    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(secuenciaId.incrementAndGet());
        }
        usuarios.put(usuario.getId(), usuario);
        return usuario;
    }

    public Collection<Usuario> findAll() {
        return usuarios.values();
    }

    public Optional<Usuario> findById(Long id) {
        return Optional.ofNullable(usuarios.get(id));
    }
}
