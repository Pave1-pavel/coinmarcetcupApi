package org.example.collector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.api.CoinMarketCapApiHelper;
import org.example.exception.ApiMonthRateException;
import org.example.exception.ApiRateException;
import org.example.service.CurrencyService;

import java.io.IOException;

public class CurrencyQuoteCollector implements Runnable {

        CurrencyService currencyService;
        private static final Logger log = LogManager.getLogger(CurrencyQuoteCollector.class.getName());

    public CurrencyQuoteCollector(CurrencyService currencyService) {
            this.currencyService = currencyService;
        }

        @Override
        public void run () {
            try {
                log.info("Retrieving currency quote from CoinMarketCap API");
                String currencyData = CoinMarketCapApiHelper.getInstance().getQuote();
                log.info("Saving currency quote to DB");
                currencyService.saveQuoteData(currencyData);
            } catch (IOException e) {
                log.error("Cannot access content", e);
            } catch (ApiRateException | ApiMonthRateException e) {
                log.error("Too many requests", e);
            }
        }
    }