package io.npee.holidaykeeper.infra.client;

import io.npee.holidaykeeper.domain.model.country.ExternalCountry;
import io.npee.holidaykeeper.domain.port.ExternalApiClient;
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
    void testFetchCountry_http_call() {
        List<ExternalCountry> externalCountries = client.fetchAllCountries();
        assertFalse(externalCountries.isEmpty());
    }

    @Test
    void testFetchHolidays_http_call() {
        List<ExternalCountry> externalCountries = client.fetchAllCountries();
        assertFalse(externalCountries.isEmpty());

        ExternalCountry country = externalCountries.getFirst();
        List.of(2022, 2023, 2024).forEach(year -> {
            var holidays = client.fetchHolidays(year, country.getCountryCode());
            assertFalse(holidays.isEmpty());
        });
    }

}