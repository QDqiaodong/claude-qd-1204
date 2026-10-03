package com.lock.smith.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lock.smith.entity.LockLedger;

@Repository
public interface LockLedgerRepository extends JpaRepository<LockLedger, Long> {

    List<LockLedger> findByKindAndStatus(String kind, String status);

    List<LockLedger> findByStatus(String status);
}
