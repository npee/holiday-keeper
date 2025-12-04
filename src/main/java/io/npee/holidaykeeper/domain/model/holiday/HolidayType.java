package io.npee.holidaykeeper.domain.model.holiday;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HolidayType {
    PUBLIC("공휴일"),
    BANK("은행휴일"),
    SCHOOL("학교휴일"),
    AUTHORITIES("관공서휴일"),
    OPTIONAL("임시공휴일"),
    OBSERVANCE("기념일");

    private final String description;
}
