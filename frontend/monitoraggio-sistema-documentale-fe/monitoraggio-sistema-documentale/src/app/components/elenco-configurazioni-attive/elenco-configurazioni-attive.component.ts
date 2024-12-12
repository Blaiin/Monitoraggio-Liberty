import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { ConfigurazioneAttiva } from 'src/app/entities/configurazioni-attive';
import { ConfigurazioneAttivaService } from 'src/app/components/elenco-configurazioni-attive/configurazione-attiva.service';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-elenco-configurazioni-attive',
  templateUrl: './elenco-configurazioni-attive.component.html',
  styleUrls: ['./elenco-configurazioni-attive.component.css'],
})
export class ElencoConfigurazioniAttiveComponent
  implements OnInit, AfterViewInit
{
  title = 'Elenco configurazioni attive';
  elencoConfigurazioniAttive: ConfigurazioneAttiva[] = [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  displayedColumns: string[] = [
    'id',
    'nome',
    'descrizione',
    'tipoControlloID',
    'ordineConfigurazione',
  ];
  dataSource = new MatTableDataSource<ConfigurazioneAttiva>();

  constructor(private configurazioneService: ConfigurazioneAttivaService) {}

  ngOnInit() {
    this.configurazioneService.getElencoConfigurazioniAttive().subscribe(
      (response: ConfigurazioneAttiva[]) => {
        this.elencoConfigurazioniAttive = response;
        this.dataSource.data = response;
      },
      (error: any) => {
        //da rimuovere
        this.elencoConfigurazioniAttive = mockData;
        this.dataSource.data = mockData;
      }
    );
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
}

const mockData: ConfigurazioneAttiva[] = [
  {
    id: 1,
    nome: 'Schedulazione cmd.exe',
    tipoControllo: { tipoControlloID: 101, descrizione: 'Controllo tipo 1' },
    ordineConfigurazione: 1,
  },
  {
    id: 2,
    nome: 'Esecuzione script batch',
    tipoControllo: { tipoControlloID: 102, descrizione: 'Controllo tipo 2' },
    ordineConfigurazione: 2,
  },
  {
    id: 3,
    nome: 'Verifica directory',
    tipoControllo: { tipoControlloID: 103, descrizione: 'Controllo tipo 3' },
    ordineConfigurazione: 3,
  },
  {
    id: 4,
    nome: 'Test 1',
    tipoControllo: { tipoControlloID: 104, descrizione: 'Controllo tipo 4' },
    ordineConfigurazione: 4,
  },
  {
    id: 5,
    nome: 'Test 2',
    tipoControllo: { tipoControlloID: 105, descrizione: 'Controllo tipo 5' },
    ordineConfigurazione: 5,
  },
  {
    id: 6,
    nome: 'Test 3',
    tipoControllo: { tipoControlloID: 106, descrizione: 'Controllo tipo 6' },
    ordineConfigurazione: 6,
  },
  {
    id: 7,
    nome: 'Test 4',
    tipoControllo: { tipoControlloID: 107, descrizione: 'Controllo tipo 7' },
    ordineConfigurazione: 7,
  },
  {
    id: 8,
    nome: 'Test 5',
    tipoControllo: { tipoControlloID: 108, descrizione: 'Controllo tipo 8' },
    ordineConfigurazione: 8,
  },
  {
    id: 9,
    nome: 'Test 6',
    tipoControllo: { tipoControlloID: 109, descrizione: 'Controllo tipo 9' },
    ordineConfigurazione: 9,
  },
  {
    id: 10,
    nome: 'Test 7',
    tipoControllo: { tipoControlloID: 110, descrizione: 'Controllo tipo 10' },
    ordineConfigurazione: 10,
  },
];
