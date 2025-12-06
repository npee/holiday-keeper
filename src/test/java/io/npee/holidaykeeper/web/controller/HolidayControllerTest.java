package io.npee.holidaykeeper.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.npee.holidaykeeper.domain.model.holiday.Holiday;
import io.npee.holidaykeeper.domain.model.holiday.HolidayRegion;
import io.npee.holidaykeeper.domain.model.holiday.HolidayType;
import io.npee.holidaykeeper.domain.model.region.Region;
import io.npee.holidaykeeper.domain.repository.HolidayRegionRepository;
import io.npee.holidaykeeper.domain.repository.HolidayRepository;
import io.npee.holidaykeeper.domain.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class HolidayControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    HolidayRegionRepository holidayRegionRepository;

    @BeforeEach
    void cleanUp() {
        holidayRegionRepository.deleteAll();
        holidayRepository.deleteAll();
        regionRepository.deleteAll();
    }

    @Test
    @DisplayName("from/to, countryCode로 공휴일을 페이징 조회한다")
    void searchHolidays_success() throws Exception {

        // given
        Region krRegion = regionRepository.save(
                Region.builder()
                        .countryCode("KR")
                        .regionCode(null)
                        .isoCode("KR")
                        .build()
        );

        Holiday holiday = Holiday.builder()
                .countryCode("KR")
                .date(LocalDate.of(2025, 1, 1))
                .localName("새해 첫날")
                .name("New Year's Day")
                .fixed(true)
                .global(true)
                .launchYear(1949)
                .build();

        holiday.getTypes().add(HolidayType.PUBLIC);

        HolidayRegion holidayRegion = HolidayRegion.builder()
                .holiday(holiday)
                .region(krRegion)
                .build();
        holiday.getHolidayRegions().add(holidayRegion);

        holidayRepository.save(holiday);

        // when & then
        mockMvc.perform(get("/api/holidays")
                        .param("from", "2024")
                        .param("to", "2025")
                        .param("countryCode", "KR")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].countryCode").value("KR"))
                .andExpect(jsonPath("$.content[0].date").value("2025-01-01"))
                .andExpect(jsonPath("$.content[0].localName").value("새해 첫날"))
                .andExpect(jsonPath("$.content[0].types[0]").value("PUBLIC"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("from > to 인 경우 400 에러를 반환한다")
    void searchHolidays_범위_오류() throws Exception {
        // when & then
        mockMvc.perform(get("/api/holidays")
                        .param("from", "2025")
                        .param("to", "2020"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists());
    }
}