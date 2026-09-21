package ar.edu.utn.frba.facturacion.servicio;

import ar.edu.utn.frba.facturacion.modelo.Factura;
import ar.edu.utn.frba.facturacion.repositorio.FacturaRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Logica de negocio sobre facturas.
 *
 * Version LEGACY: acumula con bucles {@code for} y depende de que el
 * repositorio devuelva {@code null}.
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
     * Suma el IVA de todas las facturas.
     *
     * TODO PASO 5 (Streams): reemplazar el for acumulador por:
     *       return repositorio.buscarTodas().stream()
     *               .mapToDouble(Factura::calcularIva)
     *               .sum();
     *
     * TODO PASO 3 (Pattern Matching): cuando el calculo del IVA salga
     *   de las clases, aca se usara CalculadoraIva.calcular(f) en lugar
     *   de f.calcularIva().
     */
    public double calcularTotalIva() {
        double total = 0.0;
        for (Factura f : repositorio.buscarTodas()) {
            total += f.calcularIva();
        }
        return total;
    }

    /**
     * Busca una factura por numero.
     *
     * TODO PASO 6 (Optional): cuando el repositorio devuelva
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
