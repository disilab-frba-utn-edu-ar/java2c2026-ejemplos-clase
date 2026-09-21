package ar.edu.utn.frba.facturacion.modelo;

/**
 * Resuelve el calculo del IVA (y datos derivados) con PATTERN MATCHING.
 *
 * Como {@link Factura} es sealed, los switch son exhaustivos: no hace falta
 * (ni conviene) un {@code default}. Si maniana se agrega o quita un tipo del
 * permits de Factura, estos switch dejan de compilar y el compilador obliga
 * a contemplar el cambio.
 */
public final class CalculadoraIva {

    private static final double ALICUOTA_GENERAL = 0.21;

    private CalculadoraIva() {
    }

    /** Calcula el IVA segun el tipo de factura. */
    public static double calcular(Factura factura) {
        return switch (factura) {
            case FacturaA a -> a.neto() * ALICUOTA_GENERAL; // Resp. Inscripto: 21% discriminado
            case FacturaB b -> b.neto() * ALICUOTA_GENERAL; // Consumidor Final: 21% incluido
            case FacturaC c -> 0.0;                          // Monotributista: sin IVA
            case FacturaE e -> 0.0;                          // Exportacion: exenta
        };
    }

    /** Tipo de factura como texto: "A", "B", "C" o "E". */
    public static String tipo(Factura factura) {
        return switch (factura) {
            case FacturaA a -> "A";
            case FacturaB b -> "B";
            case FacturaC c -> "C";
            case FacturaE e -> "E";
        };
    }

    /** Descripcion legible de la factura. */
    public static String descripcion(Factura factura) {
        return switch (factura) {
            case FacturaA a -> "Factura A a Responsable Inscripto";
            case FacturaB b -> "Factura B a Consumidor Final";
            case FacturaC c -> "Factura C de Monotributista";
            case FacturaE e -> "Factura E de Exportacion a " + e.paisDestino();
        };
    }
}
