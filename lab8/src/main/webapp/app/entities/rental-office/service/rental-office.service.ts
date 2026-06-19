import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IRentalOffice, NewRentalOffice } from '../rental-office.model';

export type PartialUpdateRentalOffice = Partial<IRentalOffice> & Pick<IRentalOffice, 'id'>;

@Injectable()
export class RentalOfficesService {
  readonly rentalOfficesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly rentalOfficesResource = httpResource<IRentalOffice[]>(() => {
    const params = this.rentalOfficesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of rentalOffice that have been fetched. It is updated when the rentalOfficesResource emits a new value.
   * In case of error while fetching the rentalOffices, the signal is set to an empty array.
   */
  readonly rentalOffices = computed(() => (this.rentalOfficesResource.hasValue() ? this.rentalOfficesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/rental-offices');
}

@Injectable({ providedIn: 'root' })
export class RentalOfficeService extends RentalOfficesService {
  protected readonly http = inject(HttpClient);

  create(rentalOffice: NewRentalOffice): Observable<IRentalOffice> {
    return this.http.post<IRentalOffice>(this.resourceUrl, rentalOffice);
  }

  update(rentalOffice: IRentalOffice): Observable<IRentalOffice> {
    return this.http.put<IRentalOffice>(
      `${this.resourceUrl}/${encodeURIComponent(this.getRentalOfficeIdentifier(rentalOffice))}`,
      rentalOffice,
    );
  }

  partialUpdate(rentalOffice: PartialUpdateRentalOffice): Observable<IRentalOffice> {
    return this.http.patch<IRentalOffice>(
      `${this.resourceUrl}/${encodeURIComponent(this.getRentalOfficeIdentifier(rentalOffice))}`,
      rentalOffice,
    );
  }

  find(id: number): Observable<IRentalOffice> {
    return this.http.get<IRentalOffice>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IRentalOffice[]>> {
    const options = createRequestOption(req);
    return this.http.get<IRentalOffice[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getRentalOfficeIdentifier(rentalOffice: Pick<IRentalOffice, 'id'>): number {
    return rentalOffice.id;
  }

  compareRentalOffice(o1: Pick<IRentalOffice, 'id'> | null, o2: Pick<IRentalOffice, 'id'> | null): boolean {
    return o1 && o2 ? this.getRentalOfficeIdentifier(o1) === this.getRentalOfficeIdentifier(o2) : o1 === o2;
  }

  addRentalOfficeToCollectionIfMissing<Type extends Pick<IRentalOffice, 'id'>>(
    rentalOfficeCollection: Type[],
    ...rentalOfficesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const rentalOffices: Type[] = rentalOfficesToCheck.filter(isPresent);
    if (rentalOffices.length > 0) {
      const rentalOfficeCollectionIdentifiers = rentalOfficeCollection.map(rentalOfficeItem =>
        this.getRentalOfficeIdentifier(rentalOfficeItem),
      );
      const rentalOfficesToAdd = rentalOffices.filter(rentalOfficeItem => {
        const rentalOfficeIdentifier = this.getRentalOfficeIdentifier(rentalOfficeItem);
        if (rentalOfficeCollectionIdentifiers.includes(rentalOfficeIdentifier)) {
          return false;
        }
        rentalOfficeCollectionIdentifiers.push(rentalOfficeIdentifier);
        return true;
      });
      return [...rentalOfficesToAdd, ...rentalOfficeCollection];
    }
    return rentalOfficeCollection;
  }
}
