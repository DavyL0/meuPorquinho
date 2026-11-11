import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-registro',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class RegistroComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(6)]],
  });

  erro = signal('');
  loading = signal(false);
  senhaVisivel = signal(false);

  registrar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.erro.set('');

    const { nome, email, senha } = this.form.getRawValue();
    this.auth.registrar(nome, email, senha).subscribe({
      next: () => this.router.navigate(['/login']),
      error: (err) => {
        this.erro.set(
          err?.status === 409
            ? 'Este e-mail já está cadastrado.'
            : 'Não foi possível criar a conta. Tente novamente.',
        );
        this.loading.set(false);
      },
    });
  }

  campo(name: string) {
    return this.form.get(name)!;
  }

  invalido(name: string) {
    const c = this.campo(name);
    return c.invalid && c.touched;
  }
}
