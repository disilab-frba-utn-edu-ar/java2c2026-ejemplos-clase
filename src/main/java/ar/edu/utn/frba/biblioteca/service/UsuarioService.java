package ar.edu.utn.frba.biblioteca.service;

import ar.edu.utn.frba.biblioteca.dto.CrearUsuarioRequest;
import ar.edu.utn.frba.biblioteca.exception.RecursoNoEncontradoException;
import ar.edu.utn.frba.biblioteca.model.Usuario;
import ar.edu.utn.frba.biblioteca.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario crear(CrearUsuarioRequest request) {
        Usuario usuario = new Usuario(null, request.nombre(), request.email());
        return usuarioRepository.save(usuario);
    }

    public Collection<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));
    }
}
