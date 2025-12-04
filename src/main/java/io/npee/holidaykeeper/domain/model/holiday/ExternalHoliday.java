package io.npee.holidaykeeper.domain.model.holiday;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ExternalHoliday {
    private String countryCode;
    private String date;
    private String localName;
    private String name;
    private List<HolidayType> types;
    private List<String> countries;
    private boolean fixed;
    private boolean global;
    private int launchYear;
}
