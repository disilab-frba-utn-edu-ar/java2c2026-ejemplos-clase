package ar.edu.utn.frba.padron.dominio;

/**
 * Cómo ve NUESTRO sistema a un contribuyente. No sabe nada de cómo lo
 * devuelve la API del padrón: esa traducción la hace el adapter.
 */
public record Contribuyente(String cuit, String razonSocial, CondicionIva condicionIva, boolean activo) {
}
