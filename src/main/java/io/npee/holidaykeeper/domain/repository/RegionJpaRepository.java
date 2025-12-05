package io.npee.holidaykeeper.domain.repository;

import io.npee.holidaykeeper.domain.model.region.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionJpaRepository extends JpaRepository<Region, Long> {
}
