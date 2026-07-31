package ec.edu.espe.agrosmart.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.entity.ProductoEntity;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ProductoServiceTest {

    @Test
    void obtenerProductosComercializables_conTresValidosYDosInvalidos_debeEmitirSoloLosTresValidos() {
        // Arrange
        ProductoRepository repository = mock(ProductoRepository.class);
        when(repository.findAll()).thenReturn(productosConTresValidosYDosInvalidos());
        ProductoService service = new ProductoService(repository);

        // Act
        Flux<Producto> flujo = service.obtenerProductosComercializables();

        // Assert
        StepVerifier.create(flujo)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void obtenerProductosComercializables_conTodosInvalidos_debeEmitirProductoGenerico() {
        // Arrange
        ProductoRepository repository = mock(ProductoRepository.class);
        when(repository.findAll()).thenReturn(List.of(
                producto(10L, "Quinua sin precio", BigDecimal.ZERO, 20, "Quinua", "ventas@agrosmart.ec"),
                producto(11L, "Quinua sin correos", new BigDecimal("3.40"), 15, "Quinua", "")
        ));
        ProductoService service = new ProductoService(repository);

        // Act
        Flux<Producto> flujo = service.obtenerProductosComercializables();

        // Assert
        StepVerifier.create(flujo)
                .expectNextMatches(producto -> "Producto no disponible".equals(producto.getNombre()))
                .verifyComplete();
    }

    @Test
    void buscarPorId_conIdInexistente_debeTerminarConProductoNoEncontradoException() {
        // Arrange
        ProductoRepository repository = mock(ProductoRepository.class);
        when(repository.findById(9999L)).thenReturn(Optional.empty());
        ProductoService service = new ProductoService(repository);

        // Act
        Mono<Producto> resultado = service.buscarPorId(9999L);

        // Assert
        StepVerifier.create(resultado)
                .expectError(ProductoNoEncontradoException.class)
                .verify();
    }

    private List<ProductoEntity> productosConTresValidosYDosInvalidos() {
        return List.of(
                producto(
                        1L,
                        "Quinua organica de altura",
                        new BigDecimal("4.80"),
                        120,
                        "Quinua",
                        "ventas@agrosmart.ec,exportaciones@agrosmart.ec"
                ),
                producto(
                        2L,
                        "Quinua roja andina",
                        new BigDecimal("5.35"),
                        90,
                        "Quinua",
                        "andina@agrosmart.ec"
                ),
                producto(
                        3L,
                        "Harina de quinua premium",
                        new BigDecimal("6.20"),
                        75,
                        "Quinua",
                        "premium@agrosmart.ec"
                ),
                producto(
                        4L,
                        "Quinua perlada seleccionada",
                        BigDecimal.ZERO,
                        60,
                        "Quinua",
                        "seleccion@agrosmart.ec"
                ),
                producto(
                        5L,
                        "Quinua negra gourmet",
                        new BigDecimal("7.10"),
                        45,
                        "Quinua",
                        ""
                )
        );
    }

    private ProductoEntity producto(Long id, String nombre, BigDecimal precio, Integer stock,
                                    String categoria, String correos) {
        ProductoEntity entity = new ProductoEntity(nombre, precio, stock, categoria, correos);
        ReflectionTestUtils.setField(entity, "idProducto", id);
        return entity;
    }
}
