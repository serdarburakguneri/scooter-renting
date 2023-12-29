package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "rental_history")
public class RentalHistory {

    public RentalHistory() {
        if (Objects.isNull(this.id)) {
            this.id = UUID.randomUUID();
        }
    }

    @Id
    @Column(name = "id")
    private UUID id;

    @NotNull
    @Column(name = "scooter_id")
    private UUID scooterId;

    @NotNull
    @Column(name = "user_id")
    private String userId;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @NotNull
    @NotBlank
    @Column(name = "status")
    private String status;


    public UUID getId() {
        return id;
    }

    public UUID getScooterId() {
        return scooterId;
    }

    public String getUserId() {
        return userId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public String getStatus() {
        return status;
    }

    public static class Builder {

        private final RentalHistory rentalHistory;

        public Builder() {
            this.rentalHistory = new RentalHistory();
        }

        public Builder withScooterId(UUID scooterId) {
            this.rentalHistory.scooterId = scooterId;
            return this;
        }

        public Builder withUserId(String userId) {
            this.rentalHistory.userId = userId;
            return this;
        }

        public Builder withStartDate(Date startDate) {
            this.rentalHistory.startDate = startDate;
            return this;
        }

        public Builder withEndDate(Date endDate) {
            this.rentalHistory.endDate = endDate;
            return this;
        }

        public Builder withTotalCost(BigDecimal totalCost) {
            this.rentalHistory.totalCost = totalCost;
            return this;
        }

        public Builder withStatus(String status) {
            this.rentalHistory.status = status;
            return this;
        }

        public RentalHistory build() {
            return this.rentalHistory;
        }
    }
}
