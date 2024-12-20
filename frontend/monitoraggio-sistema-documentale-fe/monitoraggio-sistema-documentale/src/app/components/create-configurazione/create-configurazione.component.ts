import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Configurazione } from 'src/app/entities/Configurazione';
import { ConfigurazioneService } from './configurazione.service';

@Component({
  selector: 'app-create-configurazione',
  templateUrl: './create-configurazione.component.html',
  styleUrls: ['./create-configurazione.component.css'],
})
export class CreateConfigurazioneComponent implements OnInit {
  configurazioneForm: FormGroup;
  elencoMesiCron: any[] = [];
  ElencoGiorniDellaSettimanaCron = [];
  filteredCronExpressionValue: string[] = [];

  constructor(
    private fb: FormBuilder,
    private configurazioneService: ConfigurazioneService
  ) {
    this.configurazioneForm = this.fb.group({
      tipoControllo: this.fb.group({
        descrizione: [null, Validators.required],
      }),
      controllo: this.fb.group({
        descrizione: [null, Validators.required],

        ambito: [null, Validators.required],
        ordineControllo: [null, Validators.required],
      }),

      fonteDati: this.fb.group({
        descrizione: [null, Validators.required],
        nomeDriver: [null, Validators.required],
        nomeClasse: [null, Validators.required],
        url: [null, [Validators.required, Validators.pattern('https?://.+')]],
        JNDIName: [null, Validators.required],
      }),
      utenteFonteDati: this.fb.group({
        descrizione: [null], //opzionale
        username: [null, Validators.required],
        password: [null, Validators.required],
      }),
      configurazione: this.fb.group({
        nome: [null, Validators.required],
        sqlScript: [null, Validators.required],
        programma: [null, Validators.required],
        classe: [null, Validators.required],
        schedulazione: this.fb.group({
          secondi: [
            null,
            [
              Validators.required,
              Validators.pattern(
                /^\s*(\*|([0-5]?\d)([,-][0-5]?\d)*([\/]\d+)?|(\d+))\s*$/
              ),
            ],
          ],
          minuti: [
            null,
            [
              Validators.required,
              Validators.pattern(
                /^\s*(\*|([0-5]?\d)([,-][0-5]?\d)*([\/]\d+)?|(\d+))\s*$/
              ),
            ],
          ],
          ore: [
            null,
            [
              Validators.required,
              Validators.pattern(
                /^\s*(\*|([01]?\d|2[0-3])([,-][01]?\d|2[0-3])*([\/]\d+)?|(\d+))\s*$/
              ),
            ],
          ],
          giornoDelMese: [
            null,
            [
              Validators.required,
              Validators.pattern(
                /^\s*(\*|\?|[1-9]|[12]\d|3[01])([,-][1-9]|[12]\d|3[01])*([\/]\d+)?(L|W)?\s*$/
              ),
            ],
          ],
          mese: [
            null,
            [
              Validators.required,
              Validators.pattern(
                /^\s*(\*|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|[1-9]|1[0-2])([,-](JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|[1-9]|1[0-2]))*([\/]\d+)?\s*$/
              ),
            ],
          ],
          giornoDellAnno: [
            null,
            [
              Validators.required,
              Validators.pattern(
                /^\s*(\*|\?|[1-7]|SUN|MON|TUE|WED|THU|FRI|SAT)([,-][1-7]|SUN|MON|TUE|WED|THU|FRI|SAT)*([\/]\d+)?(L|#\d+)?\s*$/
              ),
            ],
          ],
          anno: [
            null,
            Validators.pattern(
              /^\s*(\*|[0-9]{4})([,-][0-9]{4})*([\/]\d+)?\s*$/
            ),
          ],
        }),

        ordineConfigurazione: [
          null,
          Validators.required,
          // Validators.pattern(/^[0-9]*[1-9][0-9]*$/)
        ],
      }),
      soglie: this.fb.array([this.createSoglia()]),
    });

    this.generateCronOptions();
  }

  generateCronOptions(): void {
    const mesi = [
      'JAN',
      'FEB',
      'MAR',
      'APR',
      'MAY',
      'JUN',
      'JUL',
      'AUG',
      'SEP',
      'OCT',
      'NOV',
      'DEC',
    ];

    // Wildcard
    this.elencoMesiCron.push(',', '-', '*', '/');

    // Aggiungi i mesi abbreviati
    this.elencoMesiCron.push(...mesi);

    // Genera combinazioni del tipo n/m (dove n e m vanno da 1 a 12)
    for (let n = 1; n <= 12; n++) {
      for (let m = 1; m <= 12; m++) {
        this.elencoMesiCron.push(`${n}/${m}`);
      }
    }
  }

  ngOnInit(): void {}

  createSoglia() {
    return this.fb.group({
      sogliaInferiore: [null],
      sogliaSuperiore: [null],
      valore: [null],
      operatore: [null],
      azioni: this.fb.array([]),
    });
  }

  createAzione() {
    return this.fb.group({
      tipoAzione: [null, Validators.required],
      sqlScript: [{ value: null, disabled: true }, Validators.required],
      programma: [{ value: null, disabled: true }, Validators.required],
      classe: [{ value: null, disabled: true }, Validators.required],
      destinatario: [
        { value: null, disabled: true },
        [
          Validators.required,
          Validators.pattern(
            /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
          ),
        ],
      ],
      testoMail: [{ value: null, disabled: true }, Validators.required],
    });
  }

  get soglie(): FormArray {
    return this.configurazioneForm.get('soglie') as FormArray;
  }

  getAzioni(sogliaIndex: number): FormArray {
    return this.soglie.at(sogliaIndex).get('azioni') as FormArray;
  }

  addSoglia() {
    const sogliaGroup = this.createSoglia();
    this.soglie.push(sogliaGroup);
  }

  removeSoglia(index: number) {
    this.soglie.removeAt(index);
  }

  addAzione(sogliaIndex: number) {
    const azioneGroup = this.createAzione();
    this.getAzioni(sogliaIndex).push(azioneGroup);
  }

  removeAzione(sogliaIndex: number, azioneIndex: number) {
    this.getAzioni(sogliaIndex).removeAt(azioneIndex);
  }

  get schedulazioneControl() {
    return this.configurazioneForm.get('configurazione.schedulazione');
  }

  onFileSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;

    if (fileInput.files && fileInput.files.length > 0) {
      const file = fileInput.files[0];

      // Lettura del contenuto del file
      const reader = new FileReader();

      reader.onload = () => {
        const fileContent = reader.result as string; // Contenuto del file (base64 o testuale)

        // Oggetto contenente nome e contenuto del file
        const fileData = {
          name: file.name,
          content: fileContent,
        };

        // Aggiorna il formControl con l'oggetto completo
        this.configurazioneForm
          .get('configurazione.sqlScript')
          ?.setValue(fileData);
        console.log('File caricato:', fileData);
      };

      // Legge il file come testo o base64
      reader.readAsDataURL(file); // Altrimenti reader.readAsText(file) per testo puro
    }
  }

