package it.dmi.rest.io;

import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Schema(description = "Schema representing a response from the system.")
public class QueryResponse {
    private final String code;
    private final String message;
    private final List<Map<String, List<?>>> results;

    public QueryResponse(String code, String message, List<Map<String, List<?>>> results) {
        this.code = code;
        this.message = message;
        this.results = results;
    }

    public QueryResponse(String code, String message) {
        this(code, message, createErrorMap(code, message));
    }

    private static @NotNull @Unmodifiable List<Map<String, List<?>>> createErrorMap(String code, String message) {
        if (Objects.equals(code, "500")) {
            Map<String, List<?>> messageMap = Collections.singletonMap("error", Collections.singletonList(message));
            return Collections.singletonList(messageMap);
        }
        return Collections.singletonList(Collections.emptyMap());
    }
}
