package io.npee.holidaykeeper.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class HolidayExternalFetchServiceTest {

    @Autowired
    private HolidayExternalFetchService holidayExternalFetchService;

    @Test
    @DisplayName("최근 5년간의 모든 국가의 휴일들을 외부 API 통해 조회한다.")
    void testFetchAllHolidaysForRecentFiveYears_http_call() {
        var holidays = holidayExternalFetchService.fetchAllHolidaysForRecentFiveYears();
        assertFalse(holidays.isEmpty());
    }
}