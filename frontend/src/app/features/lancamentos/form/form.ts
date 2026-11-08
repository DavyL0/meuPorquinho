import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { inject } from 'vitest';
import { LancamentoService } from '../../../core/services/lancamento.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-lancamento-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './lancamento-form.component.html',
})
export class LancamentoFormComponent {
  private fb = inject(FormBuilder);
  private service = inject(LancamentoService);
  private router = inject(Router);

  form = this.fb.group({
    tipo: ['GASTO', Validators.required],
    valor: [null, [Validators.required, Validators.min(0.01)]],
    data: [this.hoje(), Validators.required],
    descricao: ['', [Validators.required, Validators.maxLength(200)]],
    categoria: ['Alimentação'],
    pagamento: ['Débito'],
    obs: [''],
  });

  get tipoAtual() {
    return this.form.get('tipo')!.value;
  }
  get mostrarCategoria() {
    return this.tipoAtual === 'GASTO';
  }

  setTipo(tipo: string) {
    this.form.patchValue({ tipo });
    if (tipo !== 'GASTO') this.form.patchValue({ categoria: null });
  }

  salvar() {
    if (this.form.invalid) return;
    this.service.criar(this.form.value as LancamentoDTO).subscribe({
      next: () => this.router.navigate(['/historico']),
      error: (err) => console.error(err),
    });
  }

  limpar() {
    this.form.reset({ tipo: 'GASTO', data: this.hoje() });
  }

  private hoje() {
    return new Date().toISOString().split('T')[0];
  }
}
