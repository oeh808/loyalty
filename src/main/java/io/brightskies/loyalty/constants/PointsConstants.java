package io.brightskies.loyalty.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PointsConstants {
    public final int MONTHS_UNTIL_EXPIRY;

    public final float WORTH_OF_ONE_POINT;

    public PointsConstants(@Value("${monthsUntilPointsExpire}") int expiry,
            @Value("${moneyEquivalenceToOnePoint}") float worth) {
        this.MONTHS_UNTIL_EXPIRY = expiry;
        this.WORTH_OF_ONE_POINT = worth;
    }
}
