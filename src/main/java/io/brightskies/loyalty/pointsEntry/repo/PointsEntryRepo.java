package io.brightskies.loyalty.pointsEntry.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;

import java.sql.Date;
import java.util.List;
import io.brightskies.loyalty.customer.entity.Customer;

public interface PointsEntryRepo extends JpaRepository<PointsEntry, Long> {
    // List<PointsEntry> findByCustomerOrderByExpiryDateAsc(Customer customer);

    @Query("SELECT pe FROM PointsEntry pe WHERE pe.customer = ?1 " +
            " AND pe.expiryDate > ?2 " +
            " ORDER BY pe.expiryDate ASC")
    List<PointsEntry> findNonExpiredPointsEntriesByCustomer(Customer customer, Date date);

    @Query("SELECT pe FROM PointsEntry pe WHERE pe.customer = ?1 " +
            " AND pe.expiryDate between ?2 and ?3" +
            " ORDER BY pe.expiryDate ASC")
    List<PointsEntry> findByExpiryDateBetweenDatesByCustomer(Customer customer, Date date1, Date date2);

    List<PointsEntry> findByExpiryDateBefore(Date expiryDate);

}
