import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-monitoraggio-stati',
  templateUrl: './monitoraggio-stati.component.html',
  styleUrls: ['./monitoraggio-stati.component.css'],
})
export class MonitoraggioStatiComponent implements OnInit {
  @ViewChild('sinCanvas', { static: true })
  sinCanvas!: ElementRef<HTMLCanvasElement>;

  constructor() {}

  ngOnInit() {
    this.drawSinusoidalGraph();
  }

  drawSinusoidalGraph() {
    const canvas = this.sinCanvas.nativeElement;
    const ctx = canvas.getContext('2d');

    if (!ctx) {
      console.error('Impossibile ottenere il contesto del canvas');
      return;
    }

    // Imposta il contesto
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.strokeStyle = 'blue';
    ctx.lineWidth = 2;

    // Parametri della sinusoide
    const amplitude = 100; // Altezza dell'onda
    const frequency = 2; // Frequenza dell'onda
    const offsetX = 50; // Margine sinistro
    const offsetY = canvas.height / 2; // Linea centrale

    // Disegna la sinusoide
    ctx.beginPath();
    for (let x = 0; x < canvas.width; x++) {
      const y =
        offsetY -
        amplitude * Math.sin((x / canvas.width) * frequency * 2 * Math.PI);
      x === 0 ? ctx.moveTo(x + offsetX, y) : ctx.lineTo(x + offsetX, y);
    }
    ctx.stroke();

    // Disegna gli assi
    this.drawAxes(ctx, canvas);
  }

  drawAxes(ctx: CanvasRenderingContext2D, canvas: HTMLCanvasElement) {
    ctx.strokeStyle = 'black';
    ctx.lineWidth = 1;

    // Asse X
    ctx.beginPath();
    ctx.moveTo(0, canvas.height / 2);
    ctx.lineTo(canvas.width, canvas.height / 2);
    ctx.stroke();

    // Asse Y
    ctx.beginPath();
    ctx.moveTo(50, 0);
    ctx.lineTo(50, canvas.height);
    ctx.stroke();
  }
}
