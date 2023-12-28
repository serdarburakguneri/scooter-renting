package adapter;

import dto.ScooterCreationDTO;
import entity.Scooter;
import entity.ScooterStatus;
import java.math.BigDecimal;

public class ScooterAdapter {

    public static Scooter fromScooterCreationDTO(ScooterCreationDTO request) {
        return new Scooter.Builder()
                .withSerialNumber(request.serialNumber())
                .withBrand(request.brand())
                .withModel(request.model())
                .withStatus(ScooterStatus.OUT_OF_SERVICE.name())
                .withBatteryLevel(BigDecimal.ZERO)
                .withLocation("0", "0")
                .build();
    }
}
