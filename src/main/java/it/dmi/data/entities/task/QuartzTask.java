package it.dmi.data.entities.task;

public interface QuartzTask {
    Long getId();
    String getStringID();
    String getSqlScript();
}
