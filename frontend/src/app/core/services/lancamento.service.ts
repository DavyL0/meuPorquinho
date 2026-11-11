import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  FiltroLancamento,
  Lancamento,
  LancamentoDTO,
  Page,
} from '../../shared/models/lancamento.model';

const API_URL = 'http://localhost:8080/api/lancamentos';

@Injectable({ providedIn: 'root' })
export class LancamentoService {
  private http = inject(HttpClient);

  listar(filtro: FiltroLancamento = {}): Observable<Page<Lancamento>> {
    let params = new HttpParams();
    if (filtro.tipo) params = params.set('tipo', filtro.tipo);
    if (filtro.categoria) params = params.set('categoria', filtro.categoria);
    if (filtro.mesAno) params = params.set('mesAno', filtro.mesAno);
    params = params
      .set('page', String(filtro.page ?? 0))
      .set('size', String(filtro.size ?? 20));
    return this.http.get<Page<Lancamento>>(API_URL, { params });
  }

  criar(dto: LancamentoDTO): Observable<Lancamento> {
    return this.http.post<Lancamento>(API_URL, dto);
  }

  remover(id: string): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}
