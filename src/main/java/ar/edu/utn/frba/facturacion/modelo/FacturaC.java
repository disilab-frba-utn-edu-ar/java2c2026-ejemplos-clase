package ar.edu.utn.frba.facturacion.modelo;

/**
 * Factura tipo C: emitida por un Monotributista / Exento (no cobra IVA).
 * Version MIGRADA: record.
 */
public record FacturaC(String numero, double neto)
        implements Factura {
}
