package drip.hw4.ui;

import drip.hw4.model.Launch;
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
        System.out.println("6. Выход");
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
                case "6" -> false;
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
        List<Launch> launches = launchService.getAllLaunches();
        launchPrinter.printLaunchList(launches);
    }

    private void showLatestLaunch() throws SpaceXServiceException {
        Launch latestLaunch = launchService.getLatestLaunch();
        launchPrinter.printLaunchDetails(latestLaunch);
    }

    private void searchLaunchesByDate() throws SpaceXServiceException {
        LocalDate startDate = requestDate("Введите дату начала (YYYY-MM-DD): ");
        LocalDate endDate = requestDate("Введите дату конца (YYYY-MM-DD): ");

        if (endDate.isBefore(startDate)) {
            System.out.println("Дата конца не может быть раньше даты начала.");
            return;
        }

        List<Launch> launches = launchService.findLaunchesByDate(startDate, endDate);
        launchPrinter.printLaunchList(launches);
    }

    private void showSuccessfulLaunches() throws SpaceXServiceException {
        List<Launch> launches = launchService.getSuccessfulLaunches();
        launchPrinter.printLaunchList(launches);
    }

    private void showFailedLaunches() throws SpaceXServiceException {
        List<Launch> launches = launchService.getFailedLaunches();
        launchPrinter.printLaunchList(launches);
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
