import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IDriver, NewDriver } from '../driver.model';

export type PartialUpdateDriver = Partial<IDriver> & Pick<IDriver, 'id'>;

type RestOf<T extends IDriver | NewDriver> = Omit<T, 'licenseDate'> & {
  licenseDate?: string | null;
};

export type RestDriver = RestOf<IDriver>;

export type NewRestDriver = RestOf<NewDriver>;

export type PartialUpdateRestDriver = RestOf<PartialUpdateDriver>;

@Injectable()
export class DriversService {
  readonly driversParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly driversResource = httpResource<RestDriver[]>(() => {
    const params = this.driversParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of driver that have been fetched. It is updated when the driversResource emits a new value.
   * In case of error while fetching the drivers, the signal is set to an empty array.
   */
  readonly drivers = computed(() =>
    (this.driversResource.hasValue() ? this.driversResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/drivers');

  protected convertValueFromServer(restDriver: RestDriver): IDriver {
    return {
      ...restDriver,
      licenseDate: restDriver.licenseDate ? dayjs(restDriver.licenseDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class DriverService extends DriversService {
  protected readonly http = inject(HttpClient);

  create(driver: NewDriver): Observable<IDriver> {
    const copy = this.convertValueFromClient(driver);
    return this.http.post<RestDriver>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(driver: IDriver): Observable<IDriver> {
    const copy = this.convertValueFromClient(driver);
    return this.http
      .put<RestDriver>(`${this.resourceUrl}/${encodeURIComponent(this.getDriverIdentifier(driver))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(driver: PartialUpdateDriver): Observable<IDriver> {
    const copy = this.convertValueFromClient(driver);
    return this.http
      .patch<RestDriver>(`${this.resourceUrl}/${encodeURIComponent(this.getDriverIdentifier(driver))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IDriver> {
    return this.http.get<RestDriver>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IDriver[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDriver[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getDriverIdentifier(driver: Pick<IDriver, 'id'>): number {
    return driver.id;
  }

  compareDriver(o1: Pick<IDriver, 'id'> | null, o2: Pick<IDriver, 'id'> | null): boolean {
    return o1 && o2 ? this.getDriverIdentifier(o1) === this.getDriverIdentifier(o2) : o1 === o2;
  }

  addDriverToCollectionIfMissing<Type extends Pick<IDriver, 'id'>>(
    driverCollection: Type[],
    ...driversToCheck: (Type | null | undefined)[]
  ): Type[] {
    const drivers: Type[] = driversToCheck.filter(isPresent);
    if (drivers.length > 0) {
      const driverCollectionIdentifiers = driverCollection.map(driverItem => this.getDriverIdentifier(driverItem));
      const driversToAdd = drivers.filter(driverItem => {
        const driverIdentifier = this.getDriverIdentifier(driverItem);
        if (driverCollectionIdentifiers.includes(driverIdentifier)) {
          return false;
        }
        driverCollectionIdentifiers.push(driverIdentifier);
        return true;
      });
      return [...driversToAdd, ...driverCollection];
    }
    return driverCollection;
  }

  protected convertValueFromClient<T extends IDriver | NewDriver | PartialUpdateDriver>(driver: T): RestOf<T> {
    return {
      ...driver,
      licenseDate: driver.licenseDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestDriver): IDriver {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestDriver[]): IDriver[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
