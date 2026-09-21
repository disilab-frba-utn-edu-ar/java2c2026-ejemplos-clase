package ar.edu.utn.frba.facturacion.modelo;

/**
 * Factura tipo E: operacion de Exportacion (exenta de IVA).
 *
 * Es la factura que se AGREGA en el Paso 4 del ejercicio. Al sumarla al
 * permits de {@link Factura}, todos los switch exhaustivos sobre Factura
 * dejan de compilar hasta contemplar este nuevo caso.
 */
public record FacturaE(String numero, double neto, String paisDestino)
        implements Factura {
}
