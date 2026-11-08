import { Component, computed, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { inject } from 'vitest';
import { ResumoService } from '../../core/services/resumo.service';
import { LancamentoService } from '../../core/services/lancamento.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, NgChartsModule],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  private resumoService = inject(ResumoService);
  private lancamentoService = inject(LancamentoService);

  mesAtual = signal(new Date().toISOString().slice(0, 7));
  resumo = signal<Resumo | null>(null);
  categorias = signal<ResumoCategoria[]>([]);
  evolucao = signal<ResumoMensal[]>([]);
  recentes = signal<Lancamento[]>([]);

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

  // computed para o gráfico de barras
  barData = computed(() => ({
    labels: this.evolucao().map((e) => e.mesAno.slice(5)),
    datasets: [
      {
        label: 'Entradas',
        data: this.evolucao().map((e) => e.entradas),
        backgroundColor: '#4f8ef7',
      },
      { label: 'Gastos', data: this.evolucao().map((e) => e.gastos), backgroundColor: '#ef4444' },
      {
        label: 'Investimentos',
        data: this.evolucao().map((e) => e.investimentos),
        backgroundColor: '#7c3aed',
      },
    ],
  }));
}
