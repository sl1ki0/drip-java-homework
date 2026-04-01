package drip.hw4.model;

import java.util.Collections;
import java.util.List;

public class LaunchQueryResponse {
    private List<Launch> docs;

    public List<Launch> getDocs() {
        return docs == null ? Collections.emptyList() : docs;
    }
}
