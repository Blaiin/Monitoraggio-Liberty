package it.dmi.rest.io;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(title = "Ram Usage",
        description = "Total memory, free memory and used memory of the system running the application," +
                " all measured in MB (megabyte)")
public record RamUsageResponse(

        @Schema(description = "Total memory (MB)", example = "16129")
        long totalMemory,

        @Schema(description = "Free memory (MB)", example = "4368")
        long freeMemory,

        @Schema(description = "Used memory (MB)", example = "256")
        long usedMemory
) {
}
