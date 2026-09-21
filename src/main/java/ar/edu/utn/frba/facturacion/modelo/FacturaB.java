package ar.edu.utn.frba.facturacion.modelo;

/**
 * Factura tipo B: emitida a Consumidor Final (IVA 21% incluido en el precio).
 * Version MIGRADA: record.
 */
public record FacturaB(String numero, double neto, String nombreCliente)
        implements Factura {
}
