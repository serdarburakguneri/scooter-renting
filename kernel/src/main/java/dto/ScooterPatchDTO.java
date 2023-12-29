package dto;

import java.math.BigDecimal;

public record ScooterPatchDTO(
        BigDecimal batteryLevel,
        LocationDTO location,
        String status) {

}
