package it.dmi.quartz.builders;

import it.dmi.structure.exceptions.impl.quartz.JobTypeException;
import it.dmi.structure.internal.info.JobInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.quartz.SchedulerException;

@Slf4j
public abstract class MSDJobBuilder {

    static JobInfo resolveJobBuildingException(Throwable e) {
        switch (e) {
            case SchedulerException sE -> log.error("Error while trying to build job: {}", sE.getMessage());
            case JSQLParserException jsqlE -> log.error("Could not determine query functionality. {}", jsqlE.getMessage());
            case JobTypeException jtE -> log.error("Job type resolution found a problem: {}", jtE.getMessage());
            default -> log.error("Error: {}", e.getMessage());
        }
        return new JobInfo(null, null);
    }
}
