package it.dmi.data.entities.task;

public sealed interface QuartzTask permits Azione, Configurazione {
    Long getId();
    String getStrID();
    String getSqlScript();
    String getProgramma();
    String getClasse();
    String getLatchID();
}
