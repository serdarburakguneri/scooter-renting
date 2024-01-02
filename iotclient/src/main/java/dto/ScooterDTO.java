package dto;

import java.math.BigDecimal;

public record ScooterDTO(
        String serialNumber,
        BigDecimal batteryLevel,
        LocationDTO location,
        String status) {

}
