import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import DriverResolve from './route/driver-routing-resolve.service';

const driverRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/driver').then(m => m.Driver),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/driver-detail').then(m => m.DriverDetail),
    resolve: {
      driver: DriverResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/driver-update').then(m => m.DriverUpdate),
    resolve: {
      driver: DriverResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/driver-update').then(m => m.DriverUpdate),
    resolve: {
      driver: DriverResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default driverRoute;
