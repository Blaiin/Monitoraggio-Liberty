package it.dmi.quartz.builders;

import it.dmi.structure.exceptions.impl.quartz.JobAlreadyDefinedException;
import it.dmi.structure.exceptions.impl.quartz.JobTypeException;
import it.dmi.structure.internal.info.JobInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.quartz.SchedulerException;

@Slf4j
public abstract class MSDJobBuilder {

    @Contract("_ -> new")
    protected static @NotNull JobInfo resolveJobBuildingException(@NotNull Throwable e) {
        switch (e) {
            case SchedulerException sE -> log.error("Error while trying to create job: {}", sE.getMessage(), sE);
            case JSQLParserException jsqlE -> log.error("Could not determine query functionality. {}",
                    jsqlE.getMessage(), jsqlE);
            case JobTypeException jtE -> log.error("Jobs type resolution found a problem: {}", jtE.getMessage(), jtE);
            case JobAlreadyDefinedException jadE -> {
                log.debug("Error trying to schedule job. {}", jadE.getMessage());
                //TODO remember to remove from error logging the whole exception so console doent get clogged up
                log.error("Error trying to schedule job. {}", jadE.getMessage(), jadE);
                return new JobInfo(null, null, true);
            }
            default -> log.error("Error: {}", e.getMessage(), e);
        }
        return new JobInfo(null, null);
    }
}
