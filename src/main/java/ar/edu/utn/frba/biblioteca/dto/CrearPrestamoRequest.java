package ar.edu.utn.frba.biblioteca.dto;

import jakarta.validation.constraints.NotNull;

public record CrearPrestamoRequest(
        @NotNull(message = "El usuarioId es obligatorio") Long usuarioId,
        @NotNull(message = "El libroId es obligatorio") Long libroId
) {
}
