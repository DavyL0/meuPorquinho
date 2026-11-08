import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { inject } from 'vitest';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(6)]],
  });

  erro = signal('');
  loading = signal(false);
  senhaVisivel = signal(false);

  entrar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.erro.set('');

    const { email, senha } = this.form.value;
    this.auth.login(email!, senha!).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () => {
        this.erro.set('E-mail ou senha inválidos.');
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
