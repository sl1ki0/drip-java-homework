package drip.hw3;

import drip.hw3.argumentParser.ParsedArguments;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RealLogsTest {
    @Test
    void shouldAnalyzeReal2kLog() throws IOException {
        Path logFile = Path.of("src", "elastic_nginx_example_2k.log").toAbsolutePath().normalize();

        LogAnalysisResult result = new LogAnalyzer().analyze(new ParsedArguments(
                List.of(logFile),
                "json",
                Path.of("build", "reports", "report.json"),
                null,
                null
        ));

        assertEquals(2000, result.totalRequestsCount());
        assertEquals(66178.32, result.responseSizeInBytes().average(), 0.0001);
        assertEquals(85619205.0, result.responseSizeInBytes().max(), 0.0001);
        assertEquals(341.0, result.responseSizeInBytes().p95(), 0.0001);
        assertEquals("/downloads/product_2", result.resources().getFirst().resource());
        assertEquals(1209, result.resources().getFirst().totalRequestsCount());
        assertEquals(404, result.responseCodes().getFirst().code());
        assertEquals(1376, result.responseCodes().getFirst().totalResponsesCount());
        assertEquals("2015-05-17", result.requestsPerDate().getFirst().date());
        assertEquals(98.3, result.requestsPerDate().getFirst().totalRequestsPercentage(), 0.0001);
    }

    @Test
    void shouldAnalyzeLargeRealLog() throws IOException {
        Path logFile = Path.of("src", "elastic_nginx_example_full.log").toAbsolutePath().normalize();

        LogAnalysisResult result = new LogAnalyzer().analyze(new ParsedArguments(
                List.of(logFile),
                "json",
                Path.of("build", "reports", "report.json"),
                null,
                null
        ));

        assertEquals(51462, result.totalRequestsCount());
        assertEquals(659509.51, result.responseSizeInBytes().average(), 0.0001);
        assertEquals(86377168.0, result.responseSizeInBytes().max(), 0.0001);
        assertEquals(1768.0, result.responseSizeInBytes().p95(), 0.0001);
        assertEquals("/downloads/product_1", result.resources().getFirst().resource());
        assertEquals(30285, result.resources().getFirst().totalRequestsCount());
        assertEquals(404, result.responseCodes().getFirst().code());
        assertEquals(33876, result.responseCodes().getFirst().totalResponsesCount());
    }
}
