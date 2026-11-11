import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { debounceTime } from 'rxjs';
import { LancamentoService } from '../../../core/services/lancamento.service';
import { Lancamento, TipoLancamento } from '../../../shared/models/lancamento.model';

@Component({
  selector: 'app-historico',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './historico.html',
  styleUrl: './historico.css',
})
export class HistoricoComponent implements OnInit {
  private service = inject(LancamentoService);

  readonly tamanho = 10;
  readonly min = Math.min;
  readonly categorias = [
    'Alimentação',
    'Moradia',
    'Transporte',
    'Saúde',
    'Educação',
    'Lazer',
    'Compras',
    'Assinaturas',
    'Investimento',
    'Entrada',
    'Outros',
  ];

  lancamentos = signal<Lancamento[]>([]);
  total = signal(0);
  pagina = signal(0);
  loading = signal(false);

  filtros = new FormGroup({
    tipo: new FormControl<TipoLancamento | ''>('', { nonNullable: true }),
    categoria: new FormControl('', { nonNullable: true }),
    mesAno: new FormControl('', { nonNullable: true }),
    busca: new FormControl('', { nonNullable: true }),
  });

  ngOnInit() {
    this.buscar();
    this.filtros.valueChanges.pipe(debounceTime(400)).subscribe(() => {
      this.pagina.set(0);
      this.buscar();
    });
  }

  buscar() {
    const f = this.filtros.getRawValue();
    this.loading.set(true);
    this.service
      .listar({
        tipo: f.tipo || undefined,
        categoria: f.categoria || undefined,
        mesAno: f.mesAno || undefined,
        page: this.pagina(),
        size: this.tamanho,
      })
      .subscribe({
        next: (p) => {
          const busca = f.busca.trim().toLowerCase();
          const conteudo = busca
            ? p.content.filter((l) => l.descricao.toLowerCase().includes(busca))
            : p.content;
          this.lancamentos.set(conteudo);
          this.total.set(p.totalElements);
          this.loading.set(false);
        },
        error: () => this.loading.set(false),
      });
  }

  remover(id: string) {
    if (!confirm('Remover este lançamento?')) return;
    this.service.remover(id).subscribe(() => this.buscar());
  }

  exportarCsv() {
    const f = this.filtros.getRawValue();
    this.service
      .listar({
        tipo: f.tipo || undefined,
        categoria: f.categoria || undefined,
        mesAno: f.mesAno || undefined,
        page: 0,
        size: 10000,
      })
      .subscribe((p) => {
        const cabecalho = 'Data;Descrição;Categoria;Tipo;Valor';
        const linhas = p.content.map((l) =>
          [l.data, l.descricao, l.categoria ?? '', l.tipo, String(l.valor).replace('.', ',')].join(';'),
        );
        const csv = '\uFEFF' + [cabecalho, ...linhas].join('\n');
        const blob = new Blob([csv], { type: 'text/csv;charset=utf-8' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `lancamentos-${f.mesAno || 'todos'}.csv`;
        a.click();
        URL.revokeObjectURL(url);
      });
  }

  proximaPagina() {
    this.pagina.update((p) => p + 1);
    this.buscar();
  }

  paginaAnterior() {
    this.pagina.update((p) => p - 1);
    this.buscar();
  }

  get temProxima() {
    return (this.pagina() + 1) * this.tamanho < this.total();
  }

  get temAnterior() {
    return this.pagina() > 0;
  }
}
