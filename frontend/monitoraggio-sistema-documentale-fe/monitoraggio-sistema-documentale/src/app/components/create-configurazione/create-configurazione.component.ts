import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { Configurazione } from 'src/app/entities/Configurazione';
import { ConfigurazioneService } from './configurazione.service';

@Component({
  selector: 'app-create-configurazione',
  templateUrl: './create-configurazione.component.html',
  styleUrls: ['./create-configurazione.component.css'],
})
export class CreateConfigurazioneComponent implements OnInit {
  configurazioneForm: FormGroup;

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
        tipoControlloID: [
          '',
          Validators.required,
          // Validators.pattern(/^[0-9]*[1-9][0-9]*$/)
        ],
        ambitoID: [
          '',
          Validators.required,
          // Validators.pattern(/^[0-9]*[1-9][0-9]*$/)
        ],
        ordineControllo: [
          '',
          Validators.required,
          // Validators.pattern(/^[0-9]*[1-9][0-9]*$/)
        ],
      }),
      ambito: this.fb.group({
        nome: ['', Validators.required],
        destinazione: ['', Validators.required],
      }),
      fonteDati: this.fb.group({
        descrizione: ['', Validators.required],
        nomeDriver: ['', Validators.required],
        nomeClasse: ['', Validators.required],
        url: ['', [Validators.required, Validators.pattern('https?://.+')]],
        JNDIName: ['', Validators.required],
      }),
      utenteFonteDati: this.fb.group({
        descrizione: ['', Validators.required],
        username: ['', Validators.required],
        password: ['', Validators.required],
      }),
      configurazione: this.fb.group({
        nome: ['', Validators.required],
        sqlScript: ['', Validators.required],
        programma: ['', Validators.required],
        classe: ['', Validators.required],
        schedulazione: [
          '',
          [
            Validators.required,
            Validators.pattern(
              /^(?:\d+|\*|\?)(\/\d+)?(\s+(?:\d+|\*|\?)(\/\d+)?){4}(\s+(MON|TUE|WED|THU|FRI|SAT|SUN)(-(MON|TUE|WED|THU|FRI|SAT|SUN))?)?(\s+(\*|\?|(\d+))(\s+(\*|\?|(\d+)))?)*$/
            ),
          ],
        ],

        ordineConfigurazione: [
          null,
          Validators.required,
          // Validators.pattern(/^[0-9]*[1-9][0-9]*$/)
        ],
      }),
      soglie: this.fb.array([
        this.fb.group({
          sogliaInferiore: ['', Validators.required],
          sogliaSuperiore: ['', Validators.required],
          valore: ['', Validators.required],
          operatore: ['', Validators.required],
        }),
      ]),
    });
  }

  ngOnInit(): void {}

  get soglie(): FormArray {
    return this.configurazioneForm.get('soglie') as FormArray;
  }

  addSoglia() {
    const sogliaGroup = this.fb.group({
      sogliaInferiore: ['', Validators.required],
      sogliaSuperiore: ['', Validators.required],
      valore: ['', Validators.required],
      operatore: ['', Validators.required],
    });

    this.soglie.push(sogliaGroup);
  }

  removeSoglia(index: number) {
    this.soglie.removeAt(index);
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
}
