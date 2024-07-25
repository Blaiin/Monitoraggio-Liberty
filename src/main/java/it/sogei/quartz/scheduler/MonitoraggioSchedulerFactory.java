package it.sogei.quartz.scheduler;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

import java.util.Collection;

public class MonitoraggioSchedulerFactory implements SchedulerFactory {
    @Override
    public Scheduler getScheduler () throws SchedulerException {
        return null;
    }

    @Override
    public Scheduler getScheduler (String s) throws SchedulerException {
        return null;
    }

    @Override
    public Collection<Scheduler> getAllSchedulers () throws SchedulerException {
        return null;
    }
}
