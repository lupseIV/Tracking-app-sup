import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'today' },
  {
    path: 'today',
    loadComponent: () => import('./features/today/today.component').then((m) => m.TodayComponent),
  },
  {
    path: 'supplements',
    loadComponent: () =>
      import('./features/catalog/catalog.component').then((m) => m.CatalogComponent),
  },
  {
    path: 'supplements/:id',
    loadComponent: () =>
      import('./features/detail/detail.component').then((m) => m.DetailComponent),
  },
  {
    path: 'history',
    loadComponent: () =>
      import('./features/history/history.component').then((m) => m.HistoryComponent),
  },
  { path: '**', redirectTo: 'today' },
];
