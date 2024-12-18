import { Component, OnInit } from '@angular/core';
import { ConfigurazioneAttiva } from 'src/app/entities/configurazioni-attive';
import { ConfigurazioneAttivaService } from 'src/app/components/elenco-configurazioni-attive/configurazione-attiva.service';
import { ChartOptions } from 'chart.js';

@Component({
  selector: 'app-elenco-configurazioni-attive',
  templateUrl: './elenco-configurazioni-attive.component.html',
  styleUrls: ['./elenco-configurazioni-attive.component.css'],
})
export class ElencoConfigurazioniAttiveComponent implements OnInit {
  title = 'Elenco configurazioni attive';
  chartType: 'bar' | 'doughnut' | 'pie' | 'line' = 'doughnut';
  chartLabels: string[] = [];
  chartData: number[] = [];

  chartOptions: ChartOptions<'bar' | 'doughnut' | 'pie' | 'line'> = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        position: 'top',
      },
    },
  };

  constructor(private configurazioneService: ConfigurazioneAttivaService) {}

  ngOnInit() {}

  onChangeGraphicType(event: any) {
    const value = event.target.value;
    console.log(value);
    this.configurazioneService.getElencoConfigurazioniAttive().subscribe(
      (response: ConfigurazioneAttiva[]) => {
        this.prepareChartData(response);
      },
      (error) => {
        this.prepareChartData(mockData);
      }
    );
  }

  private prepareChartData(configurazioni: ConfigurazioneAttiva[]) {
    this.chartLabels = configurazioni.map((config) => config.nome);
    this.chartData = configurazioni.map(() => 1); // Mostra 1 unità per configurazione attiva
  }
}

// Mock data per test
const mockData: ConfigurazioneAttiva[] = [
  {
    id: 1,
    nome: 'Schedulazione cmd.exe',
    tipoControllo: { tipoControlloID: 101, descrizione: 'test' },
    ordineConfigurazione: 1,
  },
  {
    id: 2,
    nome: 'Esecuzione script batch',
    tipoControllo: { tipoControlloID: 102, descrizione: 'test' },
    ordineConfigurazione: 2,
  },
  {
    id: 3,
    nome: 'Verifica directory',
    tipoControllo: { tipoControlloID: 103, descrizione: 'test' },
    ordineConfigurazione: 3,
  },
];
