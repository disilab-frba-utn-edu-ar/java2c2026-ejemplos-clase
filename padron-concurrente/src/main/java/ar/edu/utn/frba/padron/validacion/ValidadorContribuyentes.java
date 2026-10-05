package ar.edu.utn.frba.padron.validacion;

import ar.edu.utn.frba.padron.cliente.PadronClient;
import ar.edu.utn.frba.padron.cliente.PadronNoDisponibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Valida una lista de CUITs contra el padrón.
 *
 * Solo conoce a {@link PadronClient}: no sabe si del otro lado hay una API HTTP,
 * una base de datos o un fake de test. Eso lo resuelve el adapter.
 */
public class ValidadorContribuyentes {

    private static final Logger log = LoggerFactory.getLogger(ValidadorContribuyentes.class);

    public static final int TAMANIO_POOL = 10;

    private final PadronClient padron;

    public ValidadorContribuyentes(PadronClient padron) {
        this.padron = padron;
    }

    /**
     * Versión de referencia: un CUIT atrás del otro, en el thread que llama.
     * Si cada consulta tarda 250 ms, 100 CUITs tardan 25 segundos.
     */
    public List<ResultadoValidacion> validarSecuencial(List<String> cuits) {
        return cuits.stream()
                .map(this::evaluar)
                .toList();
    }

    /**
     * Cómo está resuelto hoy: un pool fijo de TAMANIO_POOL threads de plataforma.
     *
     * Primero se lanza una tarea por CUIT (cada submit devuelve un Future, una
     * "promesa" del resultado) y recién después se espera cada resultado, en orden.
     */
    public List<ResultadoValidacion> validarConPoolDeThreads(List<String> cuits) {
        ExecutorService executor = Executors.newFixedThreadPool(TAMANIO_POOL);
        try {
            List<Future<ResultadoValidacion>> futuros = new ArrayList<>();
            for (String cuit : cuits) {
                futuros.add(executor.submit(() -> evaluar(cuit)));
            }
            List<ResultadoValidacion> resultados = new ArrayList<>();
            for (Future<ResultadoValidacion> futuro : futuros) {
                resultados.add(esperar(futuro));
            }
            return resultados;
        } finally {
            executor.shutdown();
        }
    }

    /**
     * TODO 1: la misma validación, pero con un virtual thread por consulta.
     *
     * Para verificar: ValidadorContribuyentesTest > VirtualThreads.
     */
    public List<ResultadoValidacion> validarConVirtualThreads(List<String> cuits) {
        throw new UnsupportedOperationException("TODO 1");
    }

    // TODO 2: comparar los dos métodos de arriba. ¿Cuánto código cambió? ¿Qué tienen
    //  en común? Dejarlos como se escribirían hoy en Java 21, sin código duplicado.
    //  ¿Sigue haciendo falta el try/finally con shutdown()?
    //  Los tests tienen que seguir pasando.

    /**
     * TODO BONUS: con virtual threads, pero sin superar maxSimultaneos consultas a la vez
     * (supongamos que el padrón real solo acepta 50 pedidos simultáneos por cliente).
     * Volver a un pool de threads de plataforma no vale.
     *
     * Para verificar: ValidadorContribuyentesTest > VirtualThreadsLimitado.
     */
    public List<ResultadoValidacion> validarConVirtualThreadsLimitado(List<String> cuits, int maxSimultaneos) {
        throw new UnsupportedOperationException("BONUS");
    }

    /**
     * Consulta UN CUIT y lo traduce a un resultado. Es la unidad de trabajo que
     * conviene mandar a cada thread. Nunca lanza excepción: si el padrón falla,
     * devuelve un resultado de ERROR.
     */
    private ResultadoValidacion evaluar(String cuit) {
        try {
            return padron.consultar(cuit)
                    .map(ResultadoValidacion::de)
                    .orElseGet(() -> ResultadoValidacion.noEncontrado(cuit));
        } catch (PadronNoDisponibleException e) {
            log.warn("No se pudo validar el CUIT {}: {}", cuit, e.getMessage());
            return ResultadoValidacion.error(cuit, e);
        }
    }

    /**
     * Espera el resultado de una tarea lanzada en un executor.
     * Future.get() obliga a manejar dos excepciones chequeadas.
     */
    private ResultadoValidacion esperar(Future<ResultadoValidacion> futuro) {
        try {
            return futuro.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Se interrumpió la espera de un resultado", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Falló una consulta al padrón", e.getCause());
        }
    }
}
