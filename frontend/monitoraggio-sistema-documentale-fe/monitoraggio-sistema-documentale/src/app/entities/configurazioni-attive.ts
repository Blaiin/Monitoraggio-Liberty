export interface ConfigurazioneAttiva {
  id: number;

  nome: string; //'Schedulazione cmd.exe'

  tipoControllo: {
    tipoControlloID: number;

    descrizione: string; //tipo controllo 1
  };

  ordineConfigurazione: 1;
}
