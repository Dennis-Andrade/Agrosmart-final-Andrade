package ec.edu.espe.agrosmart.mapper;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.entity.ProductoEntity;
import java.util.Arrays;
import java.util.List;

public final class ProductoMapper {

    private ProductoMapper() {
    }

    public static Producto toDominio(ProductoEntity entity) {
        return new Producto(
                entity.getIdProducto(),
                entity.getNombreProducto(),
                entity.getCategoria(),
                entity.getPrecioUsd(),
                toCorreos(entity.getCorreosNotificacion())
        );
    }

    private static List<String> toCorreos(String correos) {
        if (correos == null || correos.isBlank()) {
            return List.of();
        }

        return Arrays.stream(correos.split(","))
                .map(String::trim)
                .filter(correo -> !correo.isEmpty())
                .toList();
    }
}
