package ar.edu.utn.frba.facturacion.modelo;

/**
 * Factura tipo C: emitida por un Monotributista (o Exento).
 * NO discrimina ni cobra IVA, por lo que el IVA es 0.
 *
 * TODO PASO 2 (Records): convertir en record (ver FacturaA).
 */
public class FacturaC implements Factura {

    private final String numero;
    private final double neto;

    public FacturaC(String numero, double neto) {
        this.numero = numero;
        this.neto = neto;
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
        return "C";
    }

    @Override
    public double calcularIva() {
        // Monotributista: no corresponde IVA.
        return 0.0;
    }
}
