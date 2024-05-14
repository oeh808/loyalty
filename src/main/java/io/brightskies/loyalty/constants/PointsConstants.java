package io.brightskies.loyalty.constants;

import org.springframework.beans.factory.annotation.Value;

public class PointsConstants {
    @Value("${monthsUntilPointsExpire:2}")
    public static int MONTHS_UNTIL_EXPIRY;

    @Value("${moneyEquivalenceToOnePoint:0.25f}")
    public static float WORTH_OF_ONE_POINT;
}