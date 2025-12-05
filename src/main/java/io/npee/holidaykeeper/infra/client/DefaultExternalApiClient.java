package io.npee.holidaykeeper.infra.client;

import io.npee.holidaykeeper.domain.model.region.ExternalCountry;
import io.npee.holidaykeeper.domain.model.holiday.ExternalHoliday;
import io.npee.holidaykeeper.domain.port.ExternalApiClient;
import io.npee.holidaykeeper.infra.client.dto.ExternalCountryResponse;
import io.npee.holidaykeeper.infra.client.dto.ExternalHolidayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;


@Component
@Slf4j
public class DefaultExternalApiClient implements ExternalApiClient {

    private final RestClient restClient;

    public DefaultExternalApiClient(@Qualifier("nagerRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<ExternalCountry> fetchAllCountries() {
        return callExternalCountries();
    }

    @Override
    public List<ExternalHoliday> fetchHolidays(int year, String countryCode) {
        return callExternalHolidays(year, countryCode);
    }

    private List<ExternalCountry> callExternalCountries() {
        List<ExternalCountryResponse> response = this.restClient.get()
                .uri("/AvailableCountries")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if (response == null) {
            return List.of();
        }

        return response.stream().map(ExternalCountryResponse::toDomain).toList();
    }

    private List<ExternalHoliday> callExternalHolidays(int year, String countryCode) {
        List<ExternalHolidayResponse> response = this.restClient.get()
                .uri("/PublicHolidays/{year}/{countryCode}", year, countryCode)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if (response == null) {
            return List.of();
        }

        return response.stream().map(ExternalHolidayResponse::toDomain).toList();
    }

}
