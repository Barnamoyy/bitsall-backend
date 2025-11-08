package com.bitsall.repository;

import com.bitsall.model.entity.PawnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PawnItemRepository extends JpaRepository<PawnItem, UUID> {
    List<PawnItem> findBySold(boolean sold);
}
