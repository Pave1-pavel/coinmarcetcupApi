package org.example.collector;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Configuration;
import org.example.api.CoinMarketCapApiHelper;
import org.example.persistence.CurrencyRepository;
import org.example.service.CurrencyService;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class DataCollector {

    //private static final Logger log = LogManager.getLogger(DataCollector.class.getName());

    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    private final CurrencyRepository currencyRepository = new CurrencyRepository();
    private final CurrencyService currencyService = new CurrencyService(currencyRepository);
    private ScheduledFuture<?> scheduleHandler;
    private static int requestRate = 30;

    public DataCollector() throws IOException {
        if (Configuration.getProperty("request_rate") != null) {
            requestRate = Integer.parseInt(Configuration.getProperty("request_rate"));
        }
    }

    public void collect() {
        scheduleHandler = scheduler.scheduleAtFixedRate(
                new CurrencyQuoteCollector(currencyService),
                0, requestRate, SECONDS);
    }

    @SneakyThrows
    public void stop() {
        if (scheduleHandler != null) {
            scheduleHandler.cancel(false);
        }
        scheduler.awaitTermination(3, SECONDS);
        closeRepository();
        scheduler.shutdownNow();
    }

    @SneakyThrows
    public void closeRepository() {
        currencyRepository.close();
    }

    public void printStatistics() {
        try {
            Configuration.currencies.forEach(currency -> {
                System.out.println(currency + " average:");
                currencyRepository.printAvgPrice(currency);
            });
        } catch (Exception e) {
         //   log.info(e.getMessage());
        }
    }

}
