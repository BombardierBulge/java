import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IDriver } from '../driver.model';
import { DriverService } from '../service/driver.service';

const driverResolve = (route: ActivatedRouteSnapshot): Observable<null | IDriver> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(DriverService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default driverResolve;
