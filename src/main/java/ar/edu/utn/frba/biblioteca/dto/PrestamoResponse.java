package ar.edu.utn.frba.biblioteca.dto;

import java.time.LocalDate;

public record PrestamoResponse(
        Long id,
        Long usuarioId,
        String nombreUsuario,
        Long libroId,
        String tituloLibro,
        LocalDate fechaPrestamo,
        LocalDate fechaDevolucion,
        boolean devuelto
) {
}
