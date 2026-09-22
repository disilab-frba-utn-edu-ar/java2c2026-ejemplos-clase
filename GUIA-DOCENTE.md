# Guía docente — Ejercicio integrador (Clases 1 y 2)

> Material **para el/la docente**. Contiene el paso a paso de la migración
> y cómo debe quedar cada cambio, para poder guiar a los alumnos con los
> `TODO` que ya están sembrados en el código.
>
> La **solución completa y funcionando** está en la branch `solucion`.

## De qué se trata

Los alumnos parten de un proyecto **Spring Boot legacy** (esta branch,
`ejercicio-facturas-legacy`) y lo migran a features modernas de Java 21:

| Paso | Feature | Qué se migra |
|------|---------|--------------|
| — | *(punto de partida)* | Interface `Factura` + clases `FacturaA/B/C` con polimorfismo |
| 1 | **Records** | `FacturaA/B/C` pasan a ser `record` |
| 2 | **Pattern matching** | El cálculo del IVA sale de las clases a un `switch` |
| 3 | **Sealed** | Se sella `Factura`; agregar `FacturaE` **rompe** los `switch` |
| 4 | **Streams** | El repositorio y el controlador dejan el `for` por streams |
| 5 | **Optional** | El repositorio deja de devolver `null` |

El orden importa: primero records (para poder hacer pattern matching con
deconstrucción), después sealed (para que el `switch` sea *exhaustivo* y el
compilador obligue a contemplar la factura nueva), y al final streams y
Optional que son independientes entre sí.

## Cómo correr el proyecto

```bash
mvn spring-boot:run
```

Endpoints (puerto 8080):

- `GET /facturas` — listado (resumen: número, tipo, neto)
- `GET /facturas/tipo/{tipo}` — listado por tipo (`A`, `B` o `C`)
- `GET /facturas/{numero}` — detalle de una factura (ej. `A-0001`), incluye
  su IVA; 404 si no existe

> Si el 8080 está ocupado (por ejemplo por otra app abierta desde el IDE):
> `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`

El IVA se calcula **por factura** y se muestra en el detalle. Valores
esperados con los datos semilla: `A-0001` (neto 10000) → IVA **2100.0**;
`C-0001` → IVA **0.0**; `E-0001` (exportación) → IVA **0.0**.

## Reglas de IVA (contexto AFIP, simplificado)

| Tipo | Emisor / Receptor | IVA |
|------|-------------------|-----|
| A | Responsable Inscripto (discrimina IVA) | 21% del neto |
| B | Consumidor Final (IVA incluido) | 21% del neto |
| C | Monotributista / Exento | 0 |
| E | Exportación *(se agrega en el paso 3)* | 0 (exenta) |

---

## Paso 1 — Records

**Objetivo:** que `FacturaA`, `FacturaB` y `FacturaC` sean `record`.

Un `record` es inmutable y genera solo el constructor, los *accessors*,
`equals`, `hashCode` y `toString`. Ojo: los accessors **no** llevan `get`
(`numero()` en vez de `getNumero()`).

Como la interface `Factura` hoy declara `getNumero()`, `getNeto()`,
`getTipo()` y `calcularIva()`, hay dos caminos didácticos:

- **Recomendado:** aprovechar el paso para *limpiar* la interface. Los
  accessors del record (`numero()`, `neto()`) cubren los datos, y el
  `getTipo()`/`calcularIva()` se resuelven aparte (ver Paso 2). La interface
  queda como un simple marcador de "esto es una Factura".
- Alternativa conservadora: mantener los `getX()` implementándolos a mano
  dentro del record. Sirve para mostrar que un record *también* puede tener
  métodos, pero ensucia el ejemplo.

Cómo queda cada record (con la interface ya limpia del Paso 2):

```java
public record FacturaA(String numero, double neto, String cuitCliente)
        implements Factura { }

public record FacturaB(String numero, double neto, String nombreCliente)
        implements Factura { }

public record FacturaC(String numero, double neto)
        implements Factura { }
```

**Se rompe en:** todos los usos de `getNumero()`/`getNeto()` pasan a
`numero()`/`neto()` (repositorio y controlador). Buen momento para mostrar el
refactor del IDE.

---

## Paso 2 — Pattern matching (cálculo del IVA)

**Objetivo:** sacar `calcularIva()` de las clases y resolverlo con un
`switch` con *pattern matching* sobre el tipo de factura.

Se crea una clase utilitaria (o método estático):

```java
public final class CalculadoraIva {

    private CalculadoraIva() { }

    public static double calcular(Factura factura) {
        return switch (factura) {
            case FacturaA a -> a.neto() * 0.21;
            case FacturaB b -> b.neto() * 0.21;
            case FacturaC c -> 0.0;
            default -> throw new IllegalStateException(
                    "Tipo de factura no contemplado: " + factura);
        };
    }
}
```

Puntos para remarcar:

- El `case FacturaA a ->` es *type pattern*: matchea el tipo **y** ya te da
  la variable tipada `a`. No hace falta castear.
- Por ahora **hace falta el `default`**: como la interface todavía **no** es
  sealed, el compilador no puede saber que A/B/C son las únicas. Ese `default`
  es justamente lo que vamos a poder borrar en el Paso 3.
- Se puede agrupar: `case FacturaA a, FacturaB b -> ...` (mismo 21%). Queda a
  criterio si mostrarlo separado (más claro) o agrupado (más DRY).