  onFileSelectedForAzione(
    event: Event,
    sogliaIndex: number,
    azioneIndex: number
  ): void {
    const fileInput = event.target as HTMLInputElement;

    if (fileInput.files && fileInput.files.length > 0) {
      const file = fileInput.files[0];
      const reader = new FileReader();

      // Quando il file è stato caricato, lo processiamo
      reader.onload = () => {
        const content = reader.result as string; // Contenuto del file

        // Otteniamo l'array delle azioni per la soglia specifica
        const azioniArray = this.getAzioni(sogliaIndex);

        // Aggiorniamo il controllo 'sqlScript' per la specifica azione
        azioniArray.at(azioneIndex).get('sqlScript')?.setValue({
          name: file.name, // Nome del file
          content: content, // Contenuto del file
        });

        console.log("File caricato per l'azione:", file.name);
      };

      // Legge il file come testo
      reader.readAsText(file);
    }
  }

  onSubmit(): void {
    if (this.configurazioneForm.valid) {
      const formValues = this.configurazioneForm.value;

      // Trasformiamo il campo sqlScript in un oggetto con nome e contenuto
      const sqlScript = formValues.configurazione?.sqlScript;

      const body: Configurazione = {
        content: {
          ...formValues, // Copiamo gli altri campi
          configurazione: {
            ...formValues.configurazione, // Copiamo i campi esistenti
            sqlScript: sqlScript
              ? {
                  name: sqlScript.name,
                  content: sqlScript.content, // Assicurati che il contenuto sia già in Base64 o testo
                }
              : null,
          },
        },
      };

      console.log('Dati inviati:', JSON.stringify(body));

      this.configurazioneService.aggiungiConfigurazione(body).subscribe(
        (response) => {
          console.log('Configurazione aggiunta con successo:', response);
        },
        (error) => {
          console.error(
            "Errore durante l'aggiunta della configurazione:",
            error
          );
        }
      );
    } else {
      console.log('Form non valido, visualizzare messaggi di errore');
      this.markAllAsTouched();
    }
  }

