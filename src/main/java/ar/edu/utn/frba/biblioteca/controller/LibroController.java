package ar.edu.utn.frba.biblioteca.controller;

import ar.edu.utn.frba.biblioteca.dto.CrearLibroRequest;
import ar.edu.utn.frba.biblioteca.model.Libro;
import ar.edu.utn.frba.biblioteca.service.LibroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Libro crear(@Valid @RequestBody CrearLibroRequest request) {
        return libroService.crear(request);
    }

    @GetMapping
    public Collection<Libro> listar() {
        return libroService.listar();
    }

    @GetMapping("/{id}")
    public Libro buscarPorId(@PathVariable Long id) {
        return libroService.buscarPorId(id);
    }
}
