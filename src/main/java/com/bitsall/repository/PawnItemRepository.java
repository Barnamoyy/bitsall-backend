package com.bitsall.repository;

import com.bitsall.model.entity.PawnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PawnItemRepository extends JpaRepository<PawnItem, Long> {
    List<PawnItem> findBySold(boolean sold);
}
