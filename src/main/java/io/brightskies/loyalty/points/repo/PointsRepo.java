package io.brightskies.loyalty.points.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import io.brightskies.loyalty.points.entity.Points;

public interface PointsRepo extends JpaRepository<Points, Long> {

}
