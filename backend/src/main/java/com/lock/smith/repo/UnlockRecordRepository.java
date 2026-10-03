package com.lock.smith.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lock.smith.entity.UnlockRecord;

@Repository
public interface UnlockRecordRepository extends JpaRepository<UnlockRecord, Long> {
}
