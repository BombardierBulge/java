import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'lab8App.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  {
    path: 'car',
    data: { pageTitle: 'lab8App.car.home.title' },
    loadChildren: () => import('./car/car.routes'),
  },
  {
    path: 'driver',
    data: { pageTitle: 'lab8App.driver.home.title' },
    loadChildren: () => import('./driver/driver.routes'),
  },
  {
    path: 'rental-office',
    data: { pageTitle: 'lab8App.rentalOffice.home.title' },
    loadChildren: () => import('./rental-office/rental-office.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
