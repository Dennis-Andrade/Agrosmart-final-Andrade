package ec.edu.espe.agrosmart.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Producto {

    private final Long id;
    private final String nombre;
    private final String categoria;
    private final BigDecimal precioUsd;
    private final List<String> correosNotificacion;

    public Producto(Long id, String nombre, String categoria, BigDecimal precioUsd,
                    List<String> correosNotificacion) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "nombre no puede ser null");
        this.categoria = Objects.requireNonNull(categoria, "categoria no puede ser null");
        this.precioUsd = Objects.requireNonNull(precioUsd, "precioUsd no puede ser null");
        this.correosNotificacion = new ArrayList<>(
                Objects.requireNonNull(correosNotificacion, "correosNotificacion no puede ser null")
        );
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public BigDecimal getPrecioUsd() {
        return precioUsd;
    }

    public List<String> getCorreosNotificacion() {
        return Collections.unmodifiableList(new ArrayList<>(correosNotificacion));
    }
}
