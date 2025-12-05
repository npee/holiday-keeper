package io.npee.holidaykeeper.domain.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HolidayRegionRepository {

    private final HolidayRegionJpaRepository holidayRegionJpaRepository;

    public void deleteAllInBatch() {
        holidayRegionJpaRepository.deleteAllInBatch();
    }
}
