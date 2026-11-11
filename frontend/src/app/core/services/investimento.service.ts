import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Investimento,
  InvestimentoRequest,
} from '../../shared/models/investimento.model';

const API_URL = 'http://localhost:8080/api/investimentos';

@Injectable({ providedIn: 'root' })
export class InvestimentoService {
  private http = inject(HttpClient);

  listar(): Observable<Investimento[]> {
    return this.http.get<Investimento[]>(API_URL);
  }

  criar(req: InvestimentoRequest): Observable<Investimento> {
    return this.http.post<Investimento>(API_URL, req);
  }

  reativar(id: string): Observable<Investimento> {
    return this.http.patch<Investimento>(`${API_URL}/${id}/reativar`, null);
  }

  remover(id: string): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}
