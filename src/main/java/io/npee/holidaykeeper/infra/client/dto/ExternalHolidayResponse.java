package io.npee.holidaykeeper.infra.client.dto;

import io.npee.holidaykeeper.domain.model.holiday.ExternalHoliday;
import io.npee.holidaykeeper.domain.model.holiday.HolidayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ExternalHolidayResponse {
    private String countryCode;
    private String date;
    private String localName;
    private String name;
    private List<HolidayType> types;
    private List<String> countries;
    private boolean fixed;
    private boolean global;
    private int launchYear;

    public ExternalHoliday toDomain() {
        return ExternalHoliday.builder()
                .countryCode(this.countryCode)
                .date(this.date)
                .localName(this.localName)
                .name(this.name)
                .types(this.types)
                .countries(this.countries)
                .fixed(this.fixed)
                .global(this.global)
                .launchYear(this.launchYear)
                .build();
    }
}
