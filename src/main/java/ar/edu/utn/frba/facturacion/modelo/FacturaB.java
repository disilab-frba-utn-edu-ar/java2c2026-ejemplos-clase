package ar.edu.utn.frba.facturacion.modelo;

import java.time.LocalDate;

/**
 * Factura tipo B: emitida a Consumidor Final (IVA 21% incluido en el precio).
 * Version MIGRADA: record.
 */
public record FacturaB(String numero, double neto, String nombreCliente, LocalDate fecha)
        implements Factura {
}
