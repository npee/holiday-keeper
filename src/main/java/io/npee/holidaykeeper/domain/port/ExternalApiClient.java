package io.npee.holidaykeeper.domain.port;


import io.npee.holidaykeeper.domain.model.region.ExternalCountry;
import io.npee.holidaykeeper.domain.model.holiday.ExternalHoliday;

import java.util.List;

public interface ExternalApiClient {

    List<ExternalCountry> fetchAllCountries();

    List<ExternalHoliday> fetchHolidays(int year, String countryCode);
}
