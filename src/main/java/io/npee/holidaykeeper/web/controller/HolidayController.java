package io.npee.holidaykeeper.web.controller;

import io.npee.holidaykeeper.domain.model.holiday.HolidayType;
import io.npee.holidaykeeper.domain.service.HolidayQueryService;
import io.npee.holidaykeeper.web.controller.dto.HolidayResponse;
import io.npee.holidaykeeper.web.controller.dto.HolidaySearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/holidays")
public class HolidayController {

    private final HolidayQueryService holidayQueryService;

    @GetMapping
    public Page<HolidayResponse> searchHolidays(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false) String regionIso,
            @RequestParam(required = false) HolidayType type,
            @PageableDefault Pageable pageable
    ) {
        HolidaySearchCondition condition = HolidaySearchCondition.builder()
                .year(year)
                .countryCode(countryCode)
                .regionIso(regionIso)
                .type(type)
                .build();

        return holidayQueryService.search(condition, pageable);
    }
}