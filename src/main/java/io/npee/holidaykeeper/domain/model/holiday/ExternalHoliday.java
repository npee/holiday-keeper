package io.npee.holidaykeeper.domain.model.holiday;

import io.npee.holidaykeeper.domain.model.region.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
@Slf4j
public class ExternalHoliday {
    private String countryCode;
    private String date;
    private String localName;
    private String name;
    private List<HolidayType> types;
    private List<String> counties;
    private boolean fixed;
    private boolean global;
    private int launchYear;

    public Holiday toEntity(Map<String, Region> regionByIso) {
        Holiday holiday = Holiday.builder()
                .countryCode(countryCode)
                .date(LocalDate.parse(date))
                .localName(localName)
                .name(name)
                .fixed(fixed)
                .global(global)
                .launchYear(launchYear)
                .build();

        if (types != null && !types.isEmpty()) {
            holiday.getTypes().addAll(types);
        }

        if (counties == null || counties.isEmpty()) {
            Region region = regionByIso.get(countryCode);
            if (region != null) {
                HolidayRegion hr = HolidayRegion.builder()
                        .holiday(holiday)
                        .region(region)
                        .build();
                holiday.getHolidayRegions().add(hr);
            }
        } else {
            for (String iso : counties) {
                Region region = regionByIso.get(iso);
                if (region != null) {
                    HolidayRegion hr = HolidayRegion.builder()
                            .holiday(holiday)
                            .region(region)
                            .build();
                    holiday.getHolidayRegions().add(hr);
                } else {
                    log.warn("isoCode {}에 해당하는 Region이 존재하지 않습니다.", iso);
                }
            }
        }

        return holiday;

    }
}
