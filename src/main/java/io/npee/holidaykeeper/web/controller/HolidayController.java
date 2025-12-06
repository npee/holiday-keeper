package io.npee.holidaykeeper.web.controller;

import io.npee.holidaykeeper.domain.model.holiday.HolidayType;
import io.npee.holidaykeeper.domain.service.HolidayQueryService;
import io.npee.holidaykeeper.web.controller.dto.HolidayResponse;
import io.npee.holidaykeeper.web.controller.dto.HolidaySearchCondition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Holiday API", description = "공휴일 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/holidays")
public class HolidayController {

    private final HolidayQueryService holidayQueryService;

    @Operation(
            summary = "공휴일 조회",
            description = "연도 범위, 국가코드, 지역코드, 공휴일 타입을 기준으로 공휴일 목록을 페이징 조회합니다."
    )
    @GetMapping
    public Page<HolidayResponse> searchHolidays(
            @Parameter(
                    description = "조회 시작 연도 (예: 2020)",
                    example = "2020"
            )
            @RequestParam(required = false) Integer from,

            @Parameter(
                    description = "조회 종료 연도 (예: 2025). from 연도보다 크거나 같아야 합니다.",
                    example = "2025"
            )
            @RequestParam(required = false) Integer to,

            @Parameter(
                    description = "국가 코드 (ISO-3166-1, 예: KR, US)",
                    example = "KR"
            )
            @RequestParam(required = false) String countryCode,

            @Parameter(
                    description = "지역 코드 (ISO-3166-2, 예: US-TX). 한국처럼 지역 구분이 없으면 사용하지 않습니다.",
                    example = "US-TX"
            )
            @RequestParam(required = false) String regionIso,

            @Parameter(
                    description = "공휴일 타입 (예: PUBLIC, BANK, SCHOOL)",
                    schema = @Schema(implementation = HolidayType.class)
            )
            @RequestParam(required = false) HolidayType type,

            @ParameterObject
            @PageableDefault Pageable pageable
    ) {

        if (from != null && to != null && from > to) {
            throw new IllegalArgumentException("from 연도는 to 연도보다 작거나 같아야 합니다.");
        }

        HolidaySearchCondition condition = HolidaySearchCondition.builder()
                .from(from)
                .to(to)
                .countryCode(countryCode)
                .regionIso(regionIso)
                .type(type)
                .build();

        return holidayQueryService.search(condition, pageable);
    }
}