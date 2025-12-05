package io.npee.holidaykeeper.domain.repository;

import io.npee.holidaykeeper.domain.model.holiday.HolidayRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRegionRepository extends JpaRepository<HolidayRegion, Long> {
}
