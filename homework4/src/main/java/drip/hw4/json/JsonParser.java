package drip.hw4.json;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import drip.hw4.model.Launch;
import drip.hw4.model.LaunchQueryResponse;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class JsonParser {
    private static final Type LAUNCH_LIST_TYPE = new TypeToken<List<Launch>>() {
    }.getType();

    private final Gson gson = new Gson();

    public List<Launch> parseLaunchList(String json) throws JsonParsingException {
        try {
            List<Launch> launches = gson.fromJson(json, LAUNCH_LIST_TYPE);
            return launches == null ? Collections.emptyList() : launches;
        } catch (JsonSyntaxException e) {
            throw new JsonParsingException("Не удалось разобрать список запусков из JSON.", e);
        }
    }

    public Launch parseLaunch(String json) throws JsonParsingException {
        try {
            Launch launch = gson.fromJson(json, Launch.class);
            if (launch == null) {
                throw new JsonParsingException("Сервер вернул пустой объект запуска.", null);
            }
            return launch;
        } catch (JsonSyntaxException e) {
            throw new JsonParsingException("Не удалось разобрать запуск из JSON.", e);
        }
    }

    public LaunchQueryResponse parseLaunchQueryResponse(String json) throws JsonParsingException {
        try {
            LaunchQueryResponse response = gson.fromJson(json, LaunchQueryResponse.class);
            if (response == null) {
                throw new JsonParsingException("Сервер вернул пустой результат поиска запусков.", null);
            }
            return response;
        } catch (JsonSyntaxException e) {
            throw new JsonParsingException("Не удалось разобрать ответ поиска запусков из JSON.", e);
        }
    }
}
