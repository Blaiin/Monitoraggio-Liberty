import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-create-configurazione',
  templateUrl: './create-configurazione.component.html'
})
export class CreateConfigurazioneComponent implements OnInit {
  configurazioneForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.configurazioneForm = this.fb.group({
      tipoControllo: ['', Validators.required],
      controlloDescrizione: ['', Validators.required],
      tipoControlloID: ['', Validators.required],
      ambitoID: ['', Validators.required],
      ordineControllo: ['', Validators.required],
      ambitoNome: ['', Validators.required],
      ambitoDestinazione: ['', Validators.required],
      fonteDatiDescrizione: ['', Validators.required],
      nomeDriver: ['', Validators.required],
      nomeClasse: ['', Validators.required],
      url: ['', Validators.required],
      jndiName: ['', Validators.required],
      utenteFonteDatiDescrizione: ['', Validators.required],
      utenteFonteDatiUsername: ['', Validators.required],
      utenteFonteDatiPassword: ['', Validators.required],
      nomeConfigurazione: ['', Validators.required],
      sqlScript: ['', Validators.required],
      programma: ['', Validators.required],
      classe: ['', Validators.required],
      schedulazione: ['', Validators.required],
      ordineConfigurazione: ['', Validators.required],
      soglie: this.fb.array([
        this.fb.group({
          sogliaInferiore: ['', Validators.required],
          sogliaSuperiore: ['', Validators.required],
          valore: ['', Validators.required],
          operatore: ['', Validators.required]
        }),
        this.fb.group({
          sogliaInferiore: ['', Validators.required],
          sogliaSuperiore: ['', Validators.required],
          valore: ['', Validators.required],
          operatore: ['', Validators.required]
        })
      ])
    });
  }

  ngOnInit(): void {

  }

  // Getter per le soglie
  get soglie() {
    return this.configurazioneForm.get('soglie') as FormArray;
  }

  // Aggiungi una nuova soglia
  addSoglia() {
    this.soglie.push(this.fb.group({
      sogliaInferiore: ['', Validators.required],
      sogliaSuperiore: ['', Validators.required],
      valore: ['', Validators.required],
      operatore: ['', Validators.required]
    }));
  }

  // Rimuovi una soglia
  removeSoglia(index: number) {
    this.soglie.removeAt(index);
  }

  // Submit del form
  onSubmit() {
    if (this.configurazioneForm.valid) {
      console.log(this.configurazioneForm.value);
    } else {
      console.log('Form non valido');
    }
  }
}
