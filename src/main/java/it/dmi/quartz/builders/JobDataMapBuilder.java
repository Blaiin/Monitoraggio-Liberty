package it.dmi.quartz.builders;

import it.dmi.data.entities.FonteDati;
import it.dmi.data.entities.SicurezzaFonteDati;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.structure.exceptions.impl.quartz.JobBuildingException;
import it.dmi.structure.internal.JobType;
import it.dmi.utils.jobs.JobUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobDataMap;

import static it.dmi.utils.constants.NamingConstants.*;

@Slf4j
public class JobDataMapBuilder {

    public static @NotNull JobDataMap buildJobDataMap(@NotNull QuartzTask task) throws JobBuildingException {
        JobType jobType = JobUtils.resolveJobType(task);
        return switch (task) {
            case Azione a -> createJobDataMap(a, jobType, new JobDataMap(),
                    a.getFonteDati(), a.getUtenteFonteDati());
            case Configurazione c -> createJobDataMap(c, jobType, new JobDataMap(),
                    c.getFonteDati(), c.getUtenteFonteDati());
        };
    }

    @Contract("_, _, _, _, _ -> param3")
    private static @NotNull JobDataMap createJobDataMap(@NotNull QuartzTask task,
                                                        @NotNull JobType jobType,
                                                        @NotNull JobDataMap map,
                                                        @NotNull FonteDati fd,
                                                        @NotNull SicurezzaFonteDati sfd) {
        final var taskID = task.getStrID();
        map.put(ID, taskID);
        map.put(TASK + taskID, task);
        map.put(JOB_TYPE + taskID, jobType.getJobType());
        switch (task) {
            case Azione azione -> {
                map.put(CLASS + taskID, azione.getClasse());
                map.put(PROGRAMMA + taskID, azione.getProgramma());
                map.put(SQL_SCRIPT + taskID, azione.getSqlScript());
            }
            case Configurazione config -> {
                map.put(NOME, config.getNome());
                map.put(SQL_SCRIPT + taskID, config.getSqlScript());
                map.put(CLASS + taskID, config.getClasse());
                map.put(PROGRAMMA + taskID, config.getProgramma());
                map.put(THRESHOLDS + taskID, config.getSoglie());
            }
        }
        map.put(DRIVER_NAME + taskID, fd.getNomeDriver());
        map.put(URL + taskID, fd.getUrl());
        map.put(USERNAME + taskID, sfd.getUserID());
        map.put(PASSWORD + taskID, sfd.getPassword());
        return map;
    }
}
