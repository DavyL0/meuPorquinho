import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { ShellComponent } from './shell.component/shell.component';
import { LoginComponent } from './features/auth/login/login';
import { RegistroComponent } from './features/auth/registro/registro';
import { DashboardComponent } from './features/dashboard/dashboard';
import { LancamentoFormComponent } from './features/lancamentos/form/form';
import { HistoricoComponent } from './features/lancamentos/historico/historico';
import { RelatoriosComponent } from './features/relatorios/relatorios';
import { PerfilComponent } from './features/perfil/perfil';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'lancamentos', component: LancamentoFormComponent },
      { path: 'historico', component: HistoricoComponent },
      { path: 'relatorios', component: RelatoriosComponent },
      { path: 'perfil', component: PerfilComponent },
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
    ],
  },
  { path: '**', redirectTo: '' },
];
