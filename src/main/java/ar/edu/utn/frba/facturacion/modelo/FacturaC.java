package ar.edu.utn.frba.facturacion.modelo;

import java.time.LocalDate;

/**
 * Factura tipo C: emitida por un Monotributista (o Exento).
 * NO discrimina ni cobra IVA, por lo que el IVA es 0.
 *
 * TODO PASO 1 (Records): convertir en record (ver FacturaA).
 */
public class FacturaC implements Factura {

    private final String numero;
    private final double neto;
    private final LocalDate fecha;

    public FacturaC(String numero, double neto, LocalDate fecha) {
        this.numero = numero;
        this.neto = neto;
        this.fecha = fecha;
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
    public LocalDate getFecha() {
        return fecha;
    }

    @Override
    public double calcularIva() {
        return 0.0;
    }
}
