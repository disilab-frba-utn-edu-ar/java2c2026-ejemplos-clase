package ar.edu.utn.frba.facturacion.servicio;

import ar.edu.utn.frba.facturacion.modelo.Factura;
import ar.edu.utn.frba.facturacion.repositorio.FacturaRepositorio;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Logica de negocio sobre facturas: arma el resumen (para listados) y el
 * detalle (con IVA) de cada factura.
 *
 * Version LEGACY: recorre con bucles {@code for} y depende de que el
 * repositorio devuelva {@code null}.
 */
@Service
public class FacturaServicio {

    private final FacturaRepositorio repositorio;

    public FacturaServicio(FacturaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Resumen de todas las facturas.
     *
     * TODO PASO 4 (Streams): reemplazar el for por:
     *       return repositorio.buscarTodas().stream()
     *               .map(this::aResumen)
     *               .toList();
     */
    public List<Map<String, Object>> resumenDeTodas() {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Factura f : repositorio.buscarTodas()) {
            resultado.add(aResumen(f));
        }
        return resultado;
    }

    /**
     * Resumen de las facturas de un tipo.
     *
     * TODO PASO 4 (Streams): reemplazar el for por un map sobre el stream.
     */
    public List<Map<String, Object>> resumenPorTipo(String tipo) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Factura f : repositorio.buscarPorTipo(tipo)) {
            resultado.add(aResumen(f));
        }
        return resultado;
    }

    /**
     * Detalle (con IVA) de una factura, o null si no existe.
     *
     * TODO PASO 5 (Optional): cuando el repositorio devuelva
     *   Optional<Factura>, este metodo devuelve Optional<Map...> sin chequear
     *   null:
     *       return repositorio.buscarPorNumero(numero).map(this::aDetalle);
     */
    public Map<String, Object> detalle(String numero) {
        Factura factura = repositorio.buscarPorNumero(numero);
        if (factura == null) {
            // Manejo manual del null (facil de olvidar).
            return null;
        }
        return aDetalle(factura);
    }

    /** Resumen de una factura (sin IVA), para los listados. */
    private Map<String, Object> aResumen(Factura factura) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("numero", factura.getNumero());
        mapa.put("tipo", factura.getTipo());
        mapa.put("neto", factura.getNeto());
        return mapa;
    }

    /** Detalle de una factura, con el IVA calculado. */
    private Map<String, Object> aDetalle(Factura factura) {
        Map<String, Object> mapa = aResumen(factura);
        // TODO PASO 2 (Pattern Matching): cuando el calculo salga de las
        //   clases, el IVA se obtendra con CalculadoraIva.calcular(factura).
        mapa.put("iva", factura.calcularIva());
        return mapa;
    }
}
