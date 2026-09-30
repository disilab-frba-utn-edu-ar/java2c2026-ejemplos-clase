package ar.edu.utn.frba.facturacion.modelo;

import java.time.LocalDate;

/**
 * Factura tipo B: emitida a Consumidor Final.
 * Lleva IVA al 21%, pero NO se discrimina (va incluido en el precio final).
 * A los fines del calculo, el importe de IVA es el mismo 21% del neto.
 *
 * TODO PASO 1 (Records): convertir en record (ver FacturaA).
 */
public class FacturaB implements Factura {

    private final String numero;
    private final double neto;
    private final String nombreCliente;
    private final LocalDate fecha;

    public FacturaB(String numero, double neto, String nombreCliente, LocalDate fecha) {
        this.numero = numero;
        this.neto = neto;
        this.nombreCliente = nombreCliente;
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
        return "B";
    }

    @Override
    public LocalDate getFecha() {
        return fecha;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }
}
