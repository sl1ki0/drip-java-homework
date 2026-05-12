package drip.hw4.json;

import com.google.gson.Gson;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonBuilder {
    private final Gson gson = new Gson();

    public String serialize(Object source) {
        return gson.toJson(source);
    }

    public String buildDateRangeQuery(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> dateFilter = new LinkedHashMap<>();
        dateFilter.put("$gte", startDate + "T00:00:00.000Z");
        dateFilter.put("$lte", endDate + "T23:59:59.999Z");

        Map<String, Object> query = new LinkedHashMap<>();
        query.put("date_utc", dateFilter);

        return buildQueryBody(query);
    }

    public String buildSuccessQuery(boolean success) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("success", success);
        return buildQueryBody(query);
    }

    private String buildQueryBody(Map<String, Object> query) {
        Map<String, Object> sort = new LinkedHashMap<>();
        sort.put("flight_number", "asc");

        Map<String, Object> options = new LinkedHashMap<>();
        options.put("pagination", false);
        options.put("sort", sort);

        return serialize(new LaunchQueryBody(query, options));
    }

    private record LaunchQueryBody(Map<String, Object> query, Map<String, Object> options) {
    }
}
