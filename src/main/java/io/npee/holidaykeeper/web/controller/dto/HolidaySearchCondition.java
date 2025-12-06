package io.npee.holidaykeeper.web.controller.dto;

import io.npee.holidaykeeper.domain.model.holiday.HolidayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class HolidaySearchCondition {

    private Integer from;

    private Integer to;

    private String countryCode;

    private String regionIso;

    private HolidayType type;
}
