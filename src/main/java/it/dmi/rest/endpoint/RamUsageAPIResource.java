package it.dmi.rest.endpoint;

import com.sun.management.OperatingSystemMXBean;
import it.dmi.rest.endpoint.apis.StatisticsAPI;
import it.dmi.rest.io.RamUsageResponse;
import it.dmi.utils.Utils;
import jakarta.enterprise.context.RequestScoped;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;

@RequestScoped
@Slf4j
public class RamUsageAPIResource implements StatisticsAPI {

    @Override
    public RamUsageResponse ramUsage() {
        return measureSys();
    }

    private @NotNull RamUsageResponse measureSys() {
        OperatingSystemMXBean osBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long total = Utils.Math.calculateMB(osBean.getTotalMemorySize());
        long free = Utils.Math.calculateMB(osBean.getFreeMemorySize());
        long used = total - free;
        return new RamUsageResponse(total, free, used);
    }
}
