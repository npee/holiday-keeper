package io.npee.holidaykeeper.domain.repository;

import io.npee.holidaykeeper.domain.model.holiday.Holiday;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HolidayRepository {

    private final HolidayJpaRepository holidayJpaRepository;

    public List<Holiday> saveAll(Iterable<Holiday> holidays) {
        return holidayJpaRepository.saveAll(holidays);
    }

    public void deleteAllInBatch() {
        holidayJpaRepository.deleteAllInBatch();
    }

    public Holiday save(Holiday holiday) {
        return holidayJpaRepository.save(holiday);
    }

    public List<Holiday> findAll() {
        return holidayJpaRepository.findAll();
    }

    public long count() {
        return holidayJpaRepository.count();
    }
}
