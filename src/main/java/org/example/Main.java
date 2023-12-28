package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.collector.DataCollector;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class.getName());
    private static DataCollector dataCollector;

    public static void main(String[] args) throws IOException {

        dataCollector = new DataCollector();


        String action = "";
        if (args.length > 0) {
            action = args[0];
        }

        if (action.equalsIgnoreCase("-S") ||
                action.equalsIgnoreCase("stat")) {

            dataCollector.printStatistics();
            dataCollector.closeRepository();
            return;
        }

        dataCollector.collect();

        waitForUserCancel();
    }

    private static void waitForUserCancel() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String str = scanner.next();
            if ("Q".equalsIgnoreCase(str)) {
                dataCollector.stop();
                return;
            }
        }
    }
}