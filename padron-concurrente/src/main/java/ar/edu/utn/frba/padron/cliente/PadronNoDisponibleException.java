package ar.edu.utn.frba.padron.cliente;

public class PadronNoDisponibleException extends RuntimeException {

    public PadronNoDisponibleException(String mensaje) {
        super(mensaje);
    }

    public PadronNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
