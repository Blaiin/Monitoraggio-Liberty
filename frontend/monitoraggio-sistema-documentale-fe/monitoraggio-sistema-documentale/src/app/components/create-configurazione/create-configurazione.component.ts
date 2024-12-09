import { Component, OnInit, signal } from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { Configurazione } from 'src/app/entities/Configurazione';
import { ConfigurazioneService } from './configurazione.service';
import { MatExpansionModule } from '@angular/material/expansion';

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
        descrizione: ['', Validators.required],
      }),
      controllo: this.fb.group({
        descrizione: ['', Validators.required],

        ambito: ['', Validators.required],
        ordineControllo: ['', Validators.required],
      }),

      fonteDati: this.fb.group({
        descrizione: ['', Validators.required],
        nomeDriver: ['', Validators.required],
        nomeClasse: ['', Validators.required],
        url: ['', [Validators.required, Validators.pattern('https?://.+')]],
        JNDIName: ['', Validators.required],
      }),
      utenteFonteDati: this.fb.group({
        descrizione: [''], //opzionale
        username: ['', Validators.required],
        password: ['', Validators.required],
      }),
      configurazione: this.fb.group({
        nome: ['', Validators.required],
        sqlScript: ['', Validators.required],
        programma: ['', Validators.required],
        classe: ['', Validators.required],
        schedulazione: this.fb.group({
          secondi: [
            '',
            [
              Validators.required,
              Validators.pattern(
                /^\s*(\*|([0-5]?\d)([,-][0-5]?\d)*([\/]\d+)?|(\d+))\s*$/
              ),
            ],
          ],
          minuti: [
            '',
            [
              Validators.required,
              Validators.pattern(
                /^\s*(\*|([0-5]?\d)([,-][0-5]?\d)*([\/]\d+)?|(\d+))\s*$/
              ),
            ],
          ],
          ore: [
            '',
            [
              Validators.required,
              Validators.pattern(
                /^\s*(\*|([01]?\d|2[0-3])([,-][01]?\d|2[0-3])*([\/]\d+)?|(\d+))\s*$/
              ),
            ],
          ],
          giornoDelMese: [
            '',
            [
              Validators.required,
              Validators.pattern(
                /^\s*(\*|\?|[1-9]|[12]\d|3[01])([,-][1-9]|[12]\d|3[01])*([\/]\d+)?(L|W)?\s*$/
              ),
            ],
          ],
          mese: [
            '',
            [
              Validators.required,
              Validators.pattern(
                /^\s*(\*|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|[1-9]|1[0-2])([,-](JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|[1-9]|1[0-2]))*([\/]\d+)?\s*$/
              ),
            ],
          ],
          giornoDellAnno: [
            '',
            [
              Validators.required,
              Validators.pattern(
                /^\s*(\*|\?|[1-7]|SUN|MON|TUE|WED|THU|FRI|SAT)([,-][1-7]|SUN|MON|TUE|WED|THU|FRI|SAT)*([\/]\d+)?(L|#\d+)?\s*$/
              ),
            ],
          ],
          anno: [
            '',
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
      sogliaInferiore: ['', Validators.required],
      sogliaSuperiore: ['', Validators.required],
      valore: ['', Validators.required],
      operatore: ['', Validators.required],
      azioni: this.fb.array([]),
    });
  }

  createAzione() {
    return this.fb.group({
      tipoAzione: ['', Validators.required],
      sqlScript: [{ value: '', disabled: true }, Validators.required],
      programma: [{ value: '', disabled: true }, Validators.required],
      classe: [{ value: '', disabled: true }, Validators.required],
      destinatario: [
        { value: '', disabled: true },
        [
          Validators.required,
          Validators.pattern(
            /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
          ),
        ],
      ],
      testoMail: [{ value: '', disabled: true }, Validators.required],
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

  // Metodo di invio del form
  onSubmit() {
    if (this.configurazioneForm.valid) {
      const formData: Configurazione['content'] = this.configurazioneForm.value;
      const body: Configurazione = { content: formData };

      console.log('Form valid, data ready to send:', JSON.stringify(body));

      this.configurazioneService.aggiungiConfigurazione(body).subscribe(
        (response: any) => {
          console.log('Configurazione aggiunta con successo:', response);
        },
        (error: any) => {
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

    if (selectedValue === '1') {
      programmaControl?.disable();
      classeControl?.disable();
      programmaControl?.clearValidators();
      classeControl?.clearValidators();
    } else {
      programmaControl?.enable();
      classeControl?.enable();
      programmaControl?.setValidators(Validators.required);
      classeControl?.setValidators(Validators.required);
    }

    programmaControl?.updateValueAndValidity();
    classeControl?.updateValueAndValidity();
  }

  onSogliaChange(index: number): void {
    const sogliaGroup = (this.configurazioneForm.get('soglie') as FormArray).at(
      index
    ) as FormGroup;

    const sogliaInferioreControl = sogliaGroup.get('sogliaInferiore');
    const sogliaSuperioreControl = sogliaGroup.get('sogliaSuperiore');
    const valoreControl = sogliaGroup.get('valore');
    const operatoreControl = sogliaGroup.get('operatore');

    const sogliaInferioreValorizzata = !!sogliaInferioreControl?.value;
    const sogliaSuperioreValorizzata = !!sogliaSuperioreControl?.value;
    const valoreValorizzato = !!valoreControl?.value;
    const operatoreValorizzato = !!operatoreControl?.value;

    // Reset iniziale per evitare conflitti
    sogliaInferioreControl?.clearValidators();
    sogliaSuperioreControl?.clearValidators();
    valoreControl?.clearValidators();
    operatoreControl?.clearValidators();

    valoreControl?.enable();
    operatoreControl?.enable();

    // Caso 1: Soglia Inferiore valorizzata
    if (sogliaInferioreValorizzata) {
      sogliaSuperioreControl?.clearValidators(); // Non obbligatoria
      valoreControl?.reset();
      valoreControl?.disable(); // Disabilitata
      operatoreControl?.reset();
      operatoreControl?.disable(); // Disabilitata
    }
    // Caso 2: Soglia Superiore valorizzata
    else if (sogliaSuperioreValorizzata) {
      sogliaInferioreControl?.clearValidators(); // Non obbligatoria
      valoreControl?.reset();
      valoreControl?.disable(); // Disabilitata
      operatoreControl?.reset();
      operatoreControl?.disable(); // Disabilitata
    }
    // Caso 3: Valore valorizzato
    else if (valoreValorizzato) {
      sogliaInferioreControl?.clearValidators(); // Non obbligatoria
      sogliaSuperioreControl?.clearValidators(); // Non obbligatoria
      operatoreControl?.setValidators(Validators.required); // Obbligatorio
    }
    // Caso 4: Operatore valorizzato
    else if (operatoreValorizzato) {
      sogliaInferioreControl?.clearValidators(); // Non obbligatoria
      sogliaSuperioreControl?.clearValidators(); // Non obbligatoria
      valoreControl?.setValidators(Validators.required); // Obbligatorio
    }
    // Caso di default: Tutti obbligatori
    else {
      sogliaInferioreControl?.setValidators(Validators.required);
      sogliaSuperioreControl?.setValidators(Validators.required);
      valoreControl?.setValidators(Validators.required);
      operatoreControl?.setValidators(Validators.required);
    }

    // Aggiorna la validità dei campi
    sogliaInferioreControl?.updateValueAndValidity();
    sogliaSuperioreControl?.updateValueAndValidity();
    valoreControl?.updateValueAndValidity();
    operatoreControl?.updateValueAndValidity();
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
