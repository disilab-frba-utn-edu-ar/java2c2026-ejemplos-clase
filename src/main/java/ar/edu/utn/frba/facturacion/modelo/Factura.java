package ar.edu.utn.frba.facturacion.modelo;

import java.time.LocalDate;

/**
 *
 * ============================================================
 * TODO PASO 3 (Sealed): convertir esta interface en SEALED y
 *   permitir SOLO las implementaciones conocidas
 * ============================================================
 */
public interface Factura {

    String getNumero();

    double getNeto();

    String getTipo();

    LocalDate getFecha();

    double calcularIva();
}
