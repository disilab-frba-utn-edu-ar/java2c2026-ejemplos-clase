package ar.edu.utn.frba.facturacion.controlador;

import ar.edu.utn.frba.facturacion.servicio.FacturaServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Endpoints REST para consultar facturas. Delega el armado de la respuesta
 * en {@link FacturaServicio}.
 *
 * Probar (con la app corriendo en el puerto 8080):
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
        return servicio.resumenDeTodas();
    }

    @GetMapping("/tipo/{tipo}")
    public List<Map<String, Object>> listarPorTipo(@PathVariable String tipo) {
        return servicio.resumenPorTipo(tipo);
    }

    /**
     * Detalle de una factura, incluido su IVA.
     *
     * TODO PASO 5 (Optional): cuando el servicio devuelva Optional<Map...>,
     *   este metodo se vuelve mas expresivo:
     *
     *       return servicio.detalle(numero)
     *               .map(ResponseEntity::ok)
     *               .orElseGet(() -> ResponseEntity.notFound().build());
     */
    @GetMapping("/{numero}")
    public ResponseEntity<Map<String, Object>> buscarPorNumero(@PathVariable String numero) {
        Map<String, Object> detalle = servicio.detalle(numero);
        // Chequeo manual del null (lo que Optional viene a mejorar).
        if (detalle == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detalle);
    }
}
