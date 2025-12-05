package io.npee.holidaykeeper.domain.repository;

import io.npee.holidaykeeper.domain.model.region.Region;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RegionRepository {

    private final RegionJpaRepository regionJpaRepository;

    public List<Region> findAll() {
        return regionJpaRepository.findAll();
    }

    public List<Region> saveAll(Iterable<Region> newRegions) {
        return regionJpaRepository.saveAll(newRegions);
    }
}
