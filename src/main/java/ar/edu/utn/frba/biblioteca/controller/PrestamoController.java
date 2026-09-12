package ar.edu.utn.frba.biblioteca.controller;

import ar.edu.utn.frba.biblioteca.dto.CrearPrestamoRequest;
import ar.edu.utn.frba.biblioteca.dto.PrestamoResponse;
import ar.edu.utn.frba.biblioteca.exception.RecursoNoEncontradoException;
import ar.edu.utn.frba.biblioteca.exception.StockInsuficienteException;
import ar.edu.utn.frba.biblioteca.model.Libro;
import ar.edu.utn.frba.biblioteca.model.Prestamo;
import ar.edu.utn.frba.biblioteca.model.Usuario;
import ar.edu.utn.frba.biblioteca.repository.LibroRepository;
import ar.edu.utn.frba.biblioteca.repository.PrestamoRepository;
import ar.edu.utn.frba.biblioteca.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    public PrestamoController(PrestamoRepository prestamoRepository,
                               UsuarioRepository usuarioRepository,
                               LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrestamoResponse crear(@Valid @RequestBody CrearPrestamoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el usuario con id " + request.usuarioId()));

        Libro libro = libroRepository.findById(request.libroId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el libro con id " + request.libroId()));

        if (libro.getStock() <= 0) {
            throw new StockInsuficienteException(
                    "No hay stock disponible del libro '" + libro.getTitulo() + "'");
        }

        libro.setStock(libro.getStock() - 1);
        libroRepository.save(libro);

        Prestamo prestamo = new Prestamo(null, usuario.getId(), libro.getId(), LocalDate.now());
        prestamoRepository.save(prestamo);

        return aResponse(prestamo, usuario, libro);
    }

    @PostMapping("/{id}/devolver")
    public PrestamoResponse devolver(@PathVariable Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el prestamo con id " + id));

        if (!prestamo.isDevuelto()) {
            prestamo.setDevuelto(true);
            prestamo.setFechaDevolucion(LocalDate.now());
            prestamoRepository.save(prestamo);

            Libro libro = libroRepository.findById(prestamo.getLibroId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe el libro con id " + prestamo.getLibroId()));
            libro.setStock(libro.getStock() + 1);
            libroRepository.save(libro);
        }

        Usuario usuario = usuarioRepository.findById(prestamo.getUsuarioId()).orElse(null);
        Libro libro = libroRepository.findById(prestamo.getLibroId()).orElse(null);
        return aResponse(prestamo, usuario, libro);
    }

    @GetMapping
    public Collection<PrestamoResponse> listar() {
        return prestamoRepository.findAll().stream()
                .map(prestamo -> {
                    Usuario usuario = usuarioRepository.findById(prestamo.getUsuarioId()).orElse(null);
                    Libro libro = libroRepository.findById(prestamo.getLibroId()).orElse(null);
                    return aResponse(prestamo, usuario, libro);
                })
                .toList();
    }

    private PrestamoResponse aResponse(Prestamo prestamo, Usuario usuario, Libro libro) {
        return new PrestamoResponse(
                prestamo.getId(),
                prestamo.getUsuarioId(),
                usuario != null ? usuario.getNombre() : null,
                prestamo.getLibroId(),
                libro != null ? libro.getTitulo() : null,
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucion(),
                prestamo.isDevuelto()
        );
    }
}
