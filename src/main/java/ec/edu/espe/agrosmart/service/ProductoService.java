package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.domain.ProductoFilters;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.mapper.ProductoMapper;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ProductoService {

    private static final Producto PRODUCTO_GENERICO = new Producto(
            0L,
            "Producto no disponible",
            "Quinua",
            new BigDecimal("1.00"),
            List.of("ventas@agrosmart.ec")
    );

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Flux<Producto> obtenerProductosComercializables() {
        // fromCallable retrasa la consulta JPA hasta que exista una suscripcion real.
        return Mono.fromCallable(productoRepository::findAll)
                // boundedElastic mueve Hibernate fuera del event loop de Netty.
                .subscribeOn(Schedulers.boundedElastic())
                // flatMapMany convierte la lista bloqueante ya obtenida en flujo reactivo.
                .flatMapMany(Flux::fromIterable)
                // map separa la entidad mutable de Hibernate del modelo inmutable de dominio.
                .map(ProductoMapper::toDominio)
                // map aplica la transformacion funcional sin mutar el producto recibido.
                .map(ProductoFilters.A_MAYUSCULAS)
                // filter deja pasar solo productos comercializables segun la regla central.
                .filter(ProductoFilters.IS_VALID)
                // doOnNext registra trazabilidad sin cambiar el elemento del flujo.
                .doOnNext(ProductoFilters.LOG_PRODUCTO)
                // defaultIfEmpty evita una respuesta vacia si todos los productos son invalidos.
                .defaultIfEmpty(PRODUCTO_GENERICO);
    }

    public Mono<Producto> buscarPorId(Long id) {
        // fromCallable envuelve findById porque JpaRepository usa JDBC bloqueante.
        return Mono.fromCallable(() -> productoRepository.findById(id))
                // boundedElastic evita que la consulta bloqueante ocupe el hilo HTTP reactivo.
                .subscribeOn(Schedulers.boundedElastic())
                // flatMap convierte Optional.empty en Mono.empty sin usar block ni if externo.
                .flatMap(Mono::justOrEmpty)
                // map transforma la entidad persistente al modelo inmutable que expone la API.
                .map(ProductoMapper::toDominio)
                // switchIfEmpty representa el no encontrado como error dentro del flujo.
                .switchIfEmpty(Mono.error(new ProductoNoEncontradoException(id)));
    }
}
