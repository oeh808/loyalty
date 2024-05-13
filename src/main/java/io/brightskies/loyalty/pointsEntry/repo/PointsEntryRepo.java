package io.brightskies.loyalty.pointsEntry.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;
import java.util.List;
import io.brightskies.loyalty.customer.entity.Customer;


public interface PointsEntryRepo extends JpaRepository<PointsEntry, Long> {
    List<PointsEntry> findByCustomerOrderByExpiryDateAsc(Customer customer);
}
