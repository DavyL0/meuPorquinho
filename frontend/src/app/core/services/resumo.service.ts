import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Resumo,
  ResumoCategoria,
  ResumoComparativo,
  ResumoMensal,
} from '../../shared/models/resumo.model';

const API_URL = 'http://localhost:8080/api/resumo';

@Injectable({ providedIn: 'root' })
export class ResumoService {
  private http = inject(HttpClient);

  geral(mesAno?: string): Observable<Resumo> {
    const params = mesAno ? new HttpParams().set('mesAno', mesAno) : undefined;
    return this.http.get<Resumo>(API_URL, { params });
  }

  porCategoria(mesAno?: string): Observable<ResumoCategoria[]> {
    const params = mesAno ? new HttpParams().set('mesAno', mesAno) : undefined;
    return this.http.get<ResumoCategoria[]>(`${API_URL}/categorias`, { params });
  }

  evolucao(meses = 6): Observable<ResumoMensal[]> {
    const params = new HttpParams().set('meses', String(meses));
    return this.http.get<ResumoMensal[]>(`${API_URL}/evolucao`, { params });
  }

  comparativo(mesAtual: string, mesAnterior: string): Observable<ResumoComparativo> {
    const params = new HttpParams()
      .set('mesAtual', mesAtual)
      .set('mesAnterior', mesAnterior);
    return this.http.get<ResumoComparativo>(`${API_URL}/comparativo`, { params });
  }
}
