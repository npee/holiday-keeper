package io.npee.holidaykeeper.infra.runner;

import io.npee.holidaykeeper.domain.service.HolidayReloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "batch.initial-holiday-load.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
@Slf4j
public class InitialHolidayLoadRunner implements ApplicationRunner {

    private final HolidayReloadService holidayReloadService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("휴일 데이터 초기화 시작...");
        holidayReloadService.reloadHolidays();
        log.info("휴일 데이터 초기화 완료");
    }
}