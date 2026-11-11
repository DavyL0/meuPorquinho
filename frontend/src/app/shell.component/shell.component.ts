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
    { label: 'Dashboard', icon: '📊', route: '/dashboard' },
    { label: 'Novo lançamento', icon: '➕', route: '/lancamentos' },
    { label: 'Histórico', icon: '📋', route: '/historico' },
    { label: 'Relatórios', icon: '📈', route: '/relatorios' },
    { label: 'Investimentos', icon: '🪙', route: '/investimentos' },
    { label: 'Perfil', icon: '👤', route: '/perfil' },
  ];

  sair() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
