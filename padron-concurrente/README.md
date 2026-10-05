# Consultas concurrentes al padrón: de un pool de threads a virtual threads

Antes de emitir una tanda de facturas hay que validar los CUITs de los receptores contra el
**padrón de contribuyentes**, que es una API HTTP externa y **lenta** (cada consulta tarda ~250 ms).
Consultarlos de a uno no escala: 1.000 CUITs tardan más de 4 minutos.

Hoy esas consultas ya se hacen **de manera concurrente** con un **pool fijo de threads**
(`ExecutorService` de 10 threads, escrito "a la Java 8"). El objetivo del ejercicio es
**migrarlo a virtual threads** (Java 21) y modernizar el código.

## Estructura del código

```
src/main/java/ar/edu/utn/frba/padron/
├── Main.java                         (levanta la API dummy y arma las estrategias a comparar)
├── comparacion/
│   └── ComparadorDeEstrategias.java  (corre cada estrategia y loguea tiempo y consultas en paralelo)
├── dominio/                          (Contribuyente, CondicionIva: cómo ve el padrón NUESTRO sistema)
├── cliente/
│   ├── PadronClient.java             (la interfaz que necesita nuestro código)
│   ├── PadronHttpAdapter.java        (ADAPTER: la única clase que sabe hablar HTTP con la API)
│   ├── PersonaPadronResponse.java    (el JSON tal cual lo devuelve la API, con sus nombres)
│   └── PadronNoDisponibleException.java
├── validacion/
│   ├── ValidadorContribuyentes.java  (<-- acá están los TODO)
│   └── ResultadoValidacion.java
└── apidummy/PadronApiDummy.java      (API dummy local que simula al padrón lento)

src/test/java/.../validacion/
├── ValidadorContribuyentesTest.java  (tests de cada TODO)
└── PadronFalso.java                  (padrón en memoria: reemplaza al adapter en los tests)
```

### El adapter ya está resuelto

`ValidadorContribuyentes` no sabe que del otro lado hay HTTP, JSON, códigos de estado ni
reintentos: solo conoce la interfaz `PadronClient`. Toda esa traducción vive en
`PadronHttpAdapter`. Por eso:

- en los tests se lo reemplaza por `PadronFalso` sin levantar ninguna API;
- si la API cambia de formato o de proveedor, se reescribe el adapter y el validador no se toca;
- para hacer concurrente la validación **no hace falta tocar el adapter**: es thread-safe
  (comparte un único `HttpClient`).

## Requisitos

- Java 21 o superior
- Maven 3.9+, o el Maven Wrapper incluido (`./mvnw` en Linux/Mac, `mvnw.cmd` en Windows), o abrir
  el proyecto en IntelliJ

## Cómo correrlo

```bash
mvn test                                # corre los tests (fallan los de los TODO pendientes)
mvn compile exec:java                   # compara las estrategias con 50 CUITs
mvn compile exec:java -Dexec.args=1000  # ... y con 1000 CUITs
```

No usar `-q`: `exec:java` corre dentro de la JVM de Maven y el log sale por el logger de Maven,
que en modo silencioso no muestra nada.

`Main` levanta la API dummy en un puerto libre, así que no hace falta levantarla aparte. Si se
quiere probar a mano, `PadronApiDummy` tiene su propio `main` y queda escuchando en
`http://localhost:8089/padron/personas/20301234567`.

Salida esperada cuando todo está resuelto (los tiempos varían según la máquina):

```
16:42:29.938 [INFO] API dummy en http://localhost:56427 - 250 ms por consulta - 50 CUITs
16:42:43.570 [INFO] Secuencial                          13,60 s - 1 consultas en paralelo - 38 HABILITADO | 5 INACTIVO | 7 NO_ENCONTRADO
16:42:44.907 [INFO] Pool de 10 threads (actual)          1,34 s - 10 consultas en paralelo - 38 HABILITADO | 5 INACTIVO | 7 NO_ENCONTRADO
16:42:45.221 [INFO] TODO 1 - Virtual threads             0,31 s - 50 consultas en paralelo - 38 HABILITADO | 5 INACTIVO | 7 NO_ENCONTRADO
16:42:45.527 [INFO] BONUS - Virtual threads (max. 50)    0,30 s - 50 consultas en paralelo - 38 HABILITADO | 5 INACTIVO | 7 NO_ENCONTRADO
```

Mientras un TODO no está resuelto, su línea sale como `pendiente`.

`consultas en paralelo` es el máximo de consultas que la API estuvo atendiendo al mismo tiempo.
La salida se escribe con SLF4J (`slf4j-simple`, configurado en `src/main/resources/simplelogger.properties`).

## Consigna

Todo se resuelve en `validacion/ValidadorContribuyentes.java`.

`validarConPoolDeThreads` ya está implementado: es el punto de partida. Leerlo primero.

1. **TODO 1: migrar a virtual threads.** Implementar `validarConVirtualThreads`: la misma
   validación, con un virtual thread por consulta.
2. **TODO 2: modernizar.** Comparar los dos métodos y dejarlos como se escribirían hoy en Java 21,
   sin código duplicado. ¿Sigue haciendo falta el `try/finally` con `shutdown()`? Opcional:
   `for` → streams. Los tests tienen que seguir pasando.
3. **Analizar.** Correr `Main` con 50 y con 1000 CUITs y responder:
   - ¿Por qué con el pool de 10 threads la API nunca atiende más de 10 consultas a la vez?
   - Con 1000 CUITs, ¿cuánto tarda el pool? ¿Y los virtual threads? ¿De dónde sale cada número?
   - ¿Qué pasaría si subimos el pool a 1000 threads de plataforma? ¿Y a 100.000?
   - ¿Qué hacen los threads del pool mientras esperan la respuesta de la API?
   - ¿Cuánto código cambió entre el pool fijo y los virtual threads?
4. **BONUS: virtual threads con límite.** Implementar `validarConVirtualThreadsLimitado`
   sin superar `maxSimultaneos` consultas a la vez. Tiene que seguir usando virtual threads:
   volver a un pool de threads de plataforma no vale.

Los tests de `ValidadorContribuyentesTest` van pasando a medida que se resuelve cada TODO.
Como referencia, usar el machete de virtual threads de la clase.
