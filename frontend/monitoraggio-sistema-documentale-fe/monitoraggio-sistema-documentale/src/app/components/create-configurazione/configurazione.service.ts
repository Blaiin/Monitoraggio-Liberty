import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Configurazione } from 'src/app/entities/Configurazione';
@Injectable({
  providedIn: 'root',
})
export class ConfigurazioneService {
  private readonly apiUrl = 'https://localhost:9000/configuration';

  constructor(private http: HttpClient) {}

  /**
   * Aggiunge una nuova configurazione
   * @param configurazione L'oggetto configurazione da aggiungere
   * @returns Un Observable della risposta del backend
   */

  aggiungiConfigurazione(body: any): Observable<any> {
    const headers = new HttpHeaders();
    return this.http.post<Configurazione>(this.apiUrl, body, { headers });
  }
}
