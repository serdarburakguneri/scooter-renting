package service;

import dto.ScooterCreationDTO;
import entity.Scooter;
import entity.ScooterStatus;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import repository.ScooterRepository;

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
    void create() {
        var request = new ScooterCreationDTO("12345ABCDE", "Bird", "One");

        when(scooterRepository.persist(any(Scooter.class)))
                .thenReturn(Uni.createFrom().item(new Scooter.Builder()
                        .withSerialNumber(request.serialNumber())
                        .withBrand(request.brand())
                        .withModel(request.model())
                        .withStatus(ScooterStatus.OUT_OF_SERVICE)
                        .withBatteryLevel(BigDecimal.ZERO)
                        .withLocation("0", "0")
                        .build()));

        var subscriber = scooterService.create(request)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();

        var captor = ArgumentCaptor.forClass(Scooter.class);
        verify(scooterRepository, times(1)).persist(captor.capture());
        assertEquals(captor.getValue().getSerialNumber(), request.serialNumber());
        assertEquals(captor.getValue().getBrand(), request.brand());
        assertEquals(captor.getValue().getModel(), request.model());
    }
}
