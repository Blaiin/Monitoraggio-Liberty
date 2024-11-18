import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-create-configurazione',
  templateUrl: './create-configurazione.component.html',
  styleUrls: ['./create-configurazione.component.css'],
})
export class CreateConfigurazioneComponent implements OnInit {
  configurazioneForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.configurazioneForm = this.fb.group({
      tipoControllo: this.fb.group({
        descrizione: ['', Validators.required],
      }),
      controllo: this.fb.group({
        descrizione: ['', Validators.required],
        tipoControlloID: [null, Validators.required],
        ambitoID: [null, Validators.required],
        ordineControllo: [null, Validators.required],
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
        schedulazione: ['', Validators.required],
        ordineConfigurazione: [null, Validators.required],
      }),
      soglie: this.fb.array([]),
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


  // Metodo di invio del form
  onSubmit() {
    if (this.configurazioneForm.valid) {

      const body = {
        content: this.configurazioneForm.value
      };
      console.log(
        'Form valid, data ready to send:',
       JSON.stringify(body)
      );
    } else {
    }
  }
}
