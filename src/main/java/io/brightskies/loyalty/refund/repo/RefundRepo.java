package io.brightskies.loyalty.refund.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import io.brightskies.loyalty.refund.entity.Refund;

public interface RefundRepo extends JpaRepository<Refund, Long> {

}
