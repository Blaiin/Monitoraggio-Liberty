package it.sogei.structure.apimodels;

import jakarta.annotation.Nullable;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.Valid;

@Schema(description = "Model representing a query request")
public record QueryRequest(
        @Schema(description = "The query string", example = "SELECT * FROM test")
        @Valid String query,
        @Schema(description = "The type of the query", example = "Select *")
        @Valid String queryType,
        @Schema(description = "The name of the query", example = "Config Query")
        @Nullable String queryName,
        @Schema(description = "A brief description of the query", example = "This query fetches all names")
        @Nullable String queryDescription,
        @Schema(description = "Threshold to activate actions", example = "10")
        @Valid int threshold,
        @Schema(description = "Expected result format of the query", example = "JSON")
        @Nullable String queryResult) {}
