import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AlterarSenhaRequest,
  AtualizarPerfilRequest,
  UsuarioEstatistica,
  UsuarioPerfil,
} from '../../shared/models/usuario.model';

const API_URL = 'http://localhost:8080/api/usuarios';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private http = inject(HttpClient);

  perfil(): Observable<UsuarioPerfil> {
    return this.http.get<UsuarioPerfil>(`${API_URL}/me`);
  }

  estatisticas(): Observable<UsuarioEstatistica> {
    return this.http.get<UsuarioEstatistica>(`${API_URL}/me/estatisticas`);
  }

  atualizar(req: AtualizarPerfilRequest): Observable<UsuarioPerfil> {
    return this.http.put<UsuarioPerfil>(`${API_URL}/me`, req);
  }

  alterarSenha(req: AlterarSenhaRequest): Observable<void> {
    return this.http.patch<void>(`${API_URL}/me/senha`, req);
  }

  excluirConta(senha: string): Observable<void> {
    const params = new HttpParams().set('senha', senha);
    return this.http.delete<void>(`${API_URL}/me`, { params });
  }
}
