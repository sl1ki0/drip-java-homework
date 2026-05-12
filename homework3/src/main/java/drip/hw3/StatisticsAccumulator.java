package drip.hw3;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class StatisticsAccumulator {
    private final List<Long> responseSizes = new ArrayList<>();
    private final Map<String, Long> resourceCounts = new HashMap<>();
    private final Map<Integer, Long> responseCodeCounts = new HashMap<>();
    private final Map<LocalDate, Long> requestsPerDate = new HashMap<>();

    private long totalRequestsCount;
    private long totalResponseSize;
    private long maxResponseSize;

    void add(LogEntry entry) {
        totalRequestsCount++;
        totalResponseSize += entry.responseSize();
        maxResponseSize = Math.max(maxResponseSize, entry.responseSize());
        responseSizes.add(entry.responseSize());

        resourceCounts.merge(entry.resource(), 1L, Long::sum);
        responseCodeCounts.merge(entry.responseCode(), 1L, Long::sum);
        requestsPerDate.merge(entry.date(), 1L, Long::sum);
    }

    LogAnalysisResult toResult(List<String> files) {
        return new LogAnalysisResult(
                files,
                totalRequestsCount,
                buildResponseSizeStats(),
                buildResourceStats(),
                buildResponseCodeStats(),
                buildRequestsPerDateStats()
        );
    }

    private LogAnalysisResult.ResponseSizeStats buildResponseSizeStats() {
        if (totalRequestsCount == 0) {
            return new LogAnalysisResult.ResponseSizeStats(0.0, 0.0, 0.0);
        }

        double average = round((double) totalResponseSize / totalRequestsCount);
        double max = round(maxResponseSize);
        double p95 = round(calculatePercentile95());

        return new LogAnalysisResult.ResponseSizeStats(average, max, p95);
    }

    private double calculatePercentile95() {
        List<Long> sortedSizes = new ArrayList<>(responseSizes);
        Collections.sort(sortedSizes);

        int index = (int) Math.ceil(sortedSizes.size() * 0.95) - 1;
        index = Math.max(index, 0);

        return sortedSizes.get(index);
    }

    private List<LogAnalysisResult.ResourceStat> buildResourceStats() {
        return resourceCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(10)
                .map(entry -> new LogAnalysisResult.ResourceStat(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<LogAnalysisResult.ResponseCodeStat> buildResponseCodeStats() {
        return responseCodeCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(entry -> new LogAnalysisResult.ResponseCodeStat(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<LogAnalysisResult.RequestsPerDateStat> buildRequestsPerDateStats() {
        return requestsPerDate.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new LogAnalysisResult.RequestsPerDateStat(
                        entry.getKey().toString(),
                        entry.getKey().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        entry.getValue(),
                        totalRequestsCount == 0
                                ? 0.0
                                : round(entry.getValue() * 100.0 / totalRequestsCount)
                ))
                .toList();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
