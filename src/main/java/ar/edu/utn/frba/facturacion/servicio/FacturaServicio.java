package ar.edu.utn.frba.facturacion.servicio;

import ar.edu.utn.frba.facturacion.modelo.Factura;
import ar.edu.utn.frba.facturacion.repositorio.FacturaRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Logica de negocio sobre facturas.
 *
 * Version LEGACY: depende de que el repositorio devuelva {@code null}
 * cuando no encuentra la factura.
 */
@Service
public class FacturaServicio {

    private final FacturaRepositorio repositorio;

    public FacturaServicio(FacturaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<Factura> listarTodas() {
        return repositorio.buscarTodas();
    }

    public List<Factura> listarPorTipo(String tipo) {
        return repositorio.buscarPorTipo(tipo);
    }

    /**
     * Busca una factura por numero.
     *
     * TODO PASO 5 (Optional): cuando el repositorio devuelva
     *   Optional<Factura>, este metodo puede propagarlo:
     *       return repositorio.buscarPorNumero(numero);
     *   y el controlador decide que responder si esta vacio.
     */
    public Factura buscarPorNumero(String numero) {
        Factura factura = repositorio.buscarPorNumero(numero);
        if (factura == null) {
            // Manejo manual del null (facil de olvidar).
            return null;
        }
        return factura;
    }
}
