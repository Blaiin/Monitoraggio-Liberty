package it.sogei.structure.apimodels;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(description = "Model representing a query response")
public record QueryResponse(String status, String message, List<List<?>> results) {
}
