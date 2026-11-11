import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UsuarioService } from '../../core/services/usuario.service';
import {
  AtualizarPerfilRequest,
  UsuarioEstatistica,
  UsuarioPerfil,
} from '../../shared/models/usuario.model';

@Component({
  selector: 'app-perfil',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class PerfilComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userSvc = inject(UsuarioService);
  private authSvc = inject(AuthService);
  private router = inject(Router);

  usuario = signal<UsuarioPerfil | null>(null);
  estatistica = signal<UsuarioEstatistica | null>(null);
  sucesso = signal('');
  erro = signal('');

  formPerfil = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
  });

  formSenha = this.fb.nonNullable.group(
    {
      senhaAtual: ['', Validators.required],
      novaSenha: ['', [Validators.required, Validators.minLength(6)]],
      confirmar: ['', Validators.required],
    },
    { validators: this.senhasIguais },
  );

  ngOnInit() {
    this.userSvc.perfil().subscribe((u) => {
      this.usuario.set(u);
      this.formPerfil.patchValue({ nome: u.nome, email: u.email });
    });
    this.userSvc.estatisticas().subscribe((e) => this.estatistica.set(e));
  }

  salvarPerfil() {
    if (this.formPerfil.invalid) return;
    this.sucesso.set('');
    this.erro.set('');
    this.userSvc.atualizar(this.formPerfil.getRawValue() as AtualizarPerfilRequest).subscribe({
      next: (u) => {
        this.usuario.set(u);
        this.formPerfil.markAsPristine();
        this.sucesso.set('Perfil atualizado!');
      },
      error: () => this.erro.set('Erro ao atualizar perfil.'),
    });
  }

  alterarSenha() {
    if (this.formSenha.invalid) return;
    this.sucesso.set('');
    this.erro.set('');
    const { senhaAtual, novaSenha } = this.formSenha.getRawValue();
    this.userSvc.alterarSenha({ senhaAtual, novaSenha }).subscribe({
      next: () => {
        this.formSenha.reset();
        this.sucesso.set('Senha alterada!');
      },
      error: () => this.erro.set('Senha atual incorreta.'),
    });
  }

  excluirConta() {
    const senha = prompt('Confirme sua senha para excluir a conta:');
    if (!senha) return;
    this.userSvc.excluirConta(senha).subscribe({
      next: () => {
        this.authSvc.logout();
        this.router.navigate(['/login']);
      },
      error: () => this.erro.set('Senha incorreta.'),
    });
  }

  private senhasIguais(group: AbstractControl): ValidationErrors | null {
    const nova = group.get('novaSenha')?.value;
    const confirmar = group.get('confirmar')?.value;
    return nova === confirmar ? null : { senhasDiferentes: true };
  }
}
