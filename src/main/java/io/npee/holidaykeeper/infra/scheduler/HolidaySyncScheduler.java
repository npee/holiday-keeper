package io.npee.holidaykeeper.infra.scheduler;

import io.npee.holidaykeeper.domain.model.region.ExternalCountry;
import io.npee.holidaykeeper.domain.port.ExternalApiClient;
import io.npee.holidaykeeper.domain.service.HolidayMutationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class HolidaySyncScheduler {
    
    private final ExternalApiClient externalApiClient;
    private final HolidayMutationService holidayMutationService;

    @Scheduled(cron = "0 0 1 2 1 *", zone = "Asia/Seoul")
    public void syncHolidays() {
        log.info("휴일 동기화 작업 실행...");

        ZoneId seoul = ZoneId.systemDefault();

        int currentYear = Year.now(seoul).getValue();
        int previousYear = currentYear - 1;

        List<ExternalCountry> extCountries = externalApiClient.fetchAllCountries();
        Set<String> countryCodes = extCountries.stream().map(ExternalCountry::getCountryCode).collect(Collectors.toSet());

        countryCodes.forEach(countryCode -> {

            try {
                log.info("Upserting holidays: year={}, countryCode={}", previousYear, countryCode);
                holidayMutationService.upsertByYearAndCountry(previousYear, countryCode);

                log.info("Upserting holidays: year={}, countryCode={}", currentYear, countryCode);
                holidayMutationService.upsertByYearAndCountry(currentYear, countryCode);
            } catch (Exception e) {
                log.error("휴일 동기화 작업 중 오류 발생 countryCode={}", countryCode, e);
            }
        });

        log.info("휴일 동기화 작업 완료.");
    }
}
