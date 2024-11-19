export interface Configurazione {
  content: {
    tipoControllo: {
      descrizione: string;
    };
    controllo: {
      descrizione: string;
      tipoControlloID: number | null;
      ambitoID: number | null;
      ordineControllo: number | null;
    };
    ambito: {
      nome: string;
      destinazione: string;
    };
    fonteDati: {
      descrizione: string;
      nomeDriver: string;
      nomeClasse: string;
      url: string;
      JNDIName: string;
    };
    utenteFonteDati: {
      descrizione: string;
      username: string;
      password: string;
    };
    configurazione: {
      nome: string;
      sqlScript: string;
      programma: string;
      classe: string;
      schedulazione: string;
      ordineConfigurazione: number | null;
    };
    soglie: Array<{
      sogliaInferiore: string;
      sogliaSuperiore: string;
      valore: string;
      operatore: string;
    }>;
  };
}
