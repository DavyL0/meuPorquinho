import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LancamentoService } from '../../../core/services/lancamento.service';
import { ResumoService } from '../../../core/services/resumo.service';
import { LancamentoDTO, TipoLancamento } from '../../../shared/models/lancamento.model';
import { Resumo, ResumoCategoria } from '../../../shared/models/resumo.model';

@Component({
  selector: 'app-lancamento-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './form.html',
  styleUrl: './form.css',
})
export class LancamentoFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private service = inject(LancamentoService);
  private resumoService = inject(ResumoService);
  private router = inject(Router);

  readonly categorias = [
    'Alimentação',
    'Moradia',
    'Transporte',
    'Saúde',
    'Educação',
    'Lazer',
    'Compras',
    'Assinaturas',
    'Outros',
  ];

  form = this.fb.nonNullable.group({
    tipo: ['GASTO' as TipoLancamento, Validators.required],
    valor: [null as number | null, [Validators.required, Validators.min(0.01)]],
    data: [this.hoje(), Validators.required],
    descricao: ['', [Validators.required, Validators.maxLength(200)]],
    categoria: ['Alimentação'],
    pagamento: ['Débito'],
  });

  sucesso = signal(false);
  erro = signal('');
  loading = signal(false);

  // Painel lateral "Resumo do mês"
  resumo = signal<Resumo | null>(null);
  totalMes = signal(0);
  maiorCategoria = signal('—');
  ultimoRegistro = signal('—');

  ngOnInit() {
    this.carregarResumo();
  }

  carregarResumo() {
    const mes = this.hoje().slice(0, 7);
    this.resumoService.geral(mes).subscribe((r) => this.resumo.set(r));
    this.resumoService.porCategoria(mes).subscribe((cats: ResumoCategoria[]) => {
      this.maiorCategoria.set(cats.length ? cats[0].categoria : '—');
    });
    this.service.listar({ mesAno: mes, size: 1 }).subscribe((p) => {
      this.totalMes.set(p.totalElements);
      const ultimo = p.content[0];
      this.ultimoRegistro.set(ultimo ? this.formatarUltimo(ultimo) : '—');
    });
  }

  get mesAtualLabel(): string {
    return new Intl.DateTimeFormat('pt-BR', { month: 'long' }).format(new Date());
  }

  get tipoAtual() {
    return this.form.controls.tipo.value;
  }

  get mostrarCategoria() {
    return this.tipoAtual === 'GASTO';
  }

  setTipo(tipo: TipoLancamento) {
    this.form.patchValue({ tipo });
    if (tipo !== 'GASTO') this.form.patchValue({ categoria: '' });
    else if (!this.form.controls.categoria.value) {
      this.form.patchValue({ categoria: 'Alimentação' });
    }
  }

  invalido(name: string) {
    const c = this.form.get(name)!;
    return c.invalid && c.touched;
  }

  salvar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.sucesso.set(false);
    this.erro.set('');

    const v = this.form.getRawValue();
    const dto: LancamentoDTO = {
      tipo: v.tipo,
      descricao: v.descricao,
      valor: v.valor!,
      data: v.data,
      categoria: v.tipo === 'GASTO' ? v.categoria : null,
    };

    this.service.criar(dto).subscribe({
      next: () => {
        this.loading.set(false);
        this.sucesso.set(true);
        this.limpar();
        this.carregarResumo();
        setTimeout(() => this.router.navigate(['/historico']), 800);
      },
      error: () => {
        this.loading.set(false);
        this.erro.set('Não foi possível salvar o lançamento. Tente novamente.');
      },
    });
  }

  limpar() {
    this.form.reset({
      tipo: 'GASTO',
      valor: null,
      data: this.hoje(),
      descricao: '',
      categoria: 'Alimentação',
      pagamento: 'Débito',
    });
  }

  private formatarUltimo(l: { data: string; createdAt?: string }): string {
    const base = l.createdAt ?? `${l.data}T00:00:00`;
    const d = new Date(base);
    if (isNaN(d.getTime())) return '—';
    const hoje = new Date();
    const mesmoDia = d.toDateString() === hoje.toDateString();
    const hora = d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
    if (mesmoDia) return `hoje, ${hora}`;
    return `${d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' })}, ${hora}`;
  }

  private hoje() {
    return new Date().toISOString().split('T')[0];
  }
}
