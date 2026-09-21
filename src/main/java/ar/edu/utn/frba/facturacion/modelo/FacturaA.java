package ar.edu.utn.frba.facturacion.modelo;

/**
 * Factura tipo A: emitida a un Responsable Inscripto (IVA 21% discriminado).
 * Version MIGRADA: record (inmutable, con accessors numero()/neto()/cuitCliente()).
 */
public record FacturaA(String numero, double neto, String cuitCliente)
        implements Factura {
}
