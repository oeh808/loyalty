package io.brightskies.loyalty.pointsEntry.service;

import java.util.List;

import io.brightskies.loyalty.customer.entity.Customer;
import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;

public interface PointsEntryService {
    PointsEntry createPointsEntry(PointsEntry pointsEntry);

    PointsEntry getPointsEntry(long id);

    List<PointsEntry> getAllPointsEntries();

    List<PointsEntry> getNonExpiredPointsEntriesByCustomer(Customer customer);

    List<PointsEntry> getSoonToExpirePointsEntries(String phoneNumber);

    PointsEntry updatePointsInEntry(long id, int points);

    void deletePointsEntry(long id);
}
