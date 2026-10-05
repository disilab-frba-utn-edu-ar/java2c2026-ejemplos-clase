package ar.edu.utn.frba.padron.cliente;

import ar.edu.utn.frba.padron.dominio.CondicionIva;
import ar.edu.utn.frba.padron.dominio.Contribuyente;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Adapter: la ÚNICA clase que sabe hablar con la API HTTP del padrón.
 *
 * Traduce nuestra interfaz ({@link PadronClient}) al protocolo de la API:
 * arma la URL, hace el GET, reintenta si no puede conectarse, interpreta los
 * códigos HTTP y mapea el JSON del proveedor ({@link PersonaPadronResponse})
 * a nuestro {@link Contribuyente}.
 *
 * Es thread-safe: un único HttpClient se comparte entre todos los threads
 * (es lo recomendado; crear uno por pedido es caro).
 *
 * No hace falta modificar esta clase para el ejercicio.
 */
public class PadronHttpAdapter implements PadronClient {

    private static final Logger log = LoggerFactory.getLogger(PadronHttpAdapter.class);

    private static final String RUTA_PERSONAS = "/padron/personas/";
    private static final Duration TIMEOUT_CONEXION = Duration.ofSeconds(5);
    private static final Duration TIMEOUT_RESPUESTA = Duration.ofSeconds(30);
    private static final int MAX_INTENTOS = 5;
    private static final Duration ESPERA_ENTRE_INTENTOS = Duration.ofMillis(100);

    private final HttpClient http;
    private final String baseUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    public PadronHttpAdapter(String baseUrl) {
        this(HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(TIMEOUT_CONEXION)
                .build(), baseUrl);
    }

    public PadronHttpAdapter(HttpClient http, String baseUrl) {
        this.http = http;
        this.baseUrl = baseUrl;
    }

    @Override
    public Optional<Contribuyente> consultar(String cuit) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + RUTA_PERSONAS + cuit))
                .timeout(TIMEOUT_RESPUESTA)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = enviar(request, cuit);

        return switch (response.statusCode()) {
            case 200 -> Optional.of(aContribuyente(leer(response.body(), cuit)));
            case 404 -> Optional.empty();
            default -> throw new PadronNoDisponibleException(
                    "El padrón respondió " + response.statusCode() + " para el CUIT " + cuit);
        };
    }

    private HttpResponse<String> enviar(HttpRequest request, String cuit) {
        try {
            for (int intento = 1; ; intento++) {
                try {
                    // send() es bloqueante: el thread espera acá hasta que llega la respuesta.
                    return http.send(request, HttpResponse.BodyHandlers.ofString());
                } catch (ConnectException e) {
                    // Si llegan muchas conexiones juntas el servidor puede rechazar algunas.
                    // Reintentar es un detalle de "cómo hablar con la API": por eso vive acá.
                    if (intento == MAX_INTENTOS) {
                        throw e;
                    }
                    log.debug("No se pudo conectar para el CUIT {} (intento {}/{}): reintento", cuit, intento, MAX_INTENTOS);
                    Thread.sleep(ESPERA_ENTRE_INTENTOS.multipliedBy(intento));
                }
            }
        } catch (IOException e) {
            throw new PadronNoDisponibleException("No se pudo consultar el CUIT " + cuit, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PadronNoDisponibleException("Consulta interrumpida para el CUIT " + cuit, e);
        }
    }

    private PersonaPadronResponse leer(String json, String cuit) {
        try {
            return mapper.readValue(json, PersonaPadronResponse.class);
        } catch (JsonProcessingException e) {
            throw new PadronNoDisponibleException("Respuesta inválida del padrón para el CUIT " + cuit, e);
        }
    }

    private Contribuyente aContribuyente(PersonaPadronResponse persona) {
        return new Contribuyente(
                String.valueOf(persona.idPersona()),
                persona.denominacion(),
                aCondicionIva(persona.categoriaIva()),
                "ACTIVO".equals(persona.estadoClave()));
    }

    private CondicionIva aCondicionIva(String categoria) {
        return switch (categoria) {
            case "RI" -> CondicionIva.RESPONSABLE_INSCRIPTO;
            case "MONO" -> CondicionIva.MONOTRIBUTO;
            case "EX" -> CondicionIva.EXENTO;
            default -> throw new PadronNoDisponibleException("Categoría de IVA desconocida: " + categoria);
        };
    }
}
