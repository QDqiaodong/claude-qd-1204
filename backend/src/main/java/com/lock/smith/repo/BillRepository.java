package com.lock.smith.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lock.smith.entity.Bill;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByCustomerId(Long customerId);

    List<Bill> findByCustomerIdAndPayStatus(Long customerId, String payStatus);

    boolean existsByCustomerIdAndPayStatus(Long customerId, String payStatus);
}
