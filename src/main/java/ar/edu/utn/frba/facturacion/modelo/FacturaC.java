package ar.edu.utn.frba.facturacion.modelo;

import java.time.LocalDate;

/**
 * Factura tipo C: emitida por un Monotributista / Exento (no cobra IVA).
 * Version MIGRADA: record.
 */
public record FacturaC(String numero, double neto, LocalDate fecha)
        implements Factura {
}
