package ar.edu.utn.frba.padron.validacion;

import ar.edu.utn.frba.padron.dominio.Contribuyente;

public record ResultadoValidacion(String cuit, Estado estado, String detalle) {

    public enum Estado {
        HABILITADO,
        INACTIVO,
        NO_ENCONTRADO,
        ERROR
    }

    static ResultadoValidacion de(Contribuyente c) {
        Estado estado = c.activo() ? Estado.HABILITADO : Estado.INACTIVO;
        return new ResultadoValidacion(c.cuit(), estado, c.razonSocial() + " (" + c.condicionIva() + ")");
    }

    static ResultadoValidacion noEncontrado(String cuit) {
        return new ResultadoValidacion(cuit, Estado.NO_ENCONTRADO, "El padrón no conoce este CUIT");
    }

    static ResultadoValidacion error(String cuit, Exception e) {
        return new ResultadoValidacion(cuit, Estado.ERROR, e.getMessage());
    }
}
