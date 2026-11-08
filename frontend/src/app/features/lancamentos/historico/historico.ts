import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { LancamentoService } from '../../../core/services/lancamento.service';
import { inject } from 'vitest';
import { debounceTime } from 'rxjs';

@Component({
  selector: 'app-historico',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './historico.component.html',
})
export class HistoricoComponent implements OnInit {
  private service = inject(LancamentoService);

  lancamentos = signal<Lancamento[]>([]);
  total = signal(0);
  pagina = signal(0);
  readonly tamanho = 10;

  filtros = new FormGroup({
    tipo: new FormControl(''),
    categoria: new FormControl(''),
    mesAno: new FormControl(''),
    busca: new FormControl(''),
  });

  ngOnInit() {
    this.buscar();
    this.filtros.valueChanges.pipe(debounceTime(400)).subscribe(() => {
      this.pagina.set(0);
      this.buscar();
    });
  }

  buscar() {
    const f = this.filtros.value;
    this.service
      .listar({
        tipo: f.tipo || undefined,
        categoria: f.categoria || undefined,
        mesAno: f.mesAno || undefined,
        page: this.pagina(),
        size: this.tamanho,
      })
      .subscribe((p) => {
        this.lancamentos.set(p.content);
        this.total.set(p.totalElements);
      });
  }

  remover(id: string) {
    if (!confirm('Remover este lançamento?')) return;
    this.service.remover(id).subscribe(() => this.buscar());
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
