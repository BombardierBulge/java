import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IRentalOffice } from '../rental-office.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../rental-office.test-samples';

import { RentalOfficeService } from './rental-office.service';

const requireRestSample: IRentalOffice = {
  ...sampleWithRequiredData,
};

describe('RentalOffice Service', () => {
  let service: RentalOfficeService;
  let httpMock: HttpTestingController;
  let expectedResult: IRentalOffice | IRentalOffice[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(RentalOfficeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a RentalOffice', () => {
      const rentalOffice = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(rentalOffice).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RentalOffice', () => {
      const rentalOffice = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(rentalOffice).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RentalOffice', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RentalOffice', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RentalOffice', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addRentalOfficeToCollectionIfMissing', () => {
      it('should add a RentalOffice to an empty array', () => {
        const rentalOffice: IRentalOffice = sampleWithRequiredData;
        expectedResult = service.addRentalOfficeToCollectionIfMissing([], rentalOffice);
        expect(expectedResult).toEqual([rentalOffice]);
      });

      it('should not add a RentalOffice to an array that contains it', () => {
        const rentalOffice: IRentalOffice = sampleWithRequiredData;
        const rentalOfficeCollection: IRentalOffice[] = [
          {
            ...rentalOffice,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRentalOfficeToCollectionIfMissing(rentalOfficeCollection, rentalOffice);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RentalOffice to an array that doesn't contain it", () => {
        const rentalOffice: IRentalOffice = sampleWithRequiredData;
        const rentalOfficeCollection: IRentalOffice[] = [sampleWithPartialData];
        expectedResult = service.addRentalOfficeToCollectionIfMissing(rentalOfficeCollection, rentalOffice);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(rentalOffice);
      });

      it('should add only unique RentalOffice to an array', () => {
        const rentalOfficeArray: IRentalOffice[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const rentalOfficeCollection: IRentalOffice[] = [sampleWithRequiredData];
        expectedResult = service.addRentalOfficeToCollectionIfMissing(rentalOfficeCollection, ...rentalOfficeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const rentalOffice: IRentalOffice = sampleWithRequiredData;
        const rentalOffice2: IRentalOffice = sampleWithPartialData;
        expectedResult = service.addRentalOfficeToCollectionIfMissing([], rentalOffice, rentalOffice2);
        expect(expectedResult).toEqual([rentalOffice, rentalOffice2]);
      });

      it('should accept null and undefined values', () => {
        const rentalOffice: IRentalOffice = sampleWithRequiredData;
        expectedResult = service.addRentalOfficeToCollectionIfMissing([], null, rentalOffice, undefined);
        expect(expectedResult).toEqual([rentalOffice]);
      });

      it('should return initial array if no RentalOffice is added', () => {
        const rentalOfficeCollection: IRentalOffice[] = [sampleWithRequiredData];
        expectedResult = service.addRentalOfficeToCollectionIfMissing(rentalOfficeCollection, undefined, null);
        expect(expectedResult).toEqual(rentalOfficeCollection);
      });
    });

    describe('compareRentalOffice', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRentalOffice(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 3069 };
        const entity2 = null;

        const compareResult1 = service.compareRentalOffice(entity1, entity2);
        const compareResult2 = service.compareRentalOffice(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 3069 };
        const entity2 = { id: 11405 };

        const compareResult1 = service.compareRentalOffice(entity1, entity2);
        const compareResult2 = service.compareRentalOffice(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 3069 };
        const entity2 = { id: 3069 };

        const compareResult1 = service.compareRentalOffice(entity1, entity2);
        const compareResult2 = service.compareRentalOffice(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
