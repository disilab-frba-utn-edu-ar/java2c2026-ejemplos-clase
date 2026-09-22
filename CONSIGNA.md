# Ejercicio integrador — Migración a Java moderno

Partís de una aplicación **Spring Boot (Java 21)** de facturación que
funciona pero está escrita "a la antigua". Tu tarea es **migrarla** a las
features modernas de Java que vimos en clase, sin romper el comportamiento
(los endpoints tienen que seguir respondiendo igual).

Seguí los comentarios `// TODO PASO N: ...` que están sembrados en el código.

## Cómo correr la app

```bash
mvn spring-boot:run
```

Endpoints (puerto 8080):

- `GET /facturas` — listado de facturas (resumen: número, tipo, neto, fecha)
- `GET /facturas/tipo/{tipo}` — listado por tipo (`A`, `B` o `C`)
- `GET /facturas/{numero}` — detalle de una factura (ej. `A-0001`), **incluye
  su IVA**; 404 si no existe
- `GET /facturas/buscar?tipo=A&desde=2026-01-01&hasta=2026-03-31&montoMinimo=8000`
  — facturas de un tipo, en un rango de fechas y con neto mayor a un mínimo

> Si el puerto 8080 está ocupado:
> `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`

## Reglas de IVA

| Tipo | Emisor / Receptor | IVA |
|------|-------------------|-----|
| A | Responsable Inscripto (discrimina IVA) | 21% del neto |
| B | Consumidor Final (IVA incluido) | 21% del neto |
| C | Monotributista / Exento | 0 |

## Pasos de la migración (en orden)

1. **Records** — Convertí `FacturaA`, `FacturaB` y `FacturaC` en `record`.
   (Recordá: los accessors de un record van sin `get`: `numero()`, no
   `getNumero()`.)

2. **Pattern matching** — Sacá el cálculo del IVA de adentro de las clases y
   resolvelo por fuera con un `switch` con *pattern matching* sobre el tipo de
   factura. Empezá con un `default` en el switch.

3. **Sealed** — Convertí la interface `Factura` en `sealed` (con `permits`).
   Ahora que la jerarquía es cerrada, sacá el `default` del switch: debería
   seguir compilando. Después **agregá una `FacturaE`** (exportación, exenta
   de IVA) al `permits`... y observá qué pasa con la compilación. Arreglá lo
   que se rompa.

4. **Streams** — Reemplazá los bucles `for` del repositorio y el servicio por
   operaciones de `Stream` (`filter`, `map`, `toList`, ...). Prestá especial
   atención al método `buscar(...)` del servicio: hoy es un `for` con varios
   `if` anidados (tipo + rango de fechas + monto mínimo). Tiene que quedar
   como una **cadena de `.filter(...)`**, una por cada condición, cerrada con
   `.map(this::aResumen).toList()`.

5. **Optional** — Hacé que `buscarPorNumero` deje de devolver `null` y
   devuelva `Optional<Factura>`. Ajustá el servicio y el controlador para
   trabajar con el `Optional` (sin `if (x == null)`).

## Qué tenés que poder responder al terminar

- ¿Por qué, al agregar `FacturaE`, el compilador te avisó de los lugares a
  cambiar? ¿Qué habría pasado sin `sealed` + `switch` exhaustivo?
- ¿Qué ventaja te da `Optional` frente a devolver `null`?
- ¿Cuándo conviene pattern matching y cuándo polimorfismo clásico?
