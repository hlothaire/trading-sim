package com.acme.trader.api.backtest;

import com.acme.trader.api.backtest.dto.BacktestRequest;
import com.acme.trader.api.backtest.dto.BacktestResult;
import com.acme.trader.api.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BacktestController.class)
@Import(SecurityConfig.class)
class BacktestControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    BacktestService svc;

    @Test
    void runEndpointReturnsResult() throws Exception {
        BacktestResult mockRes = new BacktestResult(0.1, 0.2, 0.3, List.of(), List.of());
        when(svc.run(any(BacktestRequest.class))).thenReturn(mockRes);

        String body = """
            {"symbol":"AAPL","from":"2024-01-01T00:00:00Z","to":"2024-01-08T00:00:00Z","fast":1,"slow":2,"initialCash":1000.0}
            """;

        mvc.perform(post("/api/backtest/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReturn").value(0.1));

        verify(svc).run(any(BacktestRequest.class));
    }

    @Test
    void runEndpointPropagatesError() throws Exception {
        when(svc.run(any(BacktestRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "No market data for symbol AAPL"));

        String body = """
            {"symbol":"AAPL","from":"2024-01-01T00:00:00Z","to":"2024-01-08T00:00:00Z","fast":1,"slow":2,"initialCash":1000.0}
            """;

        mvc.perform(post("/api/backtest/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("No market data for symbol AAPL"));
    }
}
