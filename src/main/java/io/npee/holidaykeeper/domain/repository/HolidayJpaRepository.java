package io.npee.holidaykeeper.domain.repository;

import io.npee.holidaykeeper.domain.model.holiday.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayJpaRepository extends JpaRepository<Holiday, Long> {
}
