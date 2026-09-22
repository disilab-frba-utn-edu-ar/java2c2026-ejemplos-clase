package ar.edu.utn.frba.facturacion.controlador;

import ar.edu.utn.frba.facturacion.servicio.FacturaServicio;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Endpoints REST para consultar facturas. Delega el armado de la respuesta
 * en {@link FacturaServicio}.
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
        return servicio.resumenDeTodas();
    }

    @GetMapping("/tipo/{tipo}")
    public List<Map<String, Object>> listarPorTipo(@PathVariable String tipo) {
        return servicio.resumenPorTipo(tipo);
    }

    /**
     * Busqueda: facturas de un tipo, en un rango de fechas y con neto mayor
     * a un minimo. Ej:
     *   /facturas/buscar?tipo=A&desde=2026-01-01&hasta=2026-03-31&montoMinimo=8000
     */
    @GetMapping("/buscar")
    public List<Map<String, Object>> buscar(
            @RequestParam String tipo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam double montoMinimo) {
        return servicio.buscar(tipo, desde, hasta, montoMinimo);
    }

    @GetMapping("/{numero}")
    public ResponseEntity<Map<String, Object>> buscarPorNumero(@PathVariable String numero) {
        return servicio.detalle(numero)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
