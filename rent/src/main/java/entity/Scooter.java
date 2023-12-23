package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "scooter")
public class Scooter {

    public Scooter() {
        if (Objects.isNull(this.id)) {
            this.id = UUID.randomUUID();
        }
    }

    @Id
    @Column(name = "id")
    private UUID id;

    @NotNull
    @NotBlank
    @Column(name = "serial_number")
    private String serialNumber;

    @NotNull
    @NotBlank
    @Column(name = "brand")
    private String brand;

    @NotNull
    @NotBlank
    @Column(name = "model")
    private String model;

    @NotNull
    @NotBlank
    @Column(name = "status")
    private String status;

    @Column(name = "battery_level")
    private int batteryLevel;

    @NotNull
    @NotBlank
    @Column(name = "location")
    private String location;

    public UUID getId() {
        return id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getStatus() {
        return status;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public String getLocation() {
        return location;
    }

    public static class Builder {

        private final Scooter scooter;

        public Builder() {
            this.scooter = new Scooter();
        }

        public Builder withSerialNumber(String serialNumber) {
            this.scooter.serialNumber = serialNumber;
            return this;
        }

        public Builder withBrand(String brand) {
            this.scooter.brand = brand;
            return this;
        }

        public Builder withModel(String model) {
            this.scooter.model = model;
            return this;
        }

        public Builder withStatus(String status) {
            this.scooter.status = status;
            return this;
        }

        public Builder withBatteryLevel(int batteryLevel) {
            this.scooter.batteryLevel = batteryLevel;
            return this;
        }

        public Builder withLocation(String location) {
            this.scooter.location = location;
            return this;
        }

        public Scooter build() {
            return this.scooter;
        }
    }
}
