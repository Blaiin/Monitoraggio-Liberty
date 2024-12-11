import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment.prod';

@Injectable({
  providedIn: 'root',
})
export class TrasferimentoManualeService {
  private apiUrl = environment.developUrl;

  constructor(private http: HttpClient) {}

  getElencoConfigurazioniAttive(): Observable<any> {
    const payload = {};
    const headers = { 'Content-Type': 'application/json' };

    return this.http.post<any>(
      this.apiUrl + `elenco-configurazioni-attive`,
      payload,
      {
        headers,
      }
    );
  }
}
