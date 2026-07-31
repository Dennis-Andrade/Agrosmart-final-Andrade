package ec.edu.espe.agrosmart.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PublicidadServiceTest {

    @Test
    void generarPublicidad_cuandoProveedorResponde_debeEmitirTextoGenerado() {
        // Arrange
        AgroSmartAIService aiService = mock(AgroSmartAIService.class);
        when(aiService.generarPublicidad("Quinua", "tiendas saludables"))
                .thenReturn("Quinua premium para tiendas saludables.");
        PublicidadService service = new PublicidadService(aiService);

        // Act
        Mono<String> resultado = service.generarPublicidad("Quinua", "tiendas saludables");

        // Assert
        StepVerifier.create(resultado)
                .expectNext("Quinua premium para tiendas saludables.")
                .verifyComplete();
    }

    @Test
    void generarPublicidad_cuandoProveedorFalla_debeEmitirMensajeDeRespaldo() {
        // Arrange
        AgroSmartAIService aiService = mock(AgroSmartAIService.class);
        when(aiService.generarPublicidad(any(), any()))
                .thenThrow(new RuntimeException("429 Too Many Requests"));
        PublicidadService service = new PublicidadService(aiService);

        // Act
        Mono<String> resultado = service.generarPublicidad("Quinua", "tiendas saludables");

        // Assert
        StepVerifier.create(resultado)
                .expectNextMatches(texto -> texto.contains("Publicidad no disponible"))
                .verifyComplete();
    }
}
