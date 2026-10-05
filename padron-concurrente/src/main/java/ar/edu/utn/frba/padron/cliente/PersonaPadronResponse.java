package ar.edu.utn.frba.padron.cliente;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * El JSON tal cual lo devuelve la API del padrón, con SUS nombres y SUS códigos.
 * Solo lo conoce el adapter: no sale del paquete hacia el resto del sistema.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record PersonaPadronResponse(
        long idPersona,
        String denominacion,
        String estadoClave,   // "ACTIVO" | "INACTIVO"
        String categoriaIva   // "RI" | "MONO" | "EX"
) {
}
