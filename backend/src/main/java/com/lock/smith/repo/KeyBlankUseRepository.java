package com.lock.smith.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lock.smith.entity.KeyBlankUse;

@Repository
public interface KeyBlankUseRepository extends JpaRepository<KeyBlankUse, Long> {

    List<KeyBlankUse> findByKeyBlankId(Long keyBlankId);
}
