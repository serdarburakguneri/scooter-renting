package adapter;

import dto.LocationDTO;
import dto.ScooterDTO;
import entity.Scooter;

public class ScooterDTOAdapter {

    public static ScooterDTO fromScooter(Scooter scooter) {
        return new ScooterDTO(scooter.getId(),
                scooter.getSerialNumber(),
                scooter.getBrand(),
                scooter.getModel(),
                scooter.getBatteryLevel(),
                new LocationDTO(scooter.getLocation().getLatitude(),
                        scooter.getLocation().getLongitude()),
                scooter.getStatus());
    }

}
