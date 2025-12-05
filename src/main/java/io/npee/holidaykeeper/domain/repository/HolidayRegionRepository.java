package io.npee.holidaykeeper.domain.repository;

import io.npee.holidaykeeper.domain.model.holiday.HolidayRegion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HolidayRegionRepository {

    private final HolidayRegionJpaRepository holidayRegionJpaRepository;

    public void deleteAllInBatch() {
        holidayRegionJpaRepository.deleteAllInBatch();
    }

    public List<HolidayRegion> findAll() {
        return holidayRegionJpaRepository.findAll();
    }

    public long count() {
        return holidayRegionJpaRepository.count();
    }
}
