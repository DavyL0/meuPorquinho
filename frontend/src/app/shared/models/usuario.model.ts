export interface UsuarioLogado {
  nome: string;
  email: string;
}

export interface UsuarioPerfil {
  id: string;
  nome: string;
  email: string;
  createdAt: string;
}

export interface UsuarioEstatistica {
  id: string;
  nome: string;
  email: string;
  totalLancamentos: number;
  membroDesde: string;
}

export interface AtualizarPerfilRequest {
  nome: string;
  email: string;
}

export interface AlterarSenhaRequest {
  senhaAtual: string;
  novaSenha: string;
}

export interface LoginResponse {
  token: string;
  nome: string;
  email: string;
  expiresIn: number;
}
