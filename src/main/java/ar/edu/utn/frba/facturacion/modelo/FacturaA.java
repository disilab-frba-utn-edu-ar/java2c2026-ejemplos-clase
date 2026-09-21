package ar.edu.utn.frba.facturacion.modelo;

/**
 * Factura tipo A: emitida a un Responsable Inscripto.
 * El IVA se DISCRIMINA y se calcula al 21% sobre el neto.
 *
 * TODO PASO 2 (Records): convertir esta clase en un record.
 *   Un record es inmutable y genera constructor, getters, equals,
 *   hashCode y toString automaticamente. Ejemplo del objetivo:
 *
 *       public record FacturaA(String numero, double neto,
 *                              String cuitCliente) implements Factura { }
 *
 *   Ojo: los records usan accessors sin el prefijo "get"
 *   (numero() en vez de getNumero()).
 */
public class FacturaA implements Factura {

    private static final double ALICUOTA = 0.21;

    private final String numero;
    private final double neto;
    private final String cuitCliente;

    public FacturaA(String numero, double neto, String cuitCliente) {
        this.numero = numero;
        this.neto = neto;
        this.cuitCliente = cuitCliente;
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

    public String getCuitCliente() {
        return cuitCliente;
    }

    @Override
    public double calcularIva() {
        // Responsable Inscripto: IVA 21% discriminado sobre el neto.
        return neto * ALICUOTA;
    }
}
