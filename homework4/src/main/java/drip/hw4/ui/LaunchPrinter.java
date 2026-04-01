package drip.hw4.ui;

import drip.hw4.model.Launch;

import java.util.List;

public class LaunchPrinter {
    public void printLaunchList(List<Launch> launches) {
        if (launches.isEmpty()) {
            System.out.println("Запуски не найдены.");
            return;
        }

        for (Launch launch : launches) {
            System.out.printf(
                    "#%d  %s | %s | Успех: %s%n",
                    launch.getFlightNumber(),
                    safeText(launch.getName()),
                    extractDate(launch.getDateUtc()),
                    formatSuccess(launch.getSuccess())
            );
        }
    }

    public void printLaunchDetails(Launch launch) {
        System.out.println("Запуск: " + safeText(launch.getName()));
        System.out.println("Номер: " + launch.getFlightNumber());
        System.out.println("Дата: " + safeText(launch.getDateUtc()));
        System.out.println("Успех: " + formatSuccess(launch.getSuccess()));
        System.out.println("Описание: " + defaultIfBlank(launch.getDetails()));
    }

    private String extractDate(String dateUtc) {
        if (dateUtc == null || dateUtc.length() < 10) {
            return "дата неизвестна";
        }
        return dateUtc.substring(0, 10);
    }

    private String formatSuccess(Boolean success) {
        if (success == null) {
            return "неизвестно";
        }
        return success ? "да" : "нет";
    }

    private String safeText(String value) {
        return defaultIfBlank(value);
    }

    private String defaultIfBlank(String value) {
        if (value == null || value.isBlank()) {
            return "нет данных";
        }
        return value;
    }
}
