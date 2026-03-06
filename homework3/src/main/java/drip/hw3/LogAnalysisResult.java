package drip.hw3;

import java.util.List;

public record LogAnalysisResult(
        List<String> files,
        long totalRequestsCount,
        ResponseSizeStats responseSizeInBytes,
        List<ResourceStat> resources,
        List<ResponseCodeStat> responseCodes,
        List<RequestsPerDateStat> requestsPerDate
) {
    public record ResponseSizeStats(
            double average,
            double max,
            double p95
    ) {}

    public record ResourceStat(
            String resource,
            long totalRequestsCount
    ) {}

    public record ResponseCodeStat(
            int code,
            long totalResponsesCount
    ) {}

    public record RequestsPerDateStat(
            String date,
            String weekday,
            long totalRequestsCount,
            double totalRequestsPercentage
    ) {}
}
