package ar.edu.utn.frba.padron.validacion;

import ar.edu.utn.frba.padron.validacion.ResultadoValidacion.Estado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidadorContribuyentesTest {

    private static final Duration LATENCIA = Duration.ofMillis(100);

    private final PadronFalso padron = new PadronFalso(LATENCIA);
    private final ValidadorContribuyentes validador = new ValidadorContribuyentes(padron);

    private static List<String> cuits(int cantidad) {
        return IntStream.range(0, cantidad)
                .mapToObj(i -> "20" + "%08d".formatted(40_000_000 + i) + (i % 10))
                .toList();
    }

    /** Lo que devolvería la versión secuencial, calculado con un padrón sin latencia para no esperar. */
    private static List<ResultadoValidacion> esperado(List<String> cuits) {
        return new ValidadorContribuyentes(new PadronFalso(Duration.ZERO)).validarSecuencial(cuits);
    }

    @Test
    @DisplayName("Secuencial: un resultado por CUIT, en orden, y nunca más de una consulta a la vez")
    void secuencial() {
        List<String> cuits = cuits(10);

        List<ResultadoValidacion> resultados = validador.validarSecuencial(cuits);

        assertEquals(cuits, resultados.stream().map(ResultadoValidacion::cuit).toList());
        assertEquals(Estado.NO_ENCONTRADO, resultados.get(0).estado());
        assertEquals(Estado.INACTIVO, resultados.get(1).estado());
        assertEquals(Estado.HABILITADO, resultados.get(2).estado());
        assertEquals(Estado.ERROR, resultados.get(9).estado());
        assertEquals(1, padron.maximoSimultaneos());
    }

    @Nested
    @DisplayName("Pool de threads (implementación actual)")
    class PoolDeThreads {

        @Test
        @DisplayName("devuelve lo mismo que la versión secuencial, en el mismo orden")
        void mismosResultados() {
            List<String> cuits = cuits(30);

            assertEquals(esperado(cuits), validador.validarConPoolDeThreads(cuits));
        }

        @Test
        @DisplayName("hace como mucho TAMANIO_POOL consultas a la vez (y más de una)")
        void respetaElTamanioDelPool() {
            validador.validarConPoolDeThreads(cuits(50));

            assertEquals(ValidadorContribuyentes.TAMANIO_POOL, padron.maximoSimultaneos());
        }
    }

    @Nested
    @DisplayName("TODO 1 · Virtual threads")
    class VirtualThreads {

        @Test
        @DisplayName("devuelve lo mismo que la versión secuencial, en el mismo orden")
        void mismosResultados() {
            List<String> cuits = cuits(30);

            assertEquals(esperado(cuits), validador.validarConVirtualThreads(cuits));
        }

        @Test
        @DisplayName("cada consulta corre en un virtual thread")
        void usaVirtualThreads() {
            validador.validarConVirtualThreads(cuits(30));

            assertEquals(0, padron.consultasEnThreadsDePlataforma(),
                    "Hubo consultas hechas desde threads de plataforma");
        }

        @Test
        @DisplayName("500 consultas de 100 ms terminan en menos de 3 segundos (secuencial serían 50 s)")
        void escalaSinPool() {
            List<ResultadoValidacion> resultados = assertTimeout(Duration.ofSeconds(3),
                    () -> validador.validarConVirtualThreads(cuits(500)));

            assertEquals(500, resultados.size());
            assertTrue(padron.maximoSimultaneos() > ValidadorContribuyentes.TAMANIO_POOL,
                    "Con virtual threads debería haber muchas más consultas en vuelo que threads en el pool");
        }
    }

    @Nested
    @DisplayName("BONUS · Virtual threads con límite")
    class VirtualThreadsLimitado {

        @Test
        @DisplayName("devuelve lo mismo que la versión secuencial, en el mismo orden")
        void mismosResultados() {
            List<String> cuits = cuits(100);

            assertEquals(esperado(cuits), validador.validarConVirtualThreadsLimitado(cuits, 25));
        }

        @Test
        @DisplayName("llega al límite de consultas a la vez, pero nunca lo supera")
        void respetaElLimite() {
            validador.validarConVirtualThreadsLimitado(cuits(100), 25);

            assertEquals(25, padron.maximoSimultaneos(), "Consultas a la vez");
        }

        @Test
        @DisplayName("sigue usando virtual threads: el límite no sale de un pool")
        void usaVirtualThreads() {
            validador.validarConVirtualThreadsLimitado(cuits(100), 25);

            assertEquals(0, padron.consultasEnThreadsDePlataforma(),
                    "Hubo consultas hechas desde threads de plataforma");
        }
    }
}
