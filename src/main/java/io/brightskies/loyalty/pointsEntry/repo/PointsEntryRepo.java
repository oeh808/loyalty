package io.brightskies.loyalty.pointsEntry.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import io.brightskies.loyalty.pointsEntry.entity.PointsEntry;

public interface PointsEntryRepo extends JpaRepository<PointsEntry, Long> {

}
