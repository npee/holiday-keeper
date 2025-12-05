package io.npee.holidaykeeper.domain.model.holiday;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Holiday {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String countryCode;
    private LocalDate date;
    private String localName;
    private String name;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "holiday_type",
            joinColumns = @JoinColumn(name = "holiday_id")
    )
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<HolidayType> types = new HashSet<>();

    @OneToMany(mappedBy = "holiday", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<HolidayRegion> holidayRegions = new HashSet<>();

    private boolean fixed;
    private boolean global;
    private int launchYear;
}
