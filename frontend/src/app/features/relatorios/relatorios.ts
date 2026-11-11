import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import type { ChartOptions } from 'chart.js';
import { ResumoService } from '../../core/services/resumo.service';
import {
  Resumo,
  ResumoCategoria,
  ResumoComparativo,
  ResumoMensal,
} from '../../shared/models/resumo.model';

const PALETA = ['#5b9dff', '#34d399', '#fbbf24', '#a78bfa', '#f87171', '#22d3ee', '#f472b6', '#a3e635'];

@Component({
  selector: 'app-relatorios',
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './relatorios.html',
  styleUrl: './relatorios.css',
})
export class RelatoriosComponent implements OnInit {
  private resumoService = inject(ResumoService);

  readonly periodos = [
    { label: '3 meses', valor: 3 },
    { label: '6 meses', valor: 6 },
    { label: '12 meses', valor: 12 },
  ];

  periodoAtual = signal(6);
  evolucao = signal<ResumoMensal[]>([]);
  categorias = signal<ResumoCategoria[]>([]);
  comparativo = signal<ResumoComparativo | null>(null);

  resumo = computed<Resumo>(() => {
    const total = this.evolucao().reduce(
      (acc, m) => ({
        totalEntradas: acc.totalEntradas + m.entradas,
        totalGastos: acc.totalGastos + m.gastos,
        totalInvestimentos: acc.totalInvestimentos + m.investimentos,
      }),
      { totalEntradas: 0, totalGastos: 0, totalInvestimentos: 0 },
    );
    return {
      ...total,
      saldo: total.totalEntradas - total.totalGastos - total.totalInvestimentos,
    };
  });

  taxaEconomia = computed(() => {
    const r = this.resumo();
    if (r.totalEntradas <= 0) return 0;
    const taxa = (r.saldo / r.totalEntradas) * 100;
    return Math.min(100, Math.max(0, taxa));
  });

  ngOnInit() {
    this.carregar();

    const agora = new Date();
    const mesAtual = agora.toISOString().slice(0, 7);
    const anterior = new Date(agora.getFullYear(), agora.getMonth() - 1, 1);
    const mesAnterior = anterior.toISOString().slice(0, 7);
    this.resumoService
      .comparativo(mesAtual, mesAnterior)
      .subscribe((c) => this.comparativo.set(c));
  }

  setPeriodo(meses: number) {
    this.periodoAtual.set(meses);
    this.carregar();
  }

  carregar() {
    this.resumoService.evolucao(this.periodoAtual()).subscribe((e) => this.evolucao.set(e));
    this.resumoService.porCategoria().subscribe((c) => this.categorias.set(c));
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
}
