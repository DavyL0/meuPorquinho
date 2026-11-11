import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginResponse, UsuarioLogado } from '../../shared/models/usuario.model';

const API_URL = 'http://localhost:8080/api/auth';
const TOKEN_KEY = 'porquinho_token';
const USER_KEY = 'porquinho_usuario';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  login(email: string, senha: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_URL}/login`, { email, senha }).pipe(
      tap((res) => {
        localStorage.setItem(TOKEN_KEY, res.token);
        const usuario: UsuarioLogado = { nome: res.nome, email: res.email };
        localStorage.setItem(USER_KEY, JSON.stringify(usuario));
      }),
    );
  }

  registrar(nome: string, email: string, senha: string): Observable<void> {
    return this.http.post<void>(`${API_URL}/registro`, { nome, email, senha });
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  getUsuario(): UsuarioLogado | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as UsuarioLogado;
    } catch {
      return null;
    }
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
