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
  title = 'Grafico Sinusoidale di Memoria';

  constructor(){

  }

ngOnInit(): void {

}

  // Generiamo dati simulati per un grafico sinusoidale
  public generareDatiSinusoidali(): number[] {
    const dati = [];
    const frequenza = 0.1;  // Frequenza dell'onda sinusoidale
    const ampiezza = 50;  // Ampiezza della curva
    const offset = 100;  // Offset per il consumo di memoria

    // Genera 100 punti per la curva sinusoidale
    for (let x = 0; x < 100; x++) {
      const valoreSin = Math.sin(frequenza * x) * ampiezza + offset;
      dati.push(valoreSin);
    }
    return dati;
  }

  // Dati per il grafico (sinusoidale)
  public chartData: ChartData = {
    labels: Array.from({ length: 100 }, (_, i) => i),  // Etichette: punti temporali o altre variabili
    datasets: [
      {
        data: this.generareDatiSinusoidali(),  // Consumo di memoria simulato tramite funzione sinusoidale
        label: 'Consumo di Memoria (MB)',
        fill: false,
        borderColor: 'rgba(75, 192, 192, 1)',
        tension: 0.4  // La curvatura della linea
      }
    ]
  };

  // Opzioni del grafico
  public chartOptions: ChartOptions = {
    responsive: true,
    scales: {
      x: {
        title: {
          display: true,
          text: 'Tempo'
        }
      },
      y: {
        title: {
          display: true,
          text: 'Memoria (MB)'
        },
        beginAtZero: true  // L'asse Y inizia da zero
      }
    }
  };
}
