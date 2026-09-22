package ar.edu.utn.frba.facturacion.modelo;

import java.time.LocalDate;

/**
 * Representa una factura de venta.
 *
 * Version MIGRADA: interface SEALED. Solo las implementaciones declaradas
 * en el permits son validas, lo que permite que los switch sobre Factura
 * sean EXHAUSTIVOS (sin default). El calculo del IVA ya no vive aca: se
 * resuelve por fuera con pattern matching en {@code CalculadoraIva}.
 *
 * Se declaran numero(), neto() y fecha(): los records las cumplen
 * automaticamente con sus accessors.
 */
public sealed interface Factura
        permits FacturaA, FacturaB, FacturaC, FacturaE {

    String numero();

    double neto();

    LocalDate fecha();
}
