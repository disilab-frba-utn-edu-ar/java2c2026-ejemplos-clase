package ar.edu.utn.frba.facturacion.servicio;

import ar.edu.utn.frba.facturacion.modelo.Factura;
import ar.edu.utn.frba.facturacion.modelo.FacturaA;
import ar.edu.utn.frba.facturacion.modelo.FacturaB;
import ar.edu.utn.frba.facturacion.repositorio.FacturaRepositorio;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FacturaServicio {

    private static final double ALICUOTA_GENERAL = 0.21;

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
     *
     * TODO PASO 4 (Streams): reemplazar el for con los if anidados por una
     *   cadena de filtros sobre el stream.
     */
    public List<Map<String, Object>> buscar(String tipo, LocalDate desde,
                                            LocalDate hasta, double montoMinimo) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Factura f : repositorio.buscarTodas()) {
            if (f.getTipo().equals(tipo)) {
                if (!f.getFecha().isBefore(desde) && !f.getFecha().isAfter(hasta)) {
                    if (f.getNeto() > montoMinimo) {
                        resultado.add(aResumen(f));
                    }
                }
            }
        }
        return resultado;
    }

    /**
     *
     * TODO PASO 5 (Optional)
     */
    public Map<String, Object> detalle(String numero) {
        Factura factura = repositorio.buscarPorNumero(numero);
        if (factura == null) {
            return null;
        }
        return aDetalle(factura);
    }


    private Map<String, Object> aResumen(Factura factura) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("numero", factura.getNumero());
        mapa.put("tipo", factura.getTipo());
        mapa.put("neto", factura.getNeto());
        mapa.put("fecha", factura.getFecha());
        return mapa;
    }


    private Map<String, Object> aDetalle(Factura factura) {
        Map<String, Object> mapa = aResumen(factura);
        mapa.put("iva", calcularIva(factura));
        return mapa;
    }

    /**
     * Calcula el IVA segun el tipo de factura (ver tabla en CONSIGNA.md).
     *
     * TODO PASO 2 (Pattern Matching): reemplazar la cadena de if/else por un
     *   switch con pattern matching sobre el tipo de factura.
     */
    private double calcularIva(Factura factura) {
        double iva;
        if (factura.getClass().getSimpleName().equals("FacturaA")) {
            FacturaA facturaA = (FacturaA) factura;
            iva = facturaA.getNeto() * ALICUOTA_GENERAL;
        } else if (factura.getClass().getSimpleName().equals("FacturaB")) {
            FacturaB facturaB = (FacturaB) factura;
            iva = facturaB.getNeto() * ALICUOTA_GENERAL;
        } else if (factura.getClass().getSimpleName().equals("FacturaC")) {
            iva = 0.0;
        } else {
            throw new IllegalArgumentException(
                    "Tipo de factura desconocido: " + factura.getClass().getSimpleName());
        }
        return iva;
    }
}
