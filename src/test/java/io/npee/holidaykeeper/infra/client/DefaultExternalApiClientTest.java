package io.npee.holidaykeeper.infra.client;

import io.npee.holidaykeeper.domain.model.country.ExternalCountry;
import io.npee.holidaykeeper.domain.port.ExternalApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DefaultExternalApiClientTest {

    @Autowired
    private ExternalApiClient client;

    @Test
    void testFetchCountry_http_call() {
        List<ExternalCountry> externalCountries = client.fetchAllCountries();
        assertFalse(externalCountries.isEmpty());
    }

}