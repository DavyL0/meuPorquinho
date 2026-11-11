import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BaseChartDirective } from 'ng2-charts';
import type { ChartOptions } from 'chart.js';
import { AuthService } from '../../core/services/auth.service';
import { ResumoService } from '../../core/services/resumo.service';
import { LancamentoService } from '../../core/services/lancamento.service';
import { Resumo, ResumoCategoria, ResumoMensal } from '../../shared/models/resumo.model';
import { Lancamento, TipoLancamento } from '../../shared/models/lancamento.model';
import { UsuarioLogado } from '../../shared/models/usuario.model';

const PALETA = ['#4f8ef7', '#ef4444', '#7c3aed', '#22c55e', '#f59e0b', '#06b6d4', '#ec4899', '#84cc16'];

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
  categorias = signal<ResumoCategoria[]>([]);
  evolucao = signal<ResumoMensal[]>([]);
  recentes = signal<Lancamento[]>([]);

  mesesDisponiveis = this.gerarMeses(12);

  ngOnInit() {
    this.carregar();
  }

  carregar() {
    const mes = this.mesAtual();

    this.resumoService.geral(mes).subscribe((r) => this.resumo.set(r));
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

  barData = computed(() => ({
    labels: this.evolucao().map((e) => e.mesAno.slice(5)),
    datasets: [
      {
        label: 'Entradas',
        data: this.evolucao().map((e) => e.entradas),
        backgroundColor: '#4f8ef7',
      },
      {
        label: 'Gastos',
        data: this.evolucao().map((e) => e.gastos),
        backgroundColor: '#ef4444',
      },
      {
        label: 'Investimentos',
        data: this.evolucao().map((e) => e.investimentos),
        backgroundColor: '#7c3aed',
      },
    ],
  }));

  barOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { labels: { color: '#8b93a7' } } },
    scales: {
      x: { ticks: { color: '#8b93a7' }, grid: { color: '#232a3a' } },
      y: { ticks: { color: '#8b93a7' }, grid: { color: '#232a3a' } },
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

  iconeTipo(tipo: TipoLancamento): string {
    return tipo === 'ENTRADA' ? '💰' : tipo === 'INVESTIMENTO' ? '📈' : '💸';
  }

  bgTipo(tipo: TipoLancamento): string {
    return tipo === 'ENTRADA' ? '#22c55e22' : tipo === 'INVESTIMENTO' ? '#7c3aed22' : '#ef444422';
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
