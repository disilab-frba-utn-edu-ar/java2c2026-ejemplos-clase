package ar.edu.utn.frba.facturacion.controlador;

import ar.edu.utn.frba.facturacion.modelo.CalculadoraIva;
import ar.edu.utn.frba.facturacion.modelo.Factura;
import ar.edu.utn.frba.facturacion.servicio.FacturaServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoints REST para consultar facturas.
 *
 * Version MIGRADA: usa Streams para armar las respuestas y Optional para
 * resolver el caso "no encontrada" sin chequear null.
 *
 *   GET http://localhost:8080/facturas          -> listado (resumen)
 *   GET http://localhost:8080/facturas/tipo/A    -> listado por tipo (resumen)
 *   GET http://localhost:8080/facturas/A-0001     -> detalle (incluye el IVA)
 */
@RestController
@RequestMapping("/facturas")
public class FacturaControlador {

    private final FacturaServicio servicio;

    public FacturaControlador(FacturaServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Map<String, Object>> listarTodas() {
        return servicio.listarTodas().stream()
                .map(this::aResumen)
                .toList();
    }

    @GetMapping("/tipo/{tipo}")
    public List<Map<String, Object>> listarPorTipo(@PathVariable String tipo) {
        return servicio.listarPorTipo(tipo).stream()
                .map(this::aResumen)
                .toList();
    }

    @GetMapping("/{numero}")
    public ResponseEntity<Map<String, Object>> buscarPorNumero(@PathVariable String numero) {
        return servicio.buscarPorNumero(numero)
                .map(f -> ResponseEntity.ok(aDetalle(f)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Resumen de una factura (sin IVA), para los listados. */
    private Map<String, Object> aResumen(Factura factura) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("numero", factura.numero());
        mapa.put("tipo", CalculadoraIva.tipo(factura));
        mapa.put("neto", factura.neto());
        return mapa;
    }

    /** Detalle de una factura, con el IVA calculado por pattern matching. */
    private Map<String, Object> aDetalle(Factura factura) {
        Map<String, Object> mapa = aResumen(factura);
        mapa.put("iva", CalculadoraIva.calcular(factura));
        return mapa;
    }
}
