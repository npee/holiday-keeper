package io.npee.holidaykeeper.domain.port;


import io.npee.holidaykeeper.domain.model.country.ExternalCountry;
import io.npee.holidaykeeper.domain.model.holiday.ExternalHoliday;

import java.util.List;

public interface ExternalApiClient {

    List<ExternalCountry> fetchAllCountries();

    List<ExternalHoliday> fetchHolidays(int year, String countryCode);

    List<ExternalHoliday> fetchHolidaysForRecentFiveYears(String countryCode);
}
