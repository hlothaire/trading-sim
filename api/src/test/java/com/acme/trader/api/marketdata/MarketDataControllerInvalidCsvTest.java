package com.acme.trader.api.marketdata;

import com.acme.trader.api.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import org.springframework.mock.web.MockMultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MarketDataController.class)
@Import({SecurityConfig.class, CsvImportService.class})
class MarketDataControllerInvalidCsvTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    MarketDataService svc;

    @MockBean
    CandleRepository repo;

    @Test
    void invalidTimestampReturnsBadRequest() throws Exception {
        String csv = "timestamp,open,high,low,close,volume\n" +
                "not-a-timestamp,1,1,1,1,1\n";
        MockMultipartFile file = new MockMultipartFile(
                "file", "data.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        mvc.perform(multipart("/api/marketdata/import").file(file).param("symbol", "AAPL"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Invalid CSV")));
    }
}

