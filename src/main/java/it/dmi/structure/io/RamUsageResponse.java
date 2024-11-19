package it.dmi.structure.io;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(title = "Ram Usage", name = "Ram usage of the entire system",
        description = "In order: total memory, free memory, used memory all measured in MB (megabyte)")
public record RamUsageResponse(
        @Schema(description = "Total memory")
        long totalMemory,

        @Schema(description = "Free memory")
        long freeMemory,

        @Schema(description = "Used memory")
        long usedMemory
) {
}
