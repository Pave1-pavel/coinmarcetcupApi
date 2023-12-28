package org.example.api;

import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Configuration;
import org.example.exception.ApiMonthRateException;
import org.example.exception.ApiRateException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoinMarketCapApiHelper {

    private static final Logger log = LogManager.getLogger(CoinMarketCapApiHelper.class.getName());

    private static final String API_URL = Configuration.getProperty("api_url");
    private static final String CURRENCIES = Configuration.getProperty("currencies");
    private static final int MONTH_RATE_ERROR_CODE = 1010;


    private static CoinMarketCapApiHelper instance;
    private final List<String> apiKeys = new ArrayList<>();
    private volatile int nextKeyIndex;
    private final HttpGet httpRequest;
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    private CoinMarketCapApiHelper() throws URISyntaxException {
        var keys = Arrays.stream(Configuration.getProperty("api_keys").split(",")).toList();
        apiKeys.addAll(keys);

        URIBuilder uriBuilder = new URIBuilder(API_URL);
        uriBuilder.addParameters(List.of(new BasicNameValuePair("symbol", CURRENCIES))).build();
        httpRequest = new HttpGet(uriBuilder.build());
        httpRequest.setHeader(HttpHeaders.ACCEPT, "application/json");
    }

    @SneakyThrows
    public static synchronized CoinMarketCapApiHelper getInstance() {
        if (instance == null) {
            instance = new CoinMarketCapApiHelper();
        }
        return instance;
    }

    public String getQuote() throws IOException {

        httpRequest.setHeader("X-CMC_PRO_API_KEY", getNextKey());

        String responseContent;
        try (var response = httpClient.execute(httpRequest)) {

            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity);
            EntityUtils.consume(entity);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_TOO_MANY_REQUESTS) {
                parseTooManyRequestsResponse(responseContent);
            }
        }
        return responseContent;
    }

    @SneakyThrows
    private void parseTooManyRequestsResponse(String responseContent) {
        JSONParser jsonParser = new JSONParser();
        JSONObject content = (JSONObject) jsonParser.parse(responseContent);
        JSONObject status = (JSONObject) content.get("status");
        log.info(status);
        Long errorCode = (Long) status.get("error_code");
        String errorMessage = (String) status.get("error_message");

        if (errorCode.intValue() == MONTH_RATE_ERROR_CODE) {
            throw new ApiMonthRateException(errorMessage);
        }

        throw new ApiRateException(errorMessage);
    }

    private synchronized String getNextKey() {
        String nextKey = apiKeys.get(nextKeyIndex);
        nextKeyIndex++;
        if (nextKeyIndex == apiKeys.size()) {
            nextKeyIndex = 0;
        }
        return nextKey;
    }

}
