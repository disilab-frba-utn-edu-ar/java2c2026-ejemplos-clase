package ar.edu.utn.frba.facturacion.controlador;

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
 * Probar (con la app corriendo en el puerto 8080):
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
        List<Map<String, Object>> respuesta = new java.util.ArrayList<>();
        for (Factura f : servicio.listarTodas()) {
            respuesta.add(aMapa(f));
        }
        return respuesta;
    }

    @GetMapping("/tipo/{tipo}")
    public List<Map<String, Object>> listarPorTipo(@PathVariable String tipo) {
        List<Map<String, Object>> respuesta = new java.util.ArrayList<>();
        for (Factura f : servicio.listarPorTipo(tipo)) {
            respuesta.add(aMapa(f));
        }
        return respuesta;
    }

    @GetMapping("/total-iva")
    public Map<String, Object> totalIva() {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("totalIva", servicio.calcularTotalIva());
        return respuesta;
    }

    /**
     * Busca una factura por numero.
     *
     * TODO PASO 5 (Optional): cuando el servicio devuelva
     *   Optional<Factura>, este metodo se vuelve mas expresivo:
     *
     *       return servicio.buscarPorNumero(numero)
     *               .map(f -> ResponseEntity.ok(aMapa(f)))
     *               .orElse(ResponseEntity.notFound().build());
     */
    @GetMapping("/{numero}")
    public ResponseEntity<Map<String, Object>> buscarPorNumero(@PathVariable String numero) {
        Factura factura = servicio.buscarPorNumero(numero);
        // Chequeo manual del null (lo que Optional viene a mejorar).
        if (factura == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(aMapa(factura));
    }

    /** Arma la representacion JSON de una factura, incluyendo su IVA. */
    private Map<String, Object> aMapa(Factura factura) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("numero", factura.getNumero());
        mapa.put("tipo", factura.getTipo());
        mapa.put("neto", factura.getNeto());
        mapa.put("iva", factura.calcularIva());
        return mapa;
    }
}
