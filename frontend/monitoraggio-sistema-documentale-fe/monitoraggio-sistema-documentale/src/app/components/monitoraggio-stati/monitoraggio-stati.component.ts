import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ChartData, ChartOptions } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

const configurazioni = [
  { nome: 'Config1', memoria: 120 },
  { nome: 'Config2', memoria: 150 },
  { nome: 'Config3', memoria: 100 },
  { nome: 'Config4', memoria: 180 },
  { nome: 'Config5', memoria: 220 },
  { nome: 'Config6', memoria: 100 },
  { nome: 'Config7', memoria: 470 },
  { nome: 'Config8', memoria: 127 },
  { nome: 'Config9', memoria: 333 },
];
@Component({
  selector: 'app-monitoraggio-stati',
  templateUrl: './monitoraggio-stati.component.html',
  styleUrls: ['./monitoraggio-stati.component.css'],
})
export class MonitoraggioStatiComponent implements OnInit {
  title = 'Grafico Sinusoidale di Memoria';

  public chartData: ChartData = {
    labels: Array.from({ length: 100 }, (_, i) => i), // Etichette: punti temporali
    datasets: [
      {
        data: this.generareDatiSinusoidali(), // Dati iniziali
        label: 'Consumo di Memoria (MB)',
        fill: false,
        borderColor: 'rgba(75, 192, 192, 1)',
        tension: 0.4,
      },
    ],
  };

  public chartOptions: ChartOptions = {
    responsive: true,
    scales: {
      x: {
        title: {
          display: true,
          text: 'Tempo',
        },
      },
      y: {
        title: {
          display: true,
          text: 'Memoria (MB)',
        },
        beginAtZero: true,
      },
    },
  };

  ngOnInit(): void {
    // Aggiorna i dati ogni 5 secondi
    setInterval(() => {
      this.aggiornaDatiGrafico();
    }, 3000);
  }

  public generareDatiSinusoidali(): number[] {
    const dati = [];
    const frequenza = 0.1;
    const ampiezza = 50;
    const offset = 100;

    for (let x = 0; x < 100; x++) {
      const valoreSin = Math.sin(frequenza * x) * ampiezza + offset;
      dati.push(valoreSin + Math.random() * 10); // Aggiunta di rumore per variazione
    }
    return dati;
  }

  private aggiornaDatiGrafico(): void {
    // Aggiorna i dati del dataset
    this.chartData.datasets[0].data = this.generareDatiSinusoidali();

    // Forza l'aggiornamento del grafico
    this.chartData = { ...this.chartData };
  }
}
