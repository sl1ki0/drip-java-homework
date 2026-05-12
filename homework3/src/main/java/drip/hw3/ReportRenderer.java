package drip.hw3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import drip.hw3.argumentParser.ParsedArguments;

import java.util.List;
import java.util.StringJoiner;

public final class ReportRenderer {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ReportRenderer() {
    }

    public static String render(LogAnalysisResult result, ParsedArguments arguments) {
        return switch (arguments.format()) {
            case "json" -> renderJson(result);
            case "markdown" -> renderMarkdown(result, arguments);
            default -> throw new IllegalArgumentException("Неподдерживаемый формат: " + arguments.format());
        };
    }

    private static String renderJson(LogAnalysisResult result) {
        JsonObject root = new JsonObject();

        JsonArray files = new JsonArray();
        for (String file : result.files()) {
            files.add(file);
        }
        root.add("files", files);
        root.addProperty("totalRequestsCount", result.totalRequestsCount());

        JsonObject responseSize = new JsonObject();
        responseSize.addProperty("average", result.responseSizeInBytes().average());
        responseSize.addProperty("max", result.responseSizeInBytes().max());
        responseSize.addProperty("p95", result.responseSizeInBytes().p95());
        root.add("responseSizeInBytes", responseSize);

        JsonArray resources = new JsonArray();
        for (LogAnalysisResult.ResourceStat resource : result.resources()) {
            JsonObject item = new JsonObject();
            item.addProperty("resource", resource.resource());
            item.addProperty("totalRequestsCount", resource.totalRequestsCount());
            resources.add(item);
        }
        root.add("resources", resources);

        JsonArray responseCodes = new JsonArray();
        for (LogAnalysisResult.ResponseCodeStat responseCode : result.responseCodes()) {
            JsonObject item = new JsonObject();
            item.addProperty("code", responseCode.code());
            item.addProperty("totalResponsesCount", responseCode.totalResponsesCount());
            responseCodes.add(item);
        }
        root.add("responseCodes", responseCodes);

        JsonArray requestsPerDate = getJsonElements(result);
        root.add("requestsPerDate", requestsPerDate);

        return GSON.toJson(root);
    }

    private static JsonArray getJsonElements(LogAnalysisResult result) {
        JsonArray requestsPerDate = new JsonArray();
        for (LogAnalysisResult.RequestsPerDateStat requestsPerDateStat : result.requestsPerDate()) {
            JsonObject item = new JsonObject();
            item.addProperty("date", requestsPerDateStat.date());
            item.addProperty("weekday", requestsPerDateStat.weekday());
            item.addProperty("totalRequestsCount", requestsPerDateStat.totalRequestsCount());
            item.addProperty("totalRequestsPercentage", requestsPerDateStat.totalRequestsPercentage());
            requestsPerDate.add(item);
        }
        return requestsPerDate;
    }

    private static String renderMarkdown(LogAnalysisResult result, ParsedArguments arguments) {
        StringBuilder builder = new StringBuilder();

        builder.append("#### Общая информация\n");
        builder.append("| Метрика | Значение |\n");
        builder.append("|---|---|\n");
        builder.append("| Файл(-ы) | ").append(joinFiles(result.files())).append(" |\n");
        builder.append("| Начальная дата | ").append(resolveFromDate(result, arguments)).append(" |\n");
        builder.append("| Конечная дата | ").append(resolveToDate(result, arguments)).append(" |\n");
        builder.append("| Количество запросов | ").append(result.totalRequestsCount()).append(" |\n");
        builder.append("| Средний размер ответа | ").append(result.responseSizeInBytes().average()).append("b |\n");
        builder.append("| Максимальный размер ответа | ").append(result.responseSizeInBytes().max()).append("b |\n");
        builder.append("| 95p размера ответа | ").append(result.responseSizeInBytes().p95()).append("b |\n\n");

        builder.append("#### Запрашиваемые ресурсы\n");
        builder.append("| Ресурс | Количество |\n");
        builder.append("|---|---|\n");
        appendResourceRows(builder, result.resources());
        builder.append('\n');

        builder.append("#### Коды ответа\n");
        builder.append("| Код | Количество |\n");
        builder.append("|---|---|\n");
        appendResponseCodeRows(builder, result.responseCodes());
        builder.append('\n');

        builder.append("#### Запросы по датам\n");
        builder.append("| Дата | День недели | Количество | Процент |\n");
        builder.append("|---|---|---|---|\n");
        appendRequestsPerDateRows(builder, result.requestsPerDate());

        return builder.toString();
    }

    private static void appendResourceRows(StringBuilder builder, List<LogAnalysisResult.ResourceStat> resources) {
        if (resources.isEmpty()) {
            builder.append("| - | 0 |\n");
            return;
        }

        for (LogAnalysisResult.ResourceStat resource : resources) {
            builder.append("| ")
                    .append(resource.resource())
                    .append(" | ")
                    .append(resource.totalRequestsCount())
                    .append(" |\n");
        }
    }

    private static void appendResponseCodeRows(
            StringBuilder builder,
            List<LogAnalysisResult.ResponseCodeStat> responseCodes
    ) {
        if (responseCodes.isEmpty()) {
            builder.append("| - | 0 |\n");
            return;
        }

        for (LogAnalysisResult.ResponseCodeStat responseCode : responseCodes) {
            builder.append("| ")
                    .append(responseCode.code())
                    .append(" | ")
                    .append(responseCode.totalResponsesCount())
                    .append(" |\n");
        }
    }

    private static void appendRequestsPerDateRows(
            StringBuilder builder,
            List<LogAnalysisResult.RequestsPerDateStat> requestsPerDate
    ) {
        if (requestsPerDate.isEmpty()) {
            builder.append("| - | - | 0 | 0.0 |\n");
            return;
        }

        for (LogAnalysisResult.RequestsPerDateStat entry : requestsPerDate) {
            builder.append("| ")
                    .append(entry.date())
                    .append(" | ")
                    .append(entry.weekday())
                    .append(" | ")
                    .append(entry.totalRequestsCount())
                    .append(" | ")
                    .append(entry.totalRequestsPercentage())
                    .append(" |\n");
        }
    }

    private static String joinFiles(List<String> files) {
        StringJoiner joiner = new StringJoiner(", ");
        for (String file : files) {
            joiner.add(file);
        }
        return joiner.toString();
    }

    private static String resolveFromDate(LogAnalysisResult result, ParsedArguments arguments) {
        if (arguments.from() != null) {
            return arguments.from();
        }

        return result.requestsPerDate().isEmpty() ? "-" : result.requestsPerDate().getFirst().date();
    }

    private static String resolveToDate(LogAnalysisResult result, ParsedArguments arguments) {
        if (arguments.to() != null) {
            return arguments.to();
        }

        if (result.requestsPerDate().isEmpty()) {
            return "-";
        }

        return result.requestsPerDate().getLast().date();
    }
}
