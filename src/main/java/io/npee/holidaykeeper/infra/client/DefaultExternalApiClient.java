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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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

    @Override
    public List<ExternalHoliday> fetchAllHolidaysForRecentFiveYears() {
        return callAllHolidaysForRecentFiveYears();
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

    private List<ExternalHoliday> callAllHolidaysForRecentFiveYears() {
        List<ExternalCountry> countries = fetchAllCountries();
        if (countries.isEmpty()) {
            return List.of();
        }

        List<Integer> recentFiveYears = List.of(
                LocalDate.now().getYear(),
                LocalDate.now().getYear() - 1,
                LocalDate.now().getYear() - 2,
                LocalDate.now().getYear() - 3,
                LocalDate.now().getYear() - 4
        );

        List<ExternalHoliday> holidays = Collections.synchronizedList(new ArrayList<>());

        try (ExecutorService executor = Executors.newFixedThreadPool(
                32, Thread.ofVirtual().factory())) {
            List<Callable<Void>> tasks = countries.stream()
                    .flatMap(country -> recentFiveYears.stream()
                            .map(year -> (Callable<Void>) () -> {
                                try {
                                    List<ExternalHoliday> externalHolidays =
                                            fetchHolidays(year, country.getCountryCode());

                                    log.info("Fetched {} holidays for country={}, year={}",
                                            externalHolidays.size(), country.getCountryCode(), year);

                                    holidays.addAll(externalHolidays);
                                } catch (Exception e) {
                                    log.error("Failed to fetch holidays for country={}, year={}",
                                            country.getCountryCode(), year, e);
                                }
                                return null;
                            }))
                    .toList();

            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to fetch holidays", e);
        }

        log.info("holidays.size() {}", holidays.size());

        holidays.forEach(holiday -> {
            if (Objects.equals(holiday.getCountryCode(), "US")) {
                log.info("Fetched Holiday: countryCode={}, date={}, localName={}, name={}, types={}, counties={}, fixed={}, global={}, launchYear={}",
                        holiday.getCountryCode(),
                        holiday.getDate(),
                        holiday.getLocalName(),
                        holiday.getName(),
                        holiday.getTypes(),
                        holiday.getCounties(),
                        holiday.isFixed(),
                        holiday.isGlobal(),
                        holiday.getLaunchYear());
            }
        });

        return holidays;
    }
}
