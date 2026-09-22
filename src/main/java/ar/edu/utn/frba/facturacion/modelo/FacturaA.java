package ar.edu.utn.frba.facturacion.modelo;

import java.time.LocalDate;

/**
 * Factura tipo A: emitida a un Responsable Inscripto (IVA 21% discriminado).
 * Version MIGRADA: record (inmutable, con accessors numero()/neto()/cuitCliente()/fecha()).
 */
public record FacturaA(String numero, double neto, String cuitCliente, LocalDate fecha)
        implements Factura {
}
