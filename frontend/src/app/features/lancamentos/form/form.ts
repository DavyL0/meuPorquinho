import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LancamentoService } from '../../../core/services/lancamento.service';
import { LancamentoDTO, TipoLancamento } from '../../../shared/models/lancamento.model';

@Component({
  selector: 'app-lancamento-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './form.html',
  styleUrl: './form.css',
})
export class LancamentoFormComponent {
  private fb = inject(FormBuilder);
  private service = inject(LancamentoService);
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
    obs: [''],
  });

  sucesso = signal(false);
  erro = signal('');
  loading = signal(false);

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
      obs: '',
    });
  }

  private hoje() {
    return new Date().toISOString().split('T')[0];
  }
}
