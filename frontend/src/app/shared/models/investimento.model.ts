export type DirecaoAlerta = 'ALTA' | 'QUEDA' | 'AMBOS';

export interface Investimento {
  id: string;
  simbolo: string;
  quantidade: number;
  valorInicial: number;
  percentualAlerta: number;
  direcao: DirecaoAlerta;
  ativo: boolean;
  notificadoEm: string | null;
  createdAt: string;
  cotacaoAtual: number | null;
  variacaoPercentual: number | null;
  valorTotalAtual: number | null;
}

export interface InvestimentoRequest {
  simbolo: string;
  quantidade: number;
  valorInicial: number;
  percentualAlerta: number;
  direcao: DirecaoAlerta;
}
