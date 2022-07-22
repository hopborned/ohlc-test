package ru.ananev.ohlctest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ananev.ohlctest.model.Ohlc;
import ru.ananev.ohlctest.model.OhlcPeriod;
import ru.ananev.ohlctest.model.OhlcService;
import ru.ananev.ohlctest.model.Quote;

import java.util.List;

@RestController
public class TestController {

    private final OhlcService ohlcService;

    public TestController(OhlcService ohlcService) {
        this.ohlcService = ohlcService;
    }

    @PostMapping
    public void sendQuote(Quote quote) {
        ohlcService.onQuote(quote);
    }

    @GetMapping
    public List<Ohlc> get(@RequestParam("instrument") long instrumentId,
                          @RequestParam("period") OhlcPeriod period,
                          @RequestParam("current") boolean current) {
        return current ? ohlcService.getHistoricalAndCurrent(instrumentId, period)
                : ohlcService.getHistorical(instrumentId, period);
    }
}
