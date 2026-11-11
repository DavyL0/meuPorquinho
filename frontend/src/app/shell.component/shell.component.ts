import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-shell',
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.css',
})
export class ShellComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  usuario = this.authService.getUsuario();

  navItems = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Novo lançamento', route: '/lancamentos' },
    { label: 'Histórico', route: '/historico' },
    { label: 'Relatórios', route: '/relatorios' },
    { label: 'Investimentos', route: '/investimentos' },
    { label: 'Perfil', route: '/perfil' },
  ];

  sair() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
