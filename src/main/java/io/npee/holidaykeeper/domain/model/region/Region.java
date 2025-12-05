package io.npee.holidaykeeper.domain.model.region;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String countryCode;
    private String regionCode;
    @Column(unique = true, nullable = false)
    private String isoCode;
}
