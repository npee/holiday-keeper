package io.npee.holidaykeeper.domain.repository;

import io.npee.holidaykeeper.domain.model.holiday.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayQueryRepository {

    List<Holiday> findByCountryCodeAndDateBetween(String countryCode, LocalDate from, LocalDate to);
}
