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
      sqlScript: any;
      programma: string;
      classe: string;
      schedulazione: {
        secondi: string;
        minuti: string;
        ore: string;
        giornoDelMese: string;
        mese: string;
        giornoDellAnno: string;
        anno: string;
      };
      ordineConfigurazione: number | null;
    };
    soglie: Array<{
      sogliaInferiore: string;
      sogliaSuperiore: string;
      valore: string;
      operatore: string;
      azioni: Array<{
        tipoAzione: string;
        destinatario: string;
        testoMail: string;
        sqlScript: string;
        programma: string;
        classe: string;
      }>;
    }>;
  };
}


// ambito: this.fb.group({
//   nome: ['', Validators.required],
//   destinazione: ['', Validators.required],
// }),

// tipoControlloID: [
// '',
// Validators.required,
// Validators.pattern(/^[0-9]*[1-9][0-9]*$/)
// ],

// schedulazione: [
//   '',
//   [
//     Validators.required,
//     Validators.pattern(
//       /^(?:\d+|\*|\?)(\/\d+)?(\s+(?:\d+|\*|\?)(\/\d+)?){4}(\s+(MON|TUE|WED|THU|FRI|SAT|SUN)(-(MON|TUE|WED|THU|FRI|SAT|SUN))?)?(\s+(\*|\?|(\d+))(\s+(\*|\?|(\d+)))?)*$/
//     ),
//   ],
// ],
