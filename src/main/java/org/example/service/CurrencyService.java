package org.example.service;

import org.example.persistence.CurrencyRepository;

import java.io.IOException;

public class CurrencyService {

    private final CurrencyRepository currencyRepository;
//    private final JSONParser jsonParser = new JSONParser();
//    private final ObjectMapper mapper;
//    private final ObjectWriter writer;
//    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
//        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
//        writer = mapper.writer().withDefaultPrettyPrinter();
    }

    public void saveQuoteData(String data) throws IOException {

        currencyRepository.save(data);

//        JSONObject quoteData = (JSONObject) jsonParser.parse(data);
//        JSONObject currencies = (JSONObject) quoteData.get("data");
//        for (Object o : currencies.keySet()) {
//            String currency = (String) o;
//            JSONObject currencyData = (JSONObject) currencies.get(currency);
//            JSONObject quote = (JSONObject) currencyData.get("quote");
//
//            Map<String, Object> convertedData = new HashMap<>();
//
//            convertedData.put("request_time", LocalDateTime.now().format(dateFormat));
//            convertedData.put("symbol", currency);
//            for (Object keyObj : quote.keySet()) {
//                String currencyTo = (String) keyObj;
//                convertedData.put("quote", quote.get(currencyTo));
//            }
//            String res = writer.writeValueAsString(Map.of(currency, convertedData));
//            System.out.println(res);
//            currencyRepository.save(res);
//        }
    }
}
