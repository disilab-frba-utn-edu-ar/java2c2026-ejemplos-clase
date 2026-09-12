# Monolito tradicional

## Estructura del código

```
src/main/java/ar/edu/utn/frba/biblioteca/
├── BibliotecaApplication.java
├── DatosDeEjemplo.java        (carga usuarios/libros de ejemplo al arrancar)
├── controller/                (UsuarioController, LibroController, PrestamoController)
├── service/                   (UsuarioService, LibroService)
├── repository/                (UsuarioRepository, LibroRepository, PrestamoRepository - en memoria)
├── model/                     (Usuario, Libro, Prestamo)
├── dto/                       (requests y responses)
├── exception/                 (excepciones de negocio + manejador global)
└── ejemplos/daovsrepository/  (LibroDao vs LibroRepository, no forma parte del flujo de la app)
```

## Cómo correr la app

```bash
mvn spring-boot:run
```

La app levanta en `http://localhost:8080` y ya viene con 2 usuarios y 3 libros de ejemplo
(uno de ellos, "Domain-Driven Design", a propósito con stock 0).

## Endpoints

| Método | Endpoint                   | Descripción                          |
|--------|-----------------------------|---------------------------------------|
| POST   | `/usuarios`                 | Crea un usuario                       |
| GET    | `/usuarios`                 | Lista usuarios                        |
| GET    | `/usuarios/{id}`            | Busca un usuario por id               |
| POST   | `/libros`                   | Crea un libro                         |
| GET    | `/libros`                   | Lista libros                          |
| GET    | `/libros/{id}`              | Busca un libro por id                 |
| POST   | `/prestamos`                | Crea un préstamo (valida stock)       |
| POST   | `/prestamos/{id}/devolver`  | Devuelve un préstamo (repone stock)   |
| GET    | `/prestamos`                | Lista préstamos                       |
