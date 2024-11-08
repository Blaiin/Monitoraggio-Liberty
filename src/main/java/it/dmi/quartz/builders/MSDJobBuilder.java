package it.dmi.quartz.builders;

import it.dmi.structure.exceptions.impl.quartz.JobAlreadyDefinedException;
import it.dmi.structure.exceptions.impl.quartz.JobTypeException;
import it.dmi.structure.internal.info.JobInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.quartz.SchedulerException;

@Slf4j
public abstract class MSDJobBuilder {

    protected static JobInfo resolveJobBuildingException(Throwable e) {
        switch (e) {
            case SchedulerException sE -> log.error("Error while trying to build job: {}", sE.getMessage(), sE);
            case JSQLParserException jsqlE -> log.error("Could not determine query functionality. {}",
                    jsqlE.getMessage(), jsqlE);
            case JobTypeException jtE -> log.error("Jobs type resolution found a problem: {}", jtE.getMessage(), jtE);
            case JobAlreadyDefinedException jadE -> {
                log.debug("Error trying to schedule job. {}", jadE.getMessage(), jadE);
                log.error("Error trying to schedule job. {}", jadE.getMessage());
                return new JobInfo(null, null, true);
            }
            default -> log.error("Error: {}", e.getMessage(), e);
        }
        return new JobInfo(null, null);
    }
}
