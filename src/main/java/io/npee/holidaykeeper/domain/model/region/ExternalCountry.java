package io.npee.holidaykeeper.domain.model.region;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ExternalCountry {
    private String countryCode;
    private String name;
}
