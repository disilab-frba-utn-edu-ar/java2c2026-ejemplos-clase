package ar.edu.utn.frba.facturacion.servicio;

import ar.edu.utn.frba.facturacion.modelo.CalculadoraIva;
import ar.edu.utn.frba.facturacion.modelo.Factura;
import ar.edu.utn.frba.facturacion.repositorio.FacturaRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Logica de negocio sobre facturas.
 *
 * Version MIGRADA: acumula con Streams y propaga Optional.
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

    /** Suma el IVA de todas las facturas con Streams + pattern matching. */
    public double calcularTotalIva() {
        return repositorio.buscarTodas().stream()
                .mapToDouble(CalculadoraIva::calcular)
                .sum();
    }

    /** Busca una factura por numero; propaga el Optional del repositorio. */
    public Optional<Factura> buscarPorNumero(String numero) {
        return repositorio.buscarPorNumero(numero);
    }
}
