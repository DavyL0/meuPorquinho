export interface Resumo {
  totalEntradas: number;
  totalGastos: number;
  totalInvestimentos: number;
  saldo: number;
}

export interface ResumoCategoria {
  categoria: string;
  total: number;
  quantidade: number;
}

export interface ResumoMensal {
  mesAno: string; // yyyy-MM
  entradas: number;
  gastos: number;
  investimentos: number;
  saldo: number;
}

export interface ResumoComparativo {
  mesAtual: Resumo;
  mesAnterior: Resumo;
  variacaoGastos: number | null;
  variacaoEntradas: number | null;
  variacaoSaldo: number | null;
}
