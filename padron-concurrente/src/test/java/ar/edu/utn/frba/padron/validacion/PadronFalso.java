package ar.edu.utn.frba.padron.validacion;

import ar.edu.utn.frba.padron.dominio.CondicionIva;
import ar.edu.utn.frba.padron.dominio.Contribuyente;
import ar.edu.utn.frba.padron.cliente.PadronClient;
import ar.edu.utn.frba.padron.cliente.PadronNoDisponibleException;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Padrón en memoria para los tests. Gracias a que el validador depende de la
 * interfaz {@link PadronClient} (y no del adapter HTTP), se puede reemplazar
 * por esto sin levantar ninguna API.
 *
 * CUITs que terminan en 0 → no existen · en 1 → inactivos · en 9 → el padrón falla.
 */
class PadronFalso implements PadronClient {

    private final Duration latencia;
    private final AtomicInteger enCurso = new AtomicInteger();
    private final AtomicInteger maximoSimultaneos = new AtomicInteger();
    private final AtomicInteger enThreadsDePlataforma = new AtomicInteger();

    PadronFalso(Duration latencia) {
        this.latencia = latencia;
    }

    @Override
    public Optional<Contribuyente> consultar(String cuit) {
        if (!Thread.currentThread().isVirtual()) {
            enThreadsDePlataforma.incrementAndGet();
        }
        maximoSimultaneos.accumulateAndGet(enCurso.incrementAndGet(), Math::max);
        try {
            Thread.sleep(latencia);
            return switch (cuit.charAt(cuit.length() - 1)) {
                case '0' -> Optional.empty();
                case '9' -> throw new PadronNoDisponibleException("503 para " + cuit);
                default -> Optional.of(new Contribuyente(cuit, "CONTRIBUYENTE " + cuit,
                        CondicionIva.RESPONSABLE_INSCRIPTO, !cuit.endsWith("1")));
            };
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PadronNoDisponibleException("interrumpido", e);
        } finally {
            enCurso.decrementAndGet();
        }
    }

    int maximoSimultaneos() {
        return maximoSimultaneos.get();
    }

    /** Cuántas consultas se hicieron desde un thread de plataforma (no virtual). */
    int consultasEnThreadsDePlataforma() {
        return enThreadsDePlataforma.get();
    }
}
