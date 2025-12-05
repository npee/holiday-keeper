package io.npee.holidaykeeper.domain.service;

import io.npee.holidaykeeper.domain.model.holiday.Holiday;
import io.npee.holidaykeeper.domain.repository.HolidayRepository;
import io.npee.holidaykeeper.web.controller.dto.HolidayResponse;
import io.npee.holidaykeeper.web.controller.dto.HolidaySearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HolidayQueryService {

    private final HolidayRepository holidayRepository;

    @Transactional(readOnly = true)
    public Page<HolidayResponse> search(HolidaySearchCondition condition, Pageable pageable) {
        Page<Holiday> result = holidayRepository.search(condition, pageable);
        return result.map(HolidayResponse::from);
    }

}
