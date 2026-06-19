import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import RentalOfficeResolve from './route/rental-office-routing-resolve.service';

const rentalOfficeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/rental-office').then(m => m.RentalOffice),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/rental-office-detail').then(m => m.RentalOfficeDetail),
    resolve: {
      rentalOffice: RentalOfficeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/rental-office-update').then(m => m.RentalOfficeUpdate),
    resolve: {
      rentalOffice: RentalOfficeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/rental-office-update').then(m => m.RentalOfficeUpdate),
    resolve: {
      rentalOffice: RentalOfficeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default rentalOfficeRoute;
