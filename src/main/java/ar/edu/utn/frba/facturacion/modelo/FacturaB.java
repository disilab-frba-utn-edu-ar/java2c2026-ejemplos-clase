package ar.edu.utn.frba.facturacion.modelo;

/**
 * Factura tipo B: emitida a Consumidor Final.
 * Lleva IVA al 21%, pero NO se discrimina (va incluido en el precio final).
 * A los fines del calculo, el importe de IVA es el mismo 21% del neto.
 *
 * TODO PASO 1 (Records): convertir en record (ver FacturaA).
 */
public class FacturaB implements Factura {

    private static final double ALICUOTA = 0.21;

    private final String numero;
    private final double neto;
    private final String nombreCliente;

    public FacturaB(String numero, double neto, String nombreCliente) {
        this.numero = numero;
        this.neto = neto;
        this.nombreCliente = nombreCliente;
    }

    @Override
    public String getNumero() {
        return numero;
    }

    @Override
    public double getNeto() {
        return neto;
    }

    @Override
    public String getTipo() {
        return "B";
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    @Override
    public double calcularIva() {
        // Consumidor Final: IVA 21% incluido en el precio.
        return neto * ALICUOTA;
    }
}
