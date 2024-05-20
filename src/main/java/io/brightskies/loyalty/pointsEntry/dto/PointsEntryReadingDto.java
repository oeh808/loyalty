package io.brightskies.loyalty.pointsEntry.dto;

import java.sql.Date;

public record PointsEntryReadingDto(boolean isExpired, int numberOfPoints, Date expiryDate) {

}
