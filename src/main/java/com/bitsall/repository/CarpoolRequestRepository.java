package com.bitsall.repository;

import com.bitsall.model.entity.CarpoolRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarpoolRequestRepository extends JpaRepository<CarpoolRequest, Long> {
}
