package io.npee.holidaykeeper.domain.service;

import io.npee.holidaykeeper.domain.model.region.ExternalCountry;
import io.npee.holidaykeeper.domain.model.holiday.ExternalHoliday;
import io.npee.holidaykeeper.domain.port.ExternalApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayExternalFetchService {

    private final ExternalApiClient client;

    public List<ExternalHoliday> fetchAllHolidaysForRecentFiveYears() {
        List<ExternalCountry> countries = client.fetchAllCountries();
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
                                            client.fetchHolidays(year, country.getCountryCode());

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
