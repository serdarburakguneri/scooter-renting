package service;

import dto.LocationDTO;
import dto.ScooterDTO;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import producer.ScooterMessageProducer;

@ApplicationScoped
public class ScooterService {

    @ConfigProperty(name = "device.serial-number")
    String serialNumber;
    private final ScooterMessageProducer scooterMessageProducer;
    private final SensorReaderService sensorReaderService;
    private final ScooterStatusService scooterStatusService;

    @Inject
    public ScooterService(ScooterMessageProducer scooterMessageProducer,
            SensorReaderService sensorReaderService,
            ScooterStatusService scooterStatusService) {
        this.scooterMessageProducer = scooterMessageProducer;
        this.sensorReaderService = sensorReaderService;
        this.scooterStatusService = scooterStatusService;
    }

    @Scheduled(every = "10s")
    public Uni<Void> updateMe() {
        var batteryLevelResult = sensorReaderService.readBatteryLevel();
        var locationResult = sensorReaderService.readLocation();
        var statusResult = scooterStatusService.getStatus();

        return Uni.combine()
                .all()
                .unis(batteryLevelResult, locationResult, statusResult)
                .asTuple()
                .onItem()
                .transform(tuple -> new ScooterDTO(
                        serialNumber,
                        tuple.getItem1(),
                        new LocationDTO(tuple.getItem2().latitude(), tuple.getItem2().longitude()),
                        tuple.getItem3().name()))
                .onItem()
                .call(scooterMessageProducer::scooterUpdated)
                .replaceWithVoid();
    }
}
