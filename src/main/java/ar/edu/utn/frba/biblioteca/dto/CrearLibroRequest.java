package ar.edu.utn.frba.biblioteca.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CrearLibroRequest(
        @NotBlank(message = "El titulo es obligatorio") String titulo,
        @NotBlank(message = "El autor es obligatorio") String autor,
        @Min(value = 0, message = "El stock no puede ser negativo") int stock
) {
}
