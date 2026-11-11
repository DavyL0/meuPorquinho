import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BaseChartDirective } from 'ng2-charts';
import type { ChartOptions } from 'chart.js';
import { AuthService } from '../../core/services/auth.service';
import { ResumoService } from '../../core/services/resumo.service';
import { LancamentoService } from '../../core/services/lancamento.service';
import {
  Resumo,
  ResumoCategoria,
  ResumoComparativo,
  ResumoMensal,
} from '../../shared/models/resumo.model';
import { Lancamento, TipoLancamento } from '../../shared/models/lancamento.model';
import { UsuarioLogado } from '../../shared/models/usuario.model';

const PALETA = ['#5b9dff', '#34d399', '#fbbf24', '#a78bfa', '#f87171', '#22d3ee', '#f472b6', '#a3e635'];

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterLink, BaseChartDirective],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class DashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private resumoService = inject(ResumoService);
  private lancamentoService = inject(LancamentoService);

  usuario: UsuarioLogado | null = this.authService.getUsuario();

  mesAtual = signal(new Date().toISOString().slice(0, 7));
  resumo = signal<Resumo | null>(null);
  comparativo = signal<ResumoComparativo | null>(null);
  categorias = signal<ResumoCategoria[]>([]);
  evolucao = signal<ResumoMensal[]>([]);
  recentes = signal<Lancamento[]>([]);

  mesesDisponiveis = this.gerarMeses(12);

  ngOnInit() {
    this.carregar();
  }

  carregar() {
    const mes = this.mesAtual();
    const anterior = this.mesAnterior(mes);

    this.resumoService.geral(mes).subscribe((r) => this.resumo.set(r));
    this.resumoService.comparativo(mes, anterior).subscribe((c) => this.comparativo.set(c));
    this.resumoService.porCategoria(mes).subscribe((c) => this.categorias.set(c));
    this.resumoService.evolucao(6).subscribe((e) => this.evolucao.set(e));
    this.lancamentoService
      .listar({ mesAno: mes, size: 5 })
      .subscribe((p) => this.recentes.set(p.content));
  }

  onMesChange(mes: string) {
    this.mesAtual.set(mes);
    this.carregar();
  }

  /** "novembro" — nome do mês selecionado, em minúsculas. */
  nomeMesAtual(): string {
    return this.nomeMes(this.mesAtual());
  }

  /** "outubro" — nome do mês anterior ao selecionado. */
  nomeMesAnterior(): string {
    return this.nomeMes(this.mesAnterior(this.mesAtual()));
  }

  deltaEntradas(): { texto: string; classe: string } {
    const v = this.comparativo()?.variacaoEntradas;
    if (v == null) return { texto: 'sem dados de ' + this.nomeMesAnterior(), classe: 'mut' };
    return {
      texto: `${v > 0 ? '+' : ''}${v.toFixed(1).replace('.', ',')}% vs ${this.nomeMesAnterior()}`,
      classe: v >= 0 ? 'pos' : 'neg',
    };
  }

  deltaGastos(): { texto: string; classe: string } {
    const v = this.comparativo()?.variacaoGastos;
    if (v == null) return { texto: 'sem dados de ' + this.nomeMesAnterior(), classe: 'mut' };
    return {
      texto: `${v > 0 ? '+' : ''}${v.toFixed(1).replace('.', ',')}% vs ${this.nomeMesAnterior()}`,
      // gasto maior é ruim (vermelho), gasto menor é bom (verde)
      classe: v > 0 ? 'neg' : 'pos',
    };
  }

  deltaInvestimentos(): { texto: string; classe: string } {
    const c = this.comparativo();
    if (!c) return { texto: '—', classe: 'mut' };
    const diff = (c.mesAtual?.totalInvestimentos ?? 0) - (c.mesAnterior?.totalInvestimentos ?? 0);
    if (diff === 0) return { texto: 'sem aportes no mês', classe: 'mut' };
    const fmt = Math.abs(diff).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL', maximumFractionDigits: 0 });
    return { texto: `${diff > 0 ? '+' : '-'}${fmt} no mês`, classe: diff > 0 ? 'pos' : 'neg' };
  }

  margemSaldo(): { texto: string; classe: string } {
    const r = this.resumo();
    if (!r || !r.totalEntradas) return { texto: '—', classe: 'mut' };
    const margem = Math.round((r.saldo / r.totalEntradas) * 100);
    return { texto: `margem de ${margem}%`, classe: margem >= 0 ? 'pos' : 'neg' };
  }

  barData = computed(() => ({
    labels: this.evolucao().map((e) => e.mesAno.slice(5)),
    datasets: [
      {
        label: 'Entradas',
        data: this.evolucao().map((e) => e.entradas),
        backgroundColor: '#34d399',
      },
      {
        label: 'Gastos',
        data: this.evolucao().map((e) => e.gastos),
        backgroundColor: '#f87171',
      },
      {
        label: 'Investimentos',
        data: this.evolucao().map((e) => e.investimentos),
        backgroundColor: '#a78bfa',
      },
    ],
  }));

  barOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { labels: { color: '#8b95a8' } } },
    scales: {
      x: { ticks: { color: '#8b95a8' }, grid: { color: '#1e2633' } },
      y: { ticks: { color: '#8b95a8' }, grid: { color: '#1e2633' } },
    },
  };

  donutData = computed(() => ({
    labels: this.categorias().map((c) => c.categoria),
    datasets: [
      {
        data: this.categorias().map((c) => c.total),
        backgroundColor: this.categorias().map((c) => this.corCategoria(c.categoria)),
        borderWidth: 0,
      },
    ],
  }));

  donutOptions: ChartOptions<'doughnut'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    cutout: '70%',
  };

  corCategoria(categoria: string): string {
    const idx = this.categorias().findIndex((c) => c.categoria === categoria);
    if (idx >= 0) return PALETA[idx % PALETA.length];
    let hash = 0;
    for (const ch of categoria) hash = (hash * 31 + ch.charCodeAt(0)) | 0;
    return PALETA[Math.abs(hash) % PALETA.length];
  }

  pctCategoria(total: number): number {
    const max = Math.max(...this.categorias().map((c) => c.total), 0);
    return max > 0 ? Math.round((total / max) * 100) : 0;
  }

  /** Chave do ícone SVG exibido na lista de últimos lançamentos. */
  iconeLancamento(l: Lancamento): string {
    if (l.tipo === 'ENTRADA') return 'wallet';
    if (l.tipo === 'INVESTIMENTO') return 'trending';
    const mapa: Record<string, string> = {
      'Alimentação': 'cart',
      'Moradia': 'home',
      'Transporte': 'car',
      'Saúde': 'heart',
      'Educação': 'book',
      'Lazer': 'smile',
      'Compras': 'bag',
      'Assinaturas': 'tv',
      'Outros': 'dots',
    };
    return mapa[l.categoria ?? ''] ?? 'dots';
  }

  classeIcone(tipo: TipoLancamento): string {
    return tipo === 'ENTRADA' ? 'green' : tipo === 'INVESTIMENTO' ? 'purple' : 'red';
  }

  private mesAnterior(mesAno: string): string {
    const [ano, mes] = mesAno.split('-').map(Number);
    const d = new Date(ano, mes - 2, 1);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
  }

  private nomeMes(mesAno: string): string {
    const [ano, mes] = mesAno.split('-').map(Number);
    return new Intl.DateTimeFormat('pt-BR', { month: 'long' }).format(new Date(ano, mes - 1, 1));
  }

  private gerarMeses(quantidade: number) {
    const meses: { valor: string; label: string }[] = [];
    const fmt = new Intl.DateTimeFormat('pt-BR', { month: 'long', year: 'numeric' });
    const data = new Date();
    data.setDate(1);
    for (let i = 0; i < quantidade; i++) {
      const valor = `${data.getFullYear()}-${String(data.getMonth() + 1).padStart(2, '0')}`;
      const label = fmt.format(data);
      meses.push({ valor, label: label.charAt(0).toUpperCase() + label.slice(1) });
      data.setMonth(data.getMonth() - 1);
    }
    return meses;
  }
}
