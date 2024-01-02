package service;

import entity.Location;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.Stack;

/*
 For now, I am simulating. Maybe I work with real devices one day :)
 */
@ApplicationScoped
public class SensorReaderService {

    private double initialBatterLevel = 100;
    private final Stack<Location> simulationTrip = simulationTrip();

    public Uni<BigDecimal> readBatteryLevel() {
        initialBatterLevel -= 0.1;
        return Uni.createFrom().item(BigDecimal.valueOf(initialBatterLevel));
    }

    public Uni<Location> readLocation() {
        return Uni.createFrom()
                .item(simulationTrip.isEmpty() ?
                        new Location("59.335444", "18.063669")
                        : simulationTrip.pop());
    }

    private Stack<Location> simulationTrip() {
        var locations = new Stack<Location>();
        locations.push(new Location("59.335444", "18.063669")); //Hötorget
        locations.push(new Location("59.335728", "18.065472")); //Somewhere in kungsgatan
        locations.push(new Location("59.335925", "18.067703")); //Somewhere in kungsgatan
        locations.push(new Location("59.336057", "18.070836")); //Somewhere in kungsgatan
        locations.push(new Location("59.335575", "18.073046")); //Somewhere in jarlsgatan
        locations.push(new Location("59.334437", "18.074141")); //Somewhere in jarlsgatan
        locations.push(new Location("59.333036", "18.075900")); //Somewhere in jarlsgatan
        locations.push(new Location("59.332204", "18.078282")); //Somewhere in strandvägen
        locations.push(new Location("59.331520", "18.080621")); //Somewhere in strandvägen
        locations.push(new Location("59.331422", "18.083850")); //Somewhere in strandvägen
        locations.push(new Location("59.331493", "18.087305")); //Somewhere in strandvägen
        return locations;
    }

}
