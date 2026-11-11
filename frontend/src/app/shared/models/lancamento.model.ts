export type TipoLancamento = 'GASTO' | 'ENTRADA' | 'INVESTIMENTO';

export interface Lancamento {
  id: string;
  tipo: TipoLancamento;
  descricao: string;
  valor: number;
  categoria: string | null;
  data: string; // ISO yyyy-MM-dd
  createdAt?: string;
}

export interface LancamentoDTO {
  tipo: TipoLancamento;
  descricao: string;
  valor: number;
  categoria?: string | null;
  data: string; // ISO yyyy-MM-dd
}

export interface FiltroLancamento {
  tipo?: TipoLancamento | '';
  categoria?: string;
  mesAno?: string;
  page?: number;
  size?: number;
}

/** Estrutura de paginação do Spring Data. */
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
