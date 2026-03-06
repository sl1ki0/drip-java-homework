package drip.hw3;

import drip.hw3.argumentParser.ParsedArguments;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogAnalyzerTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldAnalyzeFileAndRenderReports() throws IOException {
        Path logFile = tempDir.resolve("sample.log");
        Files.write(logFile, List.of(
                "127.0.0.1 - - [17/May/2015:08:05:32 +0000] \"GET /a HTTP/1.1\" 200 100 \"-\" \"agent\"",
                "127.0.0.1 - - [17/May/2015:09:05:32 +0000] \"GET /a HTTP/1.1\" 404 300 \"-\" \"agent\"",
                "127.0.0.1 - - [18/May/2015:10:05:32 +0000] \"GET /b HTTP/1.1\" 200 50 \"-\" \"agent\"",
                "127.0.0.1 - - [18/May/2015:11:05:32 +0000] \"GET /b HTTP/1.1\" 200 150 \"-\" \"agent\"",
                "broken line"
        ));

        ParsedArguments arguments = new ParsedArguments(
                List.of(logFile),
                "json",
                tempDir.resolve("report.json"),
                null,
                null
        );

        LogAnalysisResult result = new LogAnalyzer().analyze(arguments);

        assertEquals(4, result.totalRequestsCount());
        assertEquals(150.0, result.responseSizeInBytes().average(), 0.0001);
        assertEquals(300.0, result.responseSizeInBytes().max(), 0.0001);
        assertEquals(300.0, result.responseSizeInBytes().p95(), 0.0001);
        assertEquals("/a", result.resources().get(0).resource());
        assertEquals(2, result.resources().get(0).totalRequestsCount());
        assertEquals(200, result.responseCodes().get(0).code());
        assertEquals(3, result.responseCodes().get(0).totalResponsesCount());
        assertEquals(2, result.requestsPerDate().size());
        assertEquals(50.0, result.requestsPerDate().get(0).totalRequestsPercentage(), 0.0001);

        String json = ReportRenderer.render(result, arguments);
        assertTrue(json.contains("\"totalRequestsCount\": 4"));
        assertTrue(json.contains("\"resource\": \"/a\""));

        String markdown = ReportRenderer.render(
                result,
                new ParsedArguments(arguments.paths(), "markdown", tempDir.resolve("report.md"), null, null)
        );
        assertTrue(markdown.contains("#### Общая информация"));
        assertTrue(markdown.contains("| /a | 2 |"));
    }

    @Test
    void shouldApplyDateFilter() throws IOException {
        Path logFile = tempDir.resolve("sample.log");
        Files.write(logFile, List.of(
                "127.0.0.1 - - [17/May/2015:08:05:32 +0000] \"GET /a HTTP/1.1\" 200 100 \"-\" \"agent\"",
                "127.0.0.1 - - [18/May/2015:11:05:32 +0000] \"GET /b HTTP/1.1\" 200 150 \"-\" \"agent\""
        ));

        ParsedArguments arguments = new ParsedArguments(
                List.of(logFile),
                "json",
                tempDir.resolve("report.json"),
                "2015-05-18",
                null
        );

        LogAnalysisResult result = new LogAnalyzer().analyze(arguments);

        assertEquals(1, result.totalRequestsCount());
        assertEquals("/b", result.resources().get(0).resource());
    }
}
