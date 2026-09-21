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
 *   GET http://localhost:8080/facturas
 *   GET http://localhost:8080/facturas/A-0001
 *   GET http://localhost:8080/facturas/total-iva
 *   GET http://localhost:8080/facturas/tipo/A
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
                .map(this::aMapa)
                .toList();
    }

    @GetMapping("/tipo/{tipo}")
    public List<Map<String, Object>> listarPorTipo(@PathVariable String tipo) {
        return servicio.listarPorTipo(tipo).stream()
                .map(this::aMapa)
                .toList();
    }

    @GetMapping("/total-iva")
    public Map<String, Object> totalIva() {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("totalIva", servicio.calcularTotalIva());
        return respuesta;
    }

    @GetMapping("/{numero}")
    public ResponseEntity<Map<String, Object>> buscarPorNumero(@PathVariable String numero) {
        return servicio.buscarPorNumero(numero)
                .map(f -> ResponseEntity.ok(aMapa(f)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Arma la representacion JSON de una factura, incluyendo su IVA. */
    private Map<String, Object> aMapa(Factura factura) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("numero", factura.numero());
        mapa.put("tipo", CalculadoraIva.tipo(factura));
        mapa.put("neto", factura.neto());
        mapa.put("iva", CalculadoraIva.calcular(factura));
        return mapa;
    }
}
