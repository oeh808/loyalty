package io.brightskies.loyalty.pointsEntry.service;

import java.util.List;

import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;

public interface PointsEntryService {
    PointsEntry createPoints(PointsEntry pointsEntry);

    PointsEntry getPointsEntry(long id);

    List<PointsEntry> getAllPointsEntries();

    PointsEntry updatePointsInEntry(long id, int points);

    void deletePointsEntry(long id);
}
