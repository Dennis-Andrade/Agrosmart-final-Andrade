package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.entity.ProductoEntity;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProductoSeeder implements CommandLineRunner {

    private final ProductoRepository productoRepository;

    public ProductoSeeder(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        if (productoRepository.count() > 0) {
            return;
        }

        productoRepository.saveAll(List.of(
                new ProductoEntity(
                        "Quinua organica de altura",
                        new BigDecimal("4.80"),
                        120,
                        "Quinua",
                        "ventas@agrosmart.ec,exportaciones@agrosmart.ec"
                ),
                new ProductoEntity(
                        "Quinua roja andina",
                        new BigDecimal("5.35"),
                        90,
                        "Quinua",
                        "andina@agrosmart.ec"
                ),
                new ProductoEntity(
                        "Harina de quinua premium",
                        new BigDecimal("6.20"),
                        75,
                        "Quinua",
                        "premium@agrosmart.ec"
                ),
                new ProductoEntity(
                        "Quinua perlada seleccionada",
                        BigDecimal.ZERO,
                        60,
                        "Quinua",
                        "seleccion@agrosmart.ec"
                ),
                new ProductoEntity(
                        "Quinua negra gourmet",
                        new BigDecimal("7.10"),
                        45,
                        "Quinua",
                        ""
                )
        ));
    }
}
