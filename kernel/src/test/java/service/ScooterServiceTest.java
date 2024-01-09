package service;

import dto.ScooterCreationDTO;
import dto.ScooterDTO;
import entity.Scooter;
import entity.ScooterStatus;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import jakarta.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import producer.ScooterMessageProducer;
import repository.ScooterRepository;

import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("UnitTest")
public class ScooterServiceTest {

    private ScooterService scooterService;
    private ScooterRepository scooterRepository;


    @BeforeEach
    void setUp() {
        scooterRepository = mock(ScooterRepository.class);
        scooterService = new ScooterService(scooterRepository);
    }

    @Test
    @DisplayName("It should be able to register a scooter")
    void create() {
        var request = new ScooterCreationDTO("12345ABCDE", "Bird", "One");

        var scooter = new Scooter.Builder()
                .withSerialNumber(request.serialNumber())
                .withBrand(request.brand())
                .withModel(request.model())
                .withStatus(ScooterStatus.OUT_OF_SERVICE.name())
                .withBatteryLevel(BigDecimal.ZERO)
                .withLocation("0", "0")
                .build();

        when(scooterRepository.persist(any(Scooter.class)))
                .thenReturn(Uni.createFrom().item(scooter));

        var subscriber = scooterService.create(request)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();

        var scooterArgumentCaptor = ArgumentCaptor.forClass(Scooter.class);
        verify(scooterRepository, times(1)).persist(scooterArgumentCaptor.capture());
        assertEquals(scooterArgumentCaptor.getValue().getSerialNumber(), request.serialNumber());
        assertEquals(scooterArgumentCaptor.getValue().getBrand(), request.brand());
        assertEquals(scooterArgumentCaptor.getValue().getModel(), request.model());
    }

}