Se reemplaza la llamada a `factura.calcularIva()` por
`CalculadoraIva.calcular(factura)` en el controlador (en el detalle de la
factura), y se elimina `calcularIva()` de la interface y de los records.

Segundo `switch` (opcional, refuerza el paso siguiente): una descripción por
tipo, que también quedará incompleta al agregar `FacturaE`.

```java
public static String descripcion(Factura f) {
    return switch (f) {
        case FacturaA a -> "Factura A a Responsable Inscripto";
        case FacturaB b -> "Factura B a Consumidor Final";
        case FacturaC c -> "Factura C de Monotributista";
        default -> "Factura";
    };
}
```

---

## Paso 3 — Sealed (¡acá se rompe!)

**Objetivo:** sellar la jerarquía y agregar `FacturaE`.

1. Convertir la interface en `sealed`:

```java
public sealed interface Factura permits FacturaA, FacturaB, FacturaC { }
```

2. **Borrar los `default`** de los `switch` del Paso 2. Ahora que la
   jerarquía es cerrada, el compilador sabe que A/B/C son *todas* las
   opciones y el `switch` es exhaustivo sin `default`. (Mostrar que
   *compila igual* después de sacar el `default`.)

3. Agregar la factura nueva:

```java
public record FacturaE(String numero, double neto, String paisDestino)
        implements Factura { }
```

...y agregarla al `permits`:

```java
public sealed interface Factura
        permits FacturaA, FacturaB, FacturaC, FacturaE { }
```

**Acá se rompe la compilación**, y ese es el corazón del ejercicio. Al haber
una nueva alternativa, cada `switch` exhaustivo deja de compilar con un error
tipo *"the switch statement does not cover all possible input values"*.

**Lugares que se rompen (2, gracias a haber sacado los `default`):**

1. `CalculadoraIva.calcular(...)` — falta el `case FacturaE`.
2. `CalculadoraIva.descripcion(...)` — falta el `case FacturaE`.

El compilador **te lleva de la mano** a cada lugar que hay que actualizar.
Se agregan los casos:

```java
case FacturaE e -> 0.0;                       // en calcular(): exportación exenta
case FacturaE e -> "Factura E de Exportación"; // en descripcion()
```

> **Moraleja para la clase:** sin `sealed` + `switch` exhaustivo, agregar
> `FacturaE` habría pasado silenciosamente por el `default` (IVA calculado
> mal, o excepción en runtime). Con sealed, el error es en *compilación*: es
> imposible olvidarse un caso.

Recordá sumar una `FacturaE` a los datos semilla del repositorio para verla
en los endpoints.

---

## Paso 4 — De `for` a Streams

**Objetivo:** reemplazar los bucles del repositorio y del controlador.

`FacturaRepositorio`:

```java
public List<Factura> buscarTodas() {
    return facturas.stream().toList();
}

public List<Factura> buscarPorTipo(String tipo) {
    return facturas.stream()
            .filter(f -> f.getTipo().equals(tipo))   // o CalculadoraIva.tipo(f)
            .toList();
}
```

Los `for` del controlador que arman las listas de resúmenes también pasan a
Streams:

```java
return servicio.listarTodas().stream()
        .map(this::aResumen)
        .toList();
```

> Nota: si en el Paso 1 se sacó `getTipo()` de la interface, el filtro por
> tipo se resuelve con pattern matching (`instanceof FacturaA`) o con un
> método `CalculadoraIva.tipo(f)`. Decidir según cómo haya quedado la interface.

---

## Paso 5 — De `null` a `Optional`

**Objetivo:** que `buscarPorNumero` no devuelva `null`.

`FacturaRepositorio`:

```java
public Optional<Factura> buscarPorNumero(String numero) {
    return facturas.stream()
            .filter(f -> f.getNumero().equals(numero))
            .findFirst();
}
```

`FacturaServicio` propaga el `Optional`:

```java
public Optional<Factura> buscarPorNumero(String numero) {
    return repositorio.buscarPorNumero(numero);
}
```

`FacturaControlador` decide qué responder, sin `if (x == null)`:

```java
@GetMapping("/{numero}")
public ResponseEntity<Map<String, Object>> buscarPorNumero(@PathVariable String numero) {
    return servicio.buscarPorNumero(numero)
            .map(f -> ResponseEntity.ok(aMapa(f)))
            .orElse(ResponseEntity.notFound().build());
}
```

**Remarcar:** `Optional` documenta en la *firma* que el resultado puede no
estar, y `map`/`orElse` hacen el manejo explícito e imposible de olvidar.

---

## Checklist final

- [ ] `FacturaA/B/C` (y `E`) son `record`.
- [ ] `Factura` es `sealed ... permits ...`.
- [ ] El IVA se calcula con `switch` + pattern matching, **sin `default`**.
- [ ] Agregar/quitar un tipo del `permits` rompe la compilación en los `switch`.
- [ ] El repositorio y el controlador usan streams, sin `for`.
- [ ] `buscarPorNumero` devuelve `Optional`, sin `null`.
- [ ] El detalle `GET /facturas/{numero}` muestra el IVA de esa factura
      (`A-0001` → 2100.0; `C-0001` → 0.0; `E-0001` → 0.0), y los listados
      devuelven el resumen sin IVA.
