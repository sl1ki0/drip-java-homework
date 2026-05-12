package drip.hw4;

import drip.hw4.cache.ApiCache;
import drip.hw4.client.SpaceXHttpClient;
import drip.hw4.json.JsonBuilder;
import drip.hw4.json.JsonParser;
import drip.hw4.service.SpaceXLaunchService;
import drip.hw4.storage.FileStorage;
import drip.hw4.ui.LaunchPrinter;
import drip.hw4.ui.SpaceXLaunchExplorerApp;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SpaceXHttpClient httpClient = new SpaceXHttpClient();
        JsonParser jsonParser = new JsonParser();
        JsonBuilder jsonBuilder = new JsonBuilder();
        FileStorage fileStorage = new FileStorage();
        ApiCache apiCache = new ApiCache(Path.of("cache"), Duration.ofMinutes(5), fileStorage);
        SpaceXLaunchService launchService = new SpaceXLaunchService(httpClient, jsonParser, jsonBuilder, apiCache);
        LaunchPrinter launchPrinter = new LaunchPrinter();
        Scanner scanner = new Scanner(System.in);

        new SpaceXLaunchExplorerApp(scanner, launchService, launchPrinter).run();
    }
}
