package io.npee.holidaykeeper.web.controller.dto;

import io.npee.holidaykeeper.domain.model.holiday.Holiday;
import io.npee.holidaykeeper.domain.model.holiday.HolidayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class HolidayResponse {

    private Long id;
    private String countryCode;
    private LocalDate date;
    private String localName;
    private String name;
    private boolean fixed;
    private boolean global;
    private Integer launchYear;
    private List<HolidayType> types;
    private List<String> regionIsoCodes;

    public static HolidayResponse from(Holiday holiday) {
        return HolidayResponse.builder()
                .id(holiday.getId())
                .countryCode(holiday.getCountryCode())
                .date(holiday.getDate())
                .localName(holiday.getLocalName())
                .name(holiday.getName())
                .fixed(holiday.isFixed())
                .global(holiday.isGlobal())
                .launchYear(holiday.getLaunchYear())
                .types(holiday.getTypes().stream().toList())
                .regionIsoCodes(holiday.getHolidayRegions().stream()
                        .map(hr -> hr.getRegion().getIsoCode())
                        .distinct()
                        .toList())
                .build();
    }

}
