import org.example.Configuration;
import org.example.collector.DataCollector;
import org.example.persistence.CurrencyRepository;

import java.io.IOException;

public class TestTest {

    public static void main(String[] args) throws IOException {
        System.setProperty("app_settings", "C:/Users/Pavel/IdeaProjects/Pricer/src/test/resources/app-test.properties");
        System.setProperty("log4j2.configurationFile", "/Users/Pavel/IdeaProjects/Pricer/log4j2.xml");

        DataCollector dataCollector = new DataCollector();

        dataCollector.collect();


        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        dataCollector.stop();
    }
}
