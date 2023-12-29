package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
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
    @Column(name = "serial_number", unique = true)
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
    private BigDecimal batteryLevel;

    @Embedded
    private Location location;

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

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(BigDecimal batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(String latitude, String longitude) {
        this.location.latitude = latitude;
        this.location.longitude = longitude;
    }

    public boolean isAvailable() {
        return this.status.equals(ScooterStatus.AVAILABLE.name());
    }

    public void requestUnLock() {
        this.status = ScooterStatus.REQUESTED_FOR_RENT.name();
    }

    @Embeddable
    public static class Location {

        @NotNull
        @NotBlank
        @Column(name = "location_latitude")
        private String latitude;

        @NotNull
        @NotBlank
        @Column(name = "location_longitude")
        private String longitude;

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
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

        public Builder withBatteryLevel(BigDecimal batteryLevel) {
            this.scooter.batteryLevel = batteryLevel;
            return this;
        }

        public Builder withLocation(String latitude, String longitude) {
            this.scooter.location = new Location();
            this.scooter.location.latitude = latitude;
            this.scooter.location.longitude = longitude;
            return this;
        }

        public Scooter build() {
            return this.scooter;
        }
    }
}
