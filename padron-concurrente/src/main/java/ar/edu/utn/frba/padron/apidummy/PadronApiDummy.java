package ar.edu.utn.frba.padron.apidummy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * API dummy del padrón de contribuyentes. Simula un servicio externo lento:
 * cada pedido tarda {@code latencia} en responder.
 *
 *   GET /padron/personas/{cuit}   → 200 con el JSON de la persona, o 404 si no existe
 *
 * Los datos se generan a partir del CUIT, así que siempre responde lo mismo
 * para el mismo CUIT. Además lleva la cuenta de cuántos pedidos atendió en
 * simultáneo, para ver qué tan concurrente fue el cliente.
 *
 * No hace falta modificar esta clase para el ejercicio. Se puede levantar
 * sola (ver {@link #main}) para probarla con el navegador o con curl.
 */
public class PadronApiDummy implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(PadronApiDummy.class);

    private static final String RUTA = "/padron/personas/";
    private static final List<String> APELLIDOS = List.of(
            "GOMEZ", "FERNANDEZ", "LOPEZ", "DIAZ", "MARTINEZ", "PEREZ", "GARCIA", "SANCHEZ", "ROMERO", "SOSA");
    private static final List<String> EMPRESAS = List.of(
            "DISTRIBUIDORA DEL SUR SA", "SOFTWARE PAMPA SRL", "ALIMENTOS RIO DE LA PLATA SA",
            "TRANSPORTES ANDINOS SA", "CONSULTORA DELTA SRL");

    private final HttpServer server;
    private final Duration latencia;
    private final AtomicInteger enCurso = new AtomicInteger();
    private final AtomicInteger maximoSimultaneos = new AtomicInteger();
    private final AtomicInteger atendidos = new AtomicInteger();

    public PadronApiDummy(int puerto, Duration latencia) throws IOException {
        this.latencia = latencia;
        this.server = HttpServer.create(new InetSocketAddress("localhost", puerto), 4096);
        this.server.createContext(RUTA, this::atender);
        // Del lado del servidor también usamos virtual threads, así la API
        // dummy nunca es el cuello de botella: el límite lo pone el cliente.
        this.server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }

    public static void main(String[] args) throws IOException {
        PadronApiDummy api = new PadronApiDummy(8089, Duration.ofMillis(250)).iniciar();
        log.info("API dummy del padron escuchando en {}", api.url());
        log.info("Ejemplo: {}{}20301234567", api.url(), RUTA);
    }

    public PadronApiDummy iniciar() {
        server.start();
        return this;
    }

    public String url() {
        return "http://localhost:" + server.getAddress().getPort();
    }

    /** Máximo de pedidos que la API estuvo atendiendo al mismo tiempo desde el último reinicio. */
    public int maximoSimultaneos() {
        return maximoSimultaneos.get();
    }

    public int atendidos() {
        return atendidos.get();
    }

    public void reiniciarEstadisticas() {
        maximoSimultaneos.set(0);
        atendidos.set(0);
    }

    @Override
    public void close() {
        server.stop(0);
    }

    private void atender(HttpExchange exchange) throws IOException {
        maximoSimultaneos.accumulateAndGet(enCurso.incrementAndGet(), Math::max);
        try {
            Thread.sleep(latencia); // el "servicio externo lento"
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Se descuenta ANTES de responder: apenas el cliente recibe la respuesta,
            // este pedido ya no cuenta como "en curso".
            enCurso.decrementAndGet();
            atendidos.incrementAndGet();
        }
        String cuit = exchange.getRequestURI().getPath().substring(RUTA.length());
        if (!cuit.matches("\\d{11}") || noExiste(cuit)) {
            responder(exchange, 404, "{\"error\":\"No existe una persona con id " + cuit + "\"}");
        } else {
            responder(exchange, 200, personaJson(cuit));
        }
    }

    private static boolean noExiste(String cuit) {
        return semilla(cuit) % 10 == 0;
    }

    private static String personaJson(String cuit) {
        int semilla = semilla(cuit);
        boolean esEmpresa = cuit.startsWith("30");
        String denominacion = esEmpresa
                ? EMPRESAS.get(semilla % EMPRESAS.size())
                : APELLIDOS.get(semilla % APELLIDOS.size()) + " " + APELLIDOS.get((semilla / 7) % APELLIDOS.size());
        String estado = semilla % 10 == 1 ? "INACTIVO" : "ACTIVO";
        String categoria = List.of("RI", "MONO", "EX").get((semilla / 3) % 3);
        return """
                {"idPersona":%s,"tipoPersona":"%s","denominacion":"%s","estadoClave":"%s","categoriaIva":"%s"}"""
                .formatted(cuit, esEmpresa ? "JURIDICA" : "FISICA", denominacion, estado, categoria);
    }

    private static int semilla(String cuit) {
        return Math.floorMod(cuit.hashCode(), 1000);
    }

    private static void responder(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }
}
