package adapter;

import dto.LocationDTO;
import dto.ScooterDTO;
import dto.ScooterPatchDTO;

public class ScooterPatchDTOAdapter {

    public static ScooterPatchDTO fromScooterDTO(ScooterDTO source) {
        return new ScooterPatchDTO(source.batteryLevel(),
                new LocationDTO(source.location().latitude(), source.location().longitude()),
                source.status());
    }

}
