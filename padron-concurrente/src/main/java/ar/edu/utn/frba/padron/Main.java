package ar.edu.utn.frba.padron;

import ar.edu.utn.frba.padron.apidummy.PadronApiDummy;
import ar.edu.utn.frba.padron.cliente.PadronHttpAdapter;
import ar.edu.utn.frba.padron.comparacion.ComparadorDeEstrategias;
import ar.edu.utn.frba.padron.comparacion.ComparadorDeEstrategias.Estrategia;
import ar.edu.utn.frba.padron.validacion.ValidadorContribuyentes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Levanta la API dummy del padrón, valida N CUITs con cada estrategia y compara.
 *
 *   mvn compile exec:java                       → 50 CUITs
 *   mvn compile exec:java -Dexec.args="1000"    → 1000 CUITs
 *
 * Sin -q: exec:java corre dentro de la JVM de Maven y el log sale por el logger
 * de Maven, que con -q no muestra nada.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final Duration LATENCIA_API = Duration.ofMillis(250);
    private static final int CANTIDAD_POR_DEFECTO = 50;
    private static final int MAX_CUITS_SECUENCIAL = 50;
    private static final int LIMITE_BONUS = 50;

    public static void main(String[] args) throws IOException {
        int cantidad = args.length > 0 ? Integer.parseInt(args[0]) : CANTIDAD_POR_DEFECTO;

        try (PadronApiDummy api = new PadronApiDummy(0, LATENCIA_API)) {
            api.iniciar();
            ValidadorContribuyentes validador = new ValidadorContribuyentes(new PadronHttpAdapter(api.url()));

            log.info("API dummy en {} - {} ms por consulta - {} CUITs", api.url(), LATENCIA_API.toMillis(), cantidad);
            new ComparadorDeEstrategias(api).comparar(cuitsDeEjemplo(cantidad), estrategias(validador, cantidad));
        }
    }

    private static List<Estrategia> estrategias(ValidadorContribuyentes validador, int cantidad) {
        List<Estrategia> estrategias = new ArrayList<>();
        if (cantidad <= MAX_CUITS_SECUENCIAL) {
            estrategias.add(new Estrategia("Secuencial", validador::validarSecuencial));
        } else {
            log.info("Secuencial: no se corre, tarda ~{} s", cantidad * LATENCIA_API.toMillis() / 1000);
        }
        estrategias.add(new Estrategia("Pool de " + ValidadorContribuyentes.TAMANIO_POOL + " threads (actual)",
                validador::validarConPoolDeThreads));
        estrategias.add(new Estrategia("TODO 1 - Virtual threads", validador::validarConVirtualThreads));
        estrategias.add(new Estrategia("BONUS - Virtual threads (max. " + LIMITE_BONUS + ")",
                cuits -> validador.validarConVirtualThreadsLimitado(cuits, LIMITE_BONUS)));
        return estrategias;
    }

    /** CUITs de ejemplo: personas (20-) y empresas (30-), con algunos que el padrón no conoce. */
    private static List<String> cuitsDeEjemplo(int cantidad) {
        return IntStream.range(0, cantidad)
                .mapToObj(i -> (i % 4 == 0 ? "30" : "20") + "%08d".formatted(30_000_000 + i * 7) + (i % 10))
                .toList();
    }
}
