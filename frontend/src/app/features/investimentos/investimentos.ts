import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { InvestimentoService } from '../../core/services/investimento.service';
import {
  DirecaoAlerta,
  Investimento,
  InvestimentoRequest,
} from '../../shared/models/investimento.model';

@Component({
  selector: 'app-investimentos',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './investimentos.html',
  styleUrl: './investimentos.css',
})
export class InvestimentosComponent implements OnInit {
  private fb = inject(FormBuilder);
  private service = inject(InvestimentoService);

  readonly simbolos = [
    'BTCBRL',
    'ETHBRL',
    'SOLBRL',
    'BNBBRL',
    'XRPBRL',
    'ADABRL',
    'DOGEBRL',
    'LINKBRL',
    'USDTBRL',
  ];

  investimentos = signal<Investimento[]>([]);
  loading = signal(false);
  salvando = signal(false);
  erro = signal('');
  sucesso = signal('');

  form = this.fb.nonNullable.group({
    simbolo: ['BTCBRL', Validators.required],
    quantidade: [null as number | null, [Validators.required, Validators.min(0.00000001)]],
    valorInicial: [null as number | null, [Validators.required, Validators.min(0.00000001)]],
    percentualAlerta: [10, [Validators.required, Validators.min(0.01), Validators.max(1000)]],
    direcao: ['AMBOS' as DirecaoAlerta, Validators.required],
  });

  ngOnInit() {
    this.carregar();
  }

  carregar() {
    this.loading.set(true);
    this.service.listar().subscribe({
      next: (lista) => {
        this.investimentos.set(lista);
        this.loading.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar os investimentos.');
        this.loading.set(false);
      },
    });
  }

  salvar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.salvando.set(true);
    this.erro.set('');
    this.sucesso.set('');

    const req = this.form.getRawValue() as InvestimentoRequest;
    this.service.criar(req).subscribe({
      next: () => {
        this.salvando.set(false);
        this.sucesso.set('Investimento cadastrado! O alerta está ativo.');
        this.form.reset({
          simbolo: 'BTCBRL',
          quantidade: null,
          valorInicial: null,
          percentualAlerta: 10,
          direcao: 'AMBOS',
        });
        this.carregar();
      },
      error: (err) => {
        this.salvando.set(false);
        this.erro.set(err?.error?.mensagem ?? 'Não foi possível cadastrar.');
      },
    });
  }

  reativar(inv: Investimento) {
    this.service.reativar(inv.id).subscribe({
      next: () => this.carregar(),
      error: () => this.erro.set('Não foi possível reativar o alerta.'),
    });
  }

  remover(inv: Investimento) {
    if (!confirm(`Remover o investimento em ${inv.simbolo}?`)) return;
    this.service.remover(inv.id).subscribe({
      next: () => this.carregar(),
      error: () => this.erro.set('Não foi possível remover.'),
    });
  }

  invalido(name: string) {
    const c = this.form.get(name)!;
    return c.invalid && c.touched;
  }

  classeVariacao(v: number | null): string {
    if (v == null) return '';
    return v > 0 ? 'pos' : v < 0 ? 'neg' : 'neu';
  }

  descricaoDirecao(d: DirecaoAlerta): string {
    return d === 'ALTA' ? '📈 Alta' : d === 'QUEDA' ? '📉 Queda' : '⚡ Alta ou queda';
  }
}
