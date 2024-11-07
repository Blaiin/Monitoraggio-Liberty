package it.dmi.utils.jobs;

import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.quartz.ejb.Manager;
import it.dmi.quartz.listeners.AzioneJobListener;
import it.dmi.quartz.listeners.ConfigurazioneJobListener;
import it.dmi.structure.exceptions.MSDRuntimeException;
import it.dmi.structure.exceptions.impl.quartz.JobBuildingException;
import it.dmi.structure.internal.JobType;
import it.dmi.structure.internal.info.JobInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.KeyMatcher;

@Slf4j
public class JobUtils {

    public static JobType resolveJobType(@NotNull QuartzTask task) throws JobBuildingException {
        boolean sql = task.getSqlScript() != null;
        boolean program = task.getProgramma() != null;
        boolean classe = task.getClasse() != null;
        if (hasConflictingFields(sql, program, classe)) {
            String taskType = task.getClass().getSimpleName();
            String errorMessage = String.format("%s has multiple conflicting fields set, only one allowed.", taskType);
            log.error(errorMessage, task.getId());
            throw new JobBuildingException(errorMessage);
        }
        if (sql) return JobType.SQL;
        if (program) return JobType.PROGRAM;
        if (classe) return JobType.CLASS;
        return JobType.OTHER;
    }

    private static boolean hasConflictingFields(boolean sql, boolean program, boolean classe) {
        return (sql && program) || (sql && classe) || (program && classe);
    }

    @Contract(mutates = "param2")
    public static void addJobListener(@NotNull final Manager manager, @NotNull Scheduler scheduler,
                                      @NotNull final QuartzTask task, @NotNull JobInfo jobInfo) {
        try {
            JobListener listener = createJobListener(task, manager);
            scheduler.getListenerManager().addJobListener(listener, KeyMatcher.keyEquals(jobInfo.jobDetail().getKey()));
        } catch (SchedulerException e) {
            log.error("Error adding Jobs Listener for Task {}", task.getStrID());
            throw new MSDRuntimeException(e);
        }
    }

    private static JobListener createJobListener(QuartzTask task, Manager manager) {
        return switch (task) {
            case Azione a -> new AzioneJobListener(a, manager);
            case Configurazione c -> new ConfigurazioneJobListener(c, manager);
        };
    }
}
