package service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

@ApplicationScoped
public class BatteryLevelService {

    private double initialBatterLevel = 100;


    public Uni<BigDecimal> readBatteryLevel() {
        initialBatterLevel -= 0.1;
        return Uni.createFrom().item(BigDecimal.valueOf(initialBatterLevel));
    }

}
