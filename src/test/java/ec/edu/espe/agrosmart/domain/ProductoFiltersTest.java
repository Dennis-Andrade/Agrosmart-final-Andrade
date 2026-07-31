package ec.edu.espe.agrosmart.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProductoFiltersTest {

    @Test
    void isValid_conPrecioMayorACeroYCorreos_debeRetornarTrue() {
        // Arrange
        Producto producto = new Producto(
                1L,
                "Quinua organica",
                "Quinua",
                new BigDecimal("4.80"),
                List.of("ventas@agrosmart.ec")
        );

        // Act
        boolean resultado = ProductoFilters.IS_VALID.test(producto);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void isValid_conPrecioCero_debeRetornarFalse() {
        // Arrange
        Producto producto = new Producto(
                2L,
                "Quinua perlada",
                "Quinua",
                BigDecimal.ZERO,
                List.of("ventas@agrosmart.ec")
        );

        // Act
        boolean resultado = ProductoFilters.IS_VALID.test(producto);

        // Assert
        assertFalse(resultado);
    }

    @Test
    void isValid_conCorreosVacios_debeRetornarFalse() {
        // Arrange
        Producto producto = new Producto(
                3L,
                "Quinua negra",
                "Quinua",
                new BigDecimal("7.10"),
                List.of()
        );

        // Act
        boolean resultado = ProductoFilters.IS_VALID.test(producto);

        // Assert
        assertFalse(resultado);
    }
}
