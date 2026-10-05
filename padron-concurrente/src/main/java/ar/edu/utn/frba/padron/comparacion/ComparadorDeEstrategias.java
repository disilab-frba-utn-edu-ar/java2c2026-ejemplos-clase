package ar.edu.utn.frba.padron.comparacion;

import ar.edu.utn.frba.padron.apidummy.PadronApiDummy;
import ar.edu.utn.frba.padron.validacion.ResultadoValidacion;
import ar.edu.utn.frba.padron.validacion.ResultadoValidacion.Estado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Corre cada estrategia de validación contra la API dummy y loguea cuánto tardó,
 * cuántas consultas atendió la API a la vez y qué resultados devolvió.
 *
 * No hace falta modificar esta clase para el ejercicio.
 */
public class ComparadorDeEstrategias {

    private static final Logger log = LoggerFactory.getLogger(ComparadorDeEstrategias.class);

    /** Una forma de validar una lista de CUITs, con el nombre que se muestra en el log. */
    public record Estrategia(String nombre, Function<List<String>, List<ResultadoValidacion>> validar) {
    }

    private final PadronApiDummy api;

    public ComparadorDeEstrategias(PadronApiDummy api) {
        this.api = api;
    }

    public void comparar(List<String> cuits, List<Estrategia> estrategias) {
        estrategias.forEach(estrategia -> medir(estrategia, cuits));
    }

    private void medir(Estrategia estrategia, List<String> cuits) {
        String nombre = "%-34s".formatted(estrategia.nombre());
        api.reiniciarEstadisticas();
        long inicio = System.nanoTime();
        try {
            List<ResultadoValidacion> resultados = estrategia.validar().apply(cuits);
            Duration tiempo = Duration.ofNanos(System.nanoTime() - inicio);
            log.info("{} {} s - {} consultas en paralelo - {}",
                    nombre, "%6.2f".formatted(tiempo.toMillis() / 1000.0), api.maximoSimultaneos(), resumen(resultados));
        } catch (UnsupportedOperationException e) {
            log.warn("{} pendiente ({})", nombre, e.getMessage());
        }
    }

    /** Ej.: "38 HABILITADO | 5 INACTIVO | 7 NO_ENCONTRADO", en el orden del enum. */
    private static String resumen(List<ResultadoValidacion> resultados) {
        Map<Estado, Long> porEstado = resultados.stream()
                .collect(Collectors.groupingBy(ResultadoValidacion::estado,
                        () -> new EnumMap<>(Estado.class), Collectors.counting()));
        return porEstado.entrySet().stream()
                .map(e -> e.getValue() + " " + e.getKey())
                .collect(Collectors.joining(" | "));
    }
}
