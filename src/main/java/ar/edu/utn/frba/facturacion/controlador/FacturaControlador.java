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
 *   GET http://localhost:8080/facturas          -> listado (resumen)
 *   GET http://localhost:8080/facturas/tipo/A    -> listado por tipo (resumen)
 *   GET http://localhost:8080/facturas/A-0001     -> detalle (incluye el IVA)
 *
 * El IVA se calcula por factura y se muestra en el detalle de una factura.
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
            respuesta.add(aResumen(f));
        }
        return respuesta;
    }

    @GetMapping("/tipo/{tipo}")
    public List<Map<String, Object>> listarPorTipo(@PathVariable String tipo) {
        List<Map<String, Object>> respuesta = new java.util.ArrayList<>();
        for (Factura f : servicio.listarPorTipo(tipo)) {
            respuesta.add(aResumen(f));
        }
        return respuesta;
    }

    /**
     * Detalle de una factura, incluido su IVA.
     *
     * TODO PASO 5 (Optional): cuando el servicio devuelva
     *   Optional<Factura>, este metodo se vuelve mas expresivo:
     *
     *       return servicio.buscarPorNumero(numero)
     *               .map(f -> ResponseEntity.ok(aDetalle(f)))
     *               .orElse(ResponseEntity.notFound().build());
     */
    @GetMapping("/{numero}")
    public ResponseEntity<Map<String, Object>> buscarPorNumero(@PathVariable String numero) {
        Factura factura = servicio.buscarPorNumero(numero);
        // Chequeo manual del null (lo que Optional viene a mejorar).
        if (factura == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(aDetalle(factura));
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
