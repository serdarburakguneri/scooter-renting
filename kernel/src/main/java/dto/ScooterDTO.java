package dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ScooterDTO(UUID id,
                         String serialNumber,
                         String brand,
                         String model,
                         BigDecimal batteryLevel,
                         LocationDTO location,
                         String status) {

}
