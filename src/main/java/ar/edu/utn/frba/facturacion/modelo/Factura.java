package ar.edu.utn.frba.facturacion.modelo;

/**
 * Representa una factura de venta.
 *
 * Version LEGACY: interface comun implementada por clases concretas
 * (FacturaA, FacturaB, FacturaC) que resuelven el calculo del IVA
 * mediante POLIMORFISMO (cada clase pisa {@link #calcularIva()}).
 *
 * ============================================================
 * TODO PASO 3 (Sealed): convertir esta interface en SEALED y
 *   permitir SOLO las implementaciones conocidas, p. ej.:
 *
 *       public sealed interface Factura
 *               permits FacturaA, FacturaB, FacturaC { ... }
 *
 *   Ver la CONSIGNA.md para el detalle de cada paso.
 * ============================================================
 */
public interface Factura {

    /** Numero identificatorio de la factura (ej. "A-0001"). */
    String getNumero();

    /** Importe neto (base imponible) sobre el que se calcula el IVA. */
    double getNeto();

    /** Tipo de factura como texto: "A", "B" o "C". */
    String getTipo();

    /**
     * Calcula el IVA de la factura.
     *
     * TODO PASO 2 (Pattern Matching): una vez que A/B/C sean records,
     *   este calculo NO va a vivir mas dentro de cada clase. Se va a
     *   resolver por fuera, con un switch + pattern matching sobre el
     *   tipo de factura (ver CONSIGNA.md, paso de pattern matching).
     *   Este metodo se elimina de la interface en ese paso.
     */
    double calcularIva();
}
