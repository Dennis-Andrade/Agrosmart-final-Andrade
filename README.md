# AgroSmart Final Andrade

Backend reactivo para la plataforma AgroSmart, orientado a comercializacion agricola.
El proyecto integra WebFlux, JPA/Hibernate, PostgreSQL, Project Reactor, LangChain4j y
pruebas unitarias con JUnit 5, Mockito y StepVerifier.

## Semilla Personal

| Parametro | Valor |
|-----------|-------|
| Estudiante | Dennis Alexander Andrade Guaman |
| Cedula | 1751025238 |
| NN | 38 |
| Tabla | `tbl_productos_base_38` |
| Puerto prod | `8138` |
| Categoria | Quinua |
| Audiencia IA | tiendas de alimentacion saludable |
| Base de datos | `agrosmart_db` |

La regla central del proyecto es que un producto es comercializable si `precioUsd > 0`
y `correosNotificacion` no esta vacia.

## Stack

- Java 21
- Spring Boot 3.5.7
- Spring WebFlux sobre Netty
- Spring Data JPA con Hibernate
- PostgreSQL
- Docker Compose
- LangChain4j
- JUnit 5, Mockito y Reactor Test

## Ejecucion

Requisitos:

- Java 21
- Maven
- Docker disponible

Comandos:

```bash
docker compose up -d
mvn spring-boot:run
```

La aplicacion usa el perfil `prod` y arranca en:

```text
http://localhost:8138
```

Para ejecutar pruebas:

```bash
mvn test
```

## Configuracion

El perfil `prod` se activa en:

```properties
spring.profiles.active=prod
```

El puerto se configura en `src/main/resources/application-prod.properties`:

```properties
server.port=8138
spring.jpa.hibernate.ddl-auto=update
```

LangChain4j se configura por properties. No se declara un `@Bean` manual para el modelo.

## Persistencia

La entidad JPA es `ProductoEntity` y se mapea a:

```java
@Table(name = "tbl_productos_base_38")
```

La tabla contiene:

- `id_producto`
- `nombre_producto`
- `precio_usd`
- `stock_kg`
- `categoria`
- `correos_notificacion`

La siembra inicial se hace con `ProductoSeeder`. Es idempotente porque primero revisa
`productoRepository.count()`. Si ya existen datos, no inserta de nuevo.

Datos sembrados:

- 3 productos validos de Quinua.
- 1 producto invalido por precio `0`.
- 1 producto invalido por correos vacios.

## Modelo De Dominio

`ProductoEntity` y `Producto` estan separados.

`ProductoEntity` es mutable porque Hibernate necesita constructor vacio, getters y
setters para materializar datos.

`Producto` es el modelo de dominio inmutable:

- clase `final`
- atributos `private final`
- sin setters
- copia defensiva en constructor
- copia defensiva en getter
- getter de correos con lista no modificable

## Servicio Reactivo

`ProductoService` aisla las llamadas bloqueantes de JPA:

```java
Mono.fromCallable(productoRepository::findAll)
        .subscribeOn(Schedulers.boundedElastic())
```

La razon es que `JpaRepository` usa JDBC y bloquea el hilo. En WebFlux no se debe
bloquear el event loop de Netty, por eso las llamadas a JPA se ejecutan en
`boundedElastic`.

Operadores usados:

- `Mono.fromCallable`: retrasa la llamada bloqueante hasta la suscripcion.
- `subscribeOn(Schedulers.boundedElastic())`: mueve el bloqueo fuera del event loop.
- `flatMapMany`: convierte la lista de JPA en `Flux`.
- `map`: transforma entidad a dominio y aplica mayusculas.
- `filter`: deja pasar solo productos comercializables.
- `doOnNext`: registra trazabilidad sin transformar.
- `defaultIfEmpty`: devuelve producto generico si el flujo queda vacio.
- `switchIfEmpty`: convierte un id inexistente en error reactivo.

## IA

`AgroSmartAIService` usa LangChain4j con `@AiService`, `@UserMessage` y `@V`.

`PublicidadService` envuelve la llamada al modelo:

```java
Mono.fromCallable(() -> agroSmartAIService.generarPublicidad(producto, audiencia))
        .subscribeOn(Schedulers.boundedElastic())
        .timeout(Duration.ofSeconds(30))
        .onErrorResume(...)
```

La llamada a IA tambien se considera bloqueante porque espera una respuesta HTTP externa.
Si el proveedor falla, se devuelve un mensaje de respaldo.

## API

| Metodo | Ruta | Retorno | Descripcion |
|--------|------|---------|-------------|
| GET | `/api/productos` | `Flux<Producto>` | Lista productos comercializables |
| GET | `/api/productos/{id}` | `Mono<Producto>` | Busca producto por id |
| GET | `/api/agrosmart/publicidad` | `Mono<String>` | Genera publicidad en texto plano |

Ejemplos:

```bash
curl http://localhost:8138/api/productos
curl http://localhost:8138/api/productos/1
curl -i http://localhost:8138/api/productos/9999
curl "http://localhost:8138/api/agrosmart/publicidad?producto=Quinua%20organica&audiencia=tiendas%20de%20alimentacion%20saludable"
```

El error de producto inexistente se maneja con `ApiExceptionHandler` y responde `404`.

## Pruebas

Las pruebas no dependen de PostgreSQL ni de internet.

Clases de prueba:

- `ProductoTest`
- `ProductoFiltersTest`
- `ProductoServiceTest`
- `PublicidadServiceTest`

Cobertura principal:

- getters y copias defensivas del modelo inmutable
- regla de producto valido e invalidos
- flujo reactivo con 3 validos y 2 invalidos
- producto generico con `defaultIfEmpty`
- error reactivo con `switchIfEmpty`
- respuesta exitosa y fallback de IA

Ejecucion verificada:

```text
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
```

## Evidencias

Las evidencias finales se guardan en `docs/evidencias/`:

- arranque con perfil `prod` y puerto `8138`
- estructura y datos de `tbl_productos_base_38`
- salidas reales de `curl`
- salida real de `mvn test`
- salida real de `git log --oneline --graph --all`

## Git

El trabajo se desarrollo por ramas y commits semanticos:

- `feature/config-perfiles`
- `feature/persistencia-jpa`
- `feature/modelo-inmutable`
- `feature/servicio-reactivo`
- `feature/ia-langchain4j`
- `feature/api-reactiva`
- `feature/pruebas`
- `feature/documentacion`

La integracion final debe mantener el historial visible, sin squash y sin `push --force`.
