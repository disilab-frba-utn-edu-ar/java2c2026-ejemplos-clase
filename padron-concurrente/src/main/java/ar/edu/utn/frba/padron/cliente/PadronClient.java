package ar.edu.utn.frba.padron.cliente;

import ar.edu.utn.frba.padron.dominio.Contribuyente;

import java.util.Optional;

/**
 * La interfaz que NUESTRO código necesita para consultar el padrón.
 *
 * El validador solo conoce esta interfaz. Si mañana la API cambia de formato,
 * de protocolo o de proveedor, se reescribe el adapter y nada más.
 */
public interface PadronClient {

    /**
     * Consulta un contribuyente por CUIT. Es una llamada sincrónica y bloqueante:
     * el thread que la invoca queda esperando la respuesta de la red.
     *
     * @return el contribuyente, o vacío si el padrón no lo conoce
     * @throws PadronNoDisponibleException si el padrón no responde o responde con error
     */
    Optional<Contribuyente> consultar(String cuit);
}
