package io.npee.holidaykeeper.infra.client;

import io.npee.holidaykeeper.domain.model.region.ExternalCountry;
import io.npee.holidaykeeper.domain.port.ExternalApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DefaultExternalApiClientTest {

    @Autowired
    private ExternalApiClient client;

    @Test
    @DisplayName("사용가능한 국가들을 외부 API 통해 조회한다.")
    void testFetchCountry_http_call() {
        List<ExternalCountry> externalCountries = client.fetchAllCountries();
        assertFalse(externalCountries.isEmpty());
    }

    @Test
    @DisplayName("특정 국가의 특정 연도의 휴일들을 외부 API 통해 조회한다.")
    void testFetchHolidays_http_call() {
        List<ExternalCountry> externalCountries = client.fetchAllCountries();
        assertFalse(externalCountries.isEmpty());

        ExternalCountry country = externalCountries.getFirst();
        List.of(2022, 2023, 2024).forEach(year -> {
            var holidays = client.fetchHolidays(year, country.getCountryCode());
            assertFalse(holidays.isEmpty());
        });
    }

    @Test
    @DisplayName("최근 5년간의 모든 국가의 휴일들을 외부 API 통해 조회한다.")
    void testFetchAllHolidaysForRecentFiveYears_http_call() {
        var holidays = client.fetchAllHolidaysForRecentFiveYears();
        assertFalse(holidays.isEmpty());
    }

}