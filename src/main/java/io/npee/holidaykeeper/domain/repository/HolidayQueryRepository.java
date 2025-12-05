package io.npee.holidaykeeper.domain.repository;

import io.npee.holidaykeeper.domain.model.holiday.Holiday;
import io.npee.holidaykeeper.web.controller.dto.HolidaySearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayQueryRepository {

    Page<Holiday> search(HolidaySearchCondition condition, Pageable pageable);
}