  // Metodo per segnare tutti i campi come "toccati" per mostrare gli errori
  markAllAsTouched() {
    Object.values(this.configurazioneForm.controls).forEach((control) => {
      control.markAsTouched();
    });
  }

  onTipoControlloChange(event: Event): void {
    const selectedValue = (event.target as HTMLSelectElement).value;

    const programmaControl = this.configurazioneForm.get(
      'configurazione.programma'
    );
    const classeControl = this.configurazioneForm.get('configurazione.classe');
    const sqlScriptControl = this.configurazioneForm.get(
      'configurazione.sqlScript'
    );

    if (selectedValue === '1') {
      // Disabilita e rimuovi i validatori per valore 1
      programmaControl?.disable();
      classeControl?.disable();
      programmaControl?.clearValidators();
      classeControl?.clearValidators();
    } else if (selectedValue === '4') {
      // Disabilita e rimuovi i validatori per valore 4
      programmaControl?.disable();
      classeControl?.disable();
      sqlScriptControl?.disable();
      programmaControl?.clearValidators();
      classeControl?.clearValidators();
      sqlScriptControl?.clearValidators();
    } else {
      // Abilita e imposta come obbligatori per valori diversi da 1 e 4
      programmaControl?.enable();
      classeControl?.enable();
      sqlScriptControl?.enable();
      programmaControl?.setValidators(Validators.required);
      classeControl?.setValidators(Validators.required);
    }

    // Aggiorna lo stato dei control
    programmaControl?.updateValueAndValidity();
    classeControl?.updateValueAndValidity();
    sqlScriptControl?.updateValueAndValidity();
  }

  onSogliaChange(index: number): void {
    const sogliaGroup = (this.configurazioneForm.get('soglie') as FormArray).at(
      index
    ) as FormGroup;

    const sogliaInferioreControl = sogliaGroup.get('sogliaInferiore');
    const sogliaSuperioreControl = sogliaGroup.get('sogliaSuperiore');
    const valoreControl = sogliaGroup.get('valore');
    const operatoreControl = sogliaGroup.get('operatore');

    if (valoreControl?.value != '') {
      sogliaInferioreControl?.disable();
      sogliaSuperioreControl?.disable();
      operatoreControl?.setValidators(Validators.required);
      operatoreControl?.updateValueAndValidity();
    } else {
      sogliaInferioreControl?.enable();
      sogliaSuperioreControl?.enable();
      operatoreControl?.clearValidators();
      operatoreControl?.clearAsyncValidators();
      operatoreControl?.updateValueAndValidity();
    }
    this.configurazioneForm.updateValueAndValidity();
  }

