import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { PeriodicElement } from './configurazione';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

const ELEMENT_DATA: PeriodicElement[] = [
  { id: 1, name: 'Configurazione 1', stato: 'In attesa' },
  { id: 2, name: 'Configurazione 2', stato: 'In attesa' },
  { id: 3, name: 'Configurazione 3', stato: 'Esecuzione programmata' },
  { id: 4, name: 'Configurazione 4', stato: 'Esecuzione programmata' },
  { id: 5, name: 'Configurazione 5', stato: 'Stop' },
  { id: 6, name: 'Configurazione 6', stato: 'Stop' },
  { id: 7, name: 'Configurazione 7', stato: 'In attesa' },
];

@Component({
  selector: 'app-menu-configurazioni',
  templateUrl: './menu-configurazioni.component.html',
  styleUrls: ['./menu-configurazioni.component.css'],
})
export class MenuConfigurazioniComponent implements OnInit {
  displayedColumns: string[] = ['name', 'stato'];
  dataSource = new MatTableDataSource(ELEMENT_DATA);
  new = false;
  configurazioneForm!: FormGroup;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;



  constructor(private router: Router,private fb: FormBuilder) {
    this.configurazioneForm = this.fb.group({
      soglie: this.fb.group({
        soglia1: ['', Validators.required],
        soglia2: ['', Validators.required],
        soglia3: ['', Validators.required],
      }),
      azioni: this.fb.group({
        azione1: ['', Validators.required],
        azione2: ['', Validators.required],
        azione3: ['', Validators.required],
      }),
    });
  }

  ngOnInit() {

  }


  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  toggleNew() {
    this.new = !this.new;
    this.router.navigate(['/monitoraggio-sistema-documentale/menu-configurazioni/create-configurazione']);
  }

  onSubmit(): void {
    if (this.configurazioneForm.valid) {
      console.log('Form Values:', this.configurazioneForm.value);
    } else {
      console.log('Form is invalid!');
    }
  }
}
