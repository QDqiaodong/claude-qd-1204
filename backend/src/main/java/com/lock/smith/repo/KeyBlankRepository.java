package com.lock.smith.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lock.smith.entity.KeyBlank;

@Repository
public interface KeyBlankRepository extends JpaRepository<KeyBlank, Long> {

    List<KeyBlank> findByType(String type);
}
