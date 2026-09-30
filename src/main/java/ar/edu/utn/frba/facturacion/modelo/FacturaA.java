package ar.edu.utn.frba.facturacion.modelo;

import java.time.LocalDate;

/**
 * Factura tipo A: emitida a un Responsable Inscripto.
 * El IVA se DISCRIMINA y se calcula al 21% sobre el neto.
 *
 * TODO PASO 1 (Records): convertir esta clase en un record.
 *   Un record es inmutable y genera constructor, getters, equals,
 *   hashCode y toString automaticamente. Ejemplo del objetivo:
 *
 *       public record FacturaA(String numero, double neto,
 *                              String cuitCliente, LocalDate fecha)
 *               implements Factura { }
 *
 *   Ojo: los records usan accessors sin el prefijo "get"
 *   (numero() en vez de getNumero()).
 */
public class FacturaA implements Factura {

    private final String numero;
    private final double neto;
    private final String cuitCliente;
    private final LocalDate fecha;

    public FacturaA(String numero, double neto, String cuitCliente, LocalDate fecha) {
        this.numero = numero;
        this.neto = neto;
        this.cuitCliente = cuitCliente;
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
        return "A";
    }

    @Override
    public LocalDate getFecha() {
        return fecha;
    }

    public String getCuitCliente() {
        return cuitCliente;
    }
}