  onChangeTipoAzione(index: number, subIndex: number): void {
    const azioneGroup = (this.configurazioneForm.get('soglie') as FormArray)
      .at(index)
      .get('azioni') as FormArray;
    const currentAzioneGroup = azioneGroup.at(subIndex) as FormGroup;

    const tipoAzioneControl = currentAzioneGroup.get('tipoAzione');
    const sqlScriptControl = currentAzioneGroup.get('sqlScript');
    const programmaControl = currentAzioneGroup.get('programma');
    const classeControl = currentAzioneGroup.get('classe');
    const destinatarioControl = currentAzioneGroup.get('destinatario');
    const testoMailControl = currentAzioneGroup.get('testoMail');

    switch (tipoAzioneControl?.value) {
      case '1': // invio mail
        //campi da abilitare
        destinatarioControl?.enable();
        destinatarioControl?.setValidators([
          Validators.required,
          Validators.pattern(
            /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
          ),
        ]);
        testoMailControl?.enable();
        testoMailControl?.setValidators(Validators.required);
        //campi da disabilitare
        sqlScriptControl?.disable();
        programmaControl?.disable();
        classeControl?.disable();
        sqlScriptControl?.clearValidators();
        programmaControl?.clearValidators();
        classeControl?.clearValidators();
        break;
      case '2': // Esecuzione script SQL
        sqlScriptControl?.enable();
        sqlScriptControl?.setValidators(Validators.required);
        programmaControl?.disable();
        classeControl?.disable();
        programmaControl?.clearValidators();
        classeControl?.clearValidators();
        destinatarioControl?.disable();
        destinatarioControl?.clearValidators();
        testoMailControl?.disable();
        testoMailControl?.clearValidators();
        break;

      case '3': // Esecuzione di programma/cmd/sh
        programmaControl?.enable();
        programmaControl?.setValidators(Validators.required);
        sqlScriptControl?.disable();
        classeControl?.disable();
        sqlScriptControl?.clearValidators();
        classeControl?.clearValidators();
        destinatarioControl?.disable();
        destinatarioControl?.clearValidators();
        testoMailControl?.disable();
        testoMailControl?.clearValidators();
        break;

      case '4': // Esecuzione di altro controllo
        classeControl?.enable();
        classeControl?.setValidators(Validators.required);
        sqlScriptControl?.disable();
        programmaControl?.disable();
        sqlScriptControl?.clearValidators();
        programmaControl?.clearValidators();
        destinatarioControl?.disable();
        destinatarioControl?.clearValidators();
        testoMailControl?.disable();
        testoMailControl?.clearValidators();
        break;

      default: // Reset in caso di selezione vuota o altro
        sqlScriptControl?.disable();
        programmaControl?.disable();
        classeControl?.disable();
        sqlScriptControl?.clearValidators();
        destinatarioControl?.disable();
        destinatarioControl?.clearValidators();
        programmaControl?.clearValidators();
        classeControl?.clearValidators();
        testoMailControl?.clearValidators();
        testoMailControl?.clearValidators();
        break;
    }

    // Aggiorna lo stato dei campi
    sqlScriptControl?.updateValueAndValidity();
    programmaControl?.updateValueAndValidity();
    classeControl?.updateValueAndValidity();
  }

  get filteredCronExpression(): string[] {
    const schedulazione = this.configurazioneForm.get(
      'configurazione.schedulazione'
    )?.value;

    if (!schedulazione) {
      return [];
    }

    const keys = [
      'secondi',
      'minuti',
      'ore',
      'giornoDelMese',
      'mese',
      'giornoDellAnno',
      'anno',
    ];

    const filteredValues = keys
      .map((key) => schedulazione[key])
      .filter((value) => value !== null && value !== '');

    // Salva il valore filtrato nella proprietà
    this.filteredCronExpressionValue = filteredValues;

    return filteredValues;
  }
}
