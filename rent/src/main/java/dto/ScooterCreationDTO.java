package dto;

import jakarta.validation.constraints.NotBlank;

public record ScooterCreationDTO(
        @NotBlank(message = "Serial number of a scooter can not be empty.")
        String serialNumber,
        @NotBlank(message = "Brand of a scooter can not be empty.")
        String brand,
        @NotBlank(message = "Model of a scooter can not be empty.")
        String model) {

}
