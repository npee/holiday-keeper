package io.npee.holidaykeeper.infra.client.dto;

import io.npee.holidaykeeper.domain.model.country.ExternalCountry;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ExternalCountryResponse {
    private String countryCode;
    private String name;

    // toDomain method
    public ExternalCountry toDomain() {
        return ExternalCountry.builder()
                .countryCode(this.countryCode)
                .name(this.name)
                .build();
    }
}
