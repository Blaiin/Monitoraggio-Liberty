import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ChartData, ChartOptions } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

const configurazioni = [
  { nome: 'Config1', memoria: 120 },
  { nome: 'Config2', memoria: 150 },
  { nome: 'Config3', memoria: 100 },
  { nome: 'Config4', memoria: 180 },
  { nome: 'Config5', memoria: 220 },
];
@Component({
  selector: 'app-monitoraggio-stati',
  templateUrl: './monitoraggio-stati.component.html',
  styleUrls: ['./monitoraggio-stati.component.css'],
})
export class MonitoraggioStatiComponent implements OnInit {
  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }
  title = 'Monitoraggio Sistema Documentale';

  // Esempio di dati di configurazioni con consumo di memoria
  public configurazioni = [
    { nome: 'Config1', memoria: 120 },
    { nome: 'Config2', memoria: 150 },
    { nome: 'Config3', memoria: 100 },
    { nome: 'Config4', memoria: 180 },
    { nome: 'Config5', memoria: 220 },
  ];

  // Prepara i dati del grafico
  public chartData: ChartData = {
    labels: this.configurazioni.map((c) => c.nome), // Etichette: nomi delle configurazioni
    datasets: [
      {
        data: this.configurazioni.map((c) => c.memoria), // Dati: consumo di memoria
        label: 'Consumo di Memoria (MB)',
        backgroundColor: 'rgba(0, 123, 255, 0.5)',
        borderColor: 'rgba(0, 123, 255, 1)',
        borderWidth: 1,
      },
    ],
  };

  // Opzioni del grafico
  public chartOptions: ChartOptions = {
    responsive: true,
    scales: {
      x: {
        title: {
          display: true,
          text: 'Configurazioni',
        },
      },
      y: {
        title: {
          display: true,
          text: 'Memoria (MB)',
        },
        beginAtZero: true, // Inizia l'asse Y da zero
      },
    },
  };
}
