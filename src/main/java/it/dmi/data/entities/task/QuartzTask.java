package it.dmi.data.entities.task;

public sealed interface QuartzTask permits Azione, Configurazione {
    Long getId();
    String getStringID();
    String getSqlScript();
    String getProgramma();
    String getClasse();
}
