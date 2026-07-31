package ec.edu.espe.agrosmart.domain;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

public final class ProductoFilters {

    private static final Logger LOGGER = Logger.getLogger(ProductoFilters.class.getName());

    public static final Predicate<Producto> IS_VALID = producto ->
            producto.getPrecioUsd().compareTo(BigDecimal.ZERO) > 0
                    && !producto.getCorreosNotificacion().isEmpty();

    public static final Consumer<Producto> LOG_PRODUCTO = producto ->
            LOGGER.info(() -> "Producto procesado: " + producto.getId() + " - " + producto.getNombre());

    public static final Function<Producto, Producto> A_MAYUSCULAS = producto -> new Producto(
            producto.getId(),
            producto.getNombre().toUpperCase(Locale.ROOT),
            producto.getCategoria(),
            producto.getPrecioUsd(),
            producto.getCorreosNotificacion()
    );

    private ProductoFilters() {
    }
}
