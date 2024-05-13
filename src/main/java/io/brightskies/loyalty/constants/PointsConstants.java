package io.brightskies.loyalty.constants;

import org.springframework.beans.factory.annotation.Value;

public class PointsConstants {
    @Value("${monthsUntilPointsExpire:2}")
    public static int MONTHS_UNTIL_EXPIRY;
}
