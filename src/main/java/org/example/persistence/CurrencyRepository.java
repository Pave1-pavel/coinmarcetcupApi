package org.example.persistence;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.example.Configuration;

import java.io.IOException;
import java.io.StringReader;

public class CurrencyRepository {

    RestClient restClient;
    ElasticsearchTransport elasticsearchTransport;
    ElasticsearchClient elasticsearchClient;
    String indexName = Configuration.getProperty("index_name");

    public CurrencyRepository() throws IOException {
        restClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http")
        ).build();
        JacksonJsonpMapper jsonMapper = new JacksonJsonpMapper();
        elasticsearchTransport = new RestClientTransport(restClient, jsonMapper);
        elasticsearchClient = new ElasticsearchClient(elasticsearchTransport);
    }

    public void save(String data) throws IOException {
        var stringReader = new StringReader(data);
        elasticsearchClient.index(i -> i.index(indexName)
                .withJson(stringReader));
    }

    @SneakyThrows
    public void printAvgPrice(String currency) {

        Query query = RangeQuery.of(r -> r
                .field("status.timestamp")
                .from("now-1h/h")
                .to("now/h")
        )._toQuery();

        SearchResponse<Void> response = elasticsearchClient.search(b -> b
                        .index(indexName)
                        .size(0)
                        .query(query)
                        .aggregations("avg_price", agg -> agg
                                .avg(avg -> avg.field("data.%s.quote.USD.price".formatted(currency)))
                        ),
                Void.class);

        System.out.println(
                response.aggregations()
                        .get("avg_price")
                        .avg()
                        .toString());
    }

    public void close() throws IOException {
        elasticsearchTransport.close();
        restClient.close();
    }
}
