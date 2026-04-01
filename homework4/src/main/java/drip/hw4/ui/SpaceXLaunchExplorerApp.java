package drip.hw4.ui;

import drip.hw4.model.Launch;
import drip.hw4.service.ServiceResponse;
import drip.hw4.service.SpaceXLaunchService;
import drip.hw4.service.SpaceXServiceException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class SpaceXLaunchExplorerApp {
    private final Scanner scanner;
    private final SpaceXLaunchService launchService;
    private final LaunchPrinter launchPrinter;

    public SpaceXLaunchExplorerApp(
            Scanner scanner,
            SpaceXLaunchService launchService,
            LaunchPrinter launchPrinter
    ) {
        this.scanner = scanner;
        this.launchService = launchService;
        this.launchPrinter = launchPrinter;
    }

    public void run() {
        boolean isRunning = true;

        while (isRunning) {
            printMenu();
            String choice = scanner.nextLine().trim();
            System.out.println();

            isRunning = handleChoice(choice);

            if (isRunning) {
                System.out.println();
            }
        }
    }

    private void printMenu() {
        System.out.println("=== SpaceX Launch Explorer ===");
        System.out.println("1. Показать все запуски");
        System.out.println("2. Показать последний запуск");
        System.out.println("3. Поиск запусков по дате");
        System.out.println("4. Показать только успешные запуски");
        System.out.println("5. Показать только неудачные запуски");
        System.out.println("6. Очистить кеш");
        System.out.println("7. Выход");
        System.out.print("Выберите пункт меню: ");
    }

    private boolean handleChoice(String choice) {
        try {
            return switch (choice) {
                case "1" -> {
                    showAllLaunches();
                    yield true;
                }
                case "2" -> {
                    showLatestLaunch();
                    yield true;
                }
                case "3" -> {
                    searchLaunchesByDate();
                    yield true;
                }
                case "4" -> {
                    showSuccessfulLaunches();
                    yield true;
                }
                case "5" -> {
                    showFailedLaunches();
                    yield true;
                }
                case "6" -> {
                    clearCache();
                    yield true;
                }
                case "7" -> false;
                default -> {
                    System.out.println("Неизвестный пункт меню. Попробуйте еще раз.");
                    yield true;
                }
            };
        } catch (SpaceXServiceException e) {
            System.out.println("[!] " + e.getMessage());
            return true;
        }
    }

    private void showAllLaunches() throws SpaceXServiceException {
        ServiceResponse<List<Launch>> response = launchService.getAllLaunches();
        printNotice(response);
        launchPrinter.printLaunchList(response.getData());
    }

    private void showLatestLaunch() throws SpaceXServiceException {
        ServiceResponse<Launch> response = launchService.getLatestLaunch();
        printNotice(response);
        launchPrinter.printLaunchDetails(response.getData());
    }

    private void searchLaunchesByDate() throws SpaceXServiceException {
        LocalDate startDate = requestDate("Введите дату начала (YYYY-MM-DD): ");
        LocalDate endDate = requestDate("Введите дату конца (YYYY-MM-DD): ");

        if (endDate.isBefore(startDate)) {
            System.out.println("Дата конца не может быть раньше даты начала.");
            return;
        }

        ServiceResponse<List<Launch>> response = launchService.findLaunchesByDate(startDate, endDate);
        printNotice(response);
        launchPrinter.printLaunchList(response.getData());
    }

    private void showSuccessfulLaunches() throws SpaceXServiceException {
        ServiceResponse<List<Launch>> response = launchService.getSuccessfulLaunches();
        printNotice(response);
        launchPrinter.printLaunchList(response.getData());
    }

    private void showFailedLaunches() throws SpaceXServiceException {
        ServiceResponse<List<Launch>> response = launchService.getFailedLaunches();
        printNotice(response);
        launchPrinter.printLaunchList(response.getData());
    }

    private void clearCache() throws SpaceXServiceException {
        launchService.clearCache();
        System.out.println("Кеш очищен.");
    }

    private void printNotice(ServiceResponse<?> response) {
        if (response.hasNotice()) {
            System.out.println(response.getNotice());
        }
    }

    private LocalDate requestDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String rawDate = scanner.nextLine().trim();

            try {
                return LocalDate.parse(rawDate);
            } catch (DateTimeParseException e) {
                System.out.println("Некорректная дата. Используйте формат YYYY-MM-DD.");
            }
        }
    }
}
