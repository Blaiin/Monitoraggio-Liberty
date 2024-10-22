package it.dmi.quartz.scheduler;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;

import java.util.Collection;

public class MonitoraggioSchedulerFactory implements SchedulerFactory {
    @Override
    public Scheduler getScheduler () {
        return null;
    }

    @Override
    public Scheduler getScheduler (String s) {
        return null;
    }

    @Override
    public Collection<Scheduler> getAllSchedulers () {
        return null;
    }
}
