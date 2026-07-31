package ec.edu.espe.agrosmart.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ProductoTest {

    @Test
    void getters_conValoresDelConstructor_debenDevolverLosMismosValores() {
        // Arrange
        List<String> correos = List.of("ventas@agrosmart.ec");

        // Act
        Producto producto = new Producto(1L, "Quinua organica", "Quinua", new BigDecimal("4.80"), correos);

        // Assert
        assertEquals(1L, producto.getId());
        assertEquals("Quinua organica", producto.getNombre());
        assertEquals("Quinua", producto.getCategoria());
        assertEquals(new BigDecimal("4.80"), producto.getPrecioUsd());
        assertEquals(correos, producto.getCorreosNotificacion());
    }

    @Test
    void getCorreosNotificacion_alMutarListaOriginal_noDebeAfectarProducto() {
        // Arrange
        List<String> correos = new ArrayList<>();
        correos.add("ventas@agrosmart.ec");
        Producto producto = new Producto(1L, "Quinua organica", "Quinua", new BigDecimal("4.80"), correos);

        // Act
        correos.add("externo@agrosmart.ec");

        // Assert
        assertEquals(1, producto.getCorreosNotificacion().size());
        assertNotSame(correos, producto.getCorreosNotificacion());
    }

    @Test
    void getCorreosNotificacion_alModificarListaDevuelta_debeLanzarExcepcion() {
        // Arrange
        List<String> correos = new ArrayList<>();
        correos.add("ventas@agrosmart.ec");
        Producto producto = new Producto(1L, "Quinua organica", "Quinua", new BigDecimal("4.80"), correos);

        // Act
        Executable accion = () -> producto.getCorreosNotificacion().add("externo@agrosmart.ec");

        // Assert
        assertThrows(
                UnsupportedOperationException.class,
                accion
        );
    }
}
