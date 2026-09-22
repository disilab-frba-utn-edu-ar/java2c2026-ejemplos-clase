package ar.edu.utn.frba.facturacion.servicio;

import ar.edu.utn.frba.facturacion.modelo.CalculadoraIva;
import ar.edu.utn.frba.facturacion.modelo.Factura;
import ar.edu.utn.frba.facturacion.repositorio.FacturaRepositorio;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Logica de negocio sobre facturas: arma el resumen (para listados) y el
 * detalle (con IVA) de cada factura.
 *
 * Version MIGRADA: consulta con Streams y propaga Optional.
 */
@Service
public class FacturaServicio {

    private final FacturaRepositorio repositorio;

    public FacturaServicio(FacturaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /** Resumen de todas las facturas. */
    public List<Map<String, Object>> resumenDeTodas() {
        return repositorio.buscarTodas().stream()
                .map(this::aResumen)
                .toList();
    }

    /** Resumen de las facturas de un tipo. */
    public List<Map<String, Object>> resumenPorTipo(String tipo) {
        return repositorio.buscarPorTipo(tipo).stream()
                .map(this::aResumen)
                .toList();
    }

    /**
     * Busca facturas de un tipo, emitidas dentro de un rango de fechas
     * [desde, hasta] (inclusive) y con neto MAYOR a un monto minimo.
     * Cada condicion es un filter encadenado sobre el stream.
     */
    public List<Map<String, Object>> buscar(String tipo, LocalDate desde,
                                            LocalDate hasta, double montoMinimo) {
        return repositorio.buscarTodas().stream()
                .filter(f -> CalculadoraIva.tipo(f).equals(tipo))
                .filter(f -> !f.fecha().isBefore(desde))
                .filter(f -> !f.fecha().isAfter(hasta))
                .filter(f -> f.neto() > montoMinimo)
                .map(this::aResumen)
                .toList();
    }

    /** Detalle (con IVA) de una factura, vacio si no existe. */
    public Optional<Map<String, Object>> detalle(String numero) {
        return repositorio.buscarPorNumero(numero)
                .map(this::aDetalle);
    }

    /** Resumen de una factura (sin IVA), para los listados. */
    private Map<String, Object> aResumen(Factura factura) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("numero", factura.numero());
        mapa.put("tipo", CalculadoraIva.tipo(factura));
        mapa.put("neto", factura.neto());
        mapa.put("fecha", factura.fecha());
        return mapa;
    }

    /** Detalle de una factura, con el IVA calculado por pattern matching. */
    private Map<String, Object> aDetalle(Factura factura) {
        Map<String, Object> mapa = aResumen(factura);
        mapa.put("iva", CalculadoraIva.calcular(factura));
        return mapa;
    }
}
