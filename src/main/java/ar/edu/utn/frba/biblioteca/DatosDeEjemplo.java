package ar.edu.utn.frba.biblioteca;

import ar.edu.utn.frba.biblioteca.model.Libro;
import ar.edu.utn.frba.biblioteca.model.Usuario;
import ar.edu.utn.frba.biblioteca.repository.LibroRepository;
import ar.edu.utn.frba.biblioteca.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatosDeEjemplo implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    public DatosDeEjemplo(UsuarioRepository usuarioRepository, LibroRepository libroRepository) {
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
    }

    @Override
    public void run(String... args) {
        usuarioRepository.save(new Usuario(null, "Ada Lovelace", "ada@utn.edu.ar"));
        usuarioRepository.save(new Usuario(null, "Alan Turing", "alan@utn.edu.ar"));

        libroRepository.save(new Libro(null, "Clean Code", "Robert C. Martin", 2));
        libroRepository.save(new Libro(null, "Effective Java", "Joshua Bloch", 1));
        libroRepository.save(new Libro(null, "Domain-Driven Design", "Eric Evans", 0));
    }
}
