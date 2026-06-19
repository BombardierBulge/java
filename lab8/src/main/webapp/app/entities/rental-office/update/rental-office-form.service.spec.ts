import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../rental-office.test-samples';

import { RentalOfficeFormService } from './rental-office-form.service';

describe('RentalOffice Form Service', () => {
  let service: RentalOfficeFormService;

  beforeEach(() => {
    service = TestBed.inject(RentalOfficeFormService);
  });

  describe('Service methods', () => {
    describe('createRentalOfficeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRentalOfficeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            officeName: expect.any(Object),
            city: expect.any(Object),
            status: expect.any(Object),
            drivers: expect.any(Object),
          }),
        );
      });

      it('passing IRentalOffice should create a new form with FormGroup', () => {
        const formGroup = service.createRentalOfficeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            officeName: expect.any(Object),
            city: expect.any(Object),
            status: expect.any(Object),
            drivers: expect.any(Object),
          }),
        );
      });
    });

    describe('getRentalOffice', () => {
      it('should return NewRentalOffice for default RentalOffice initial value', () => {
        const formGroup = service.createRentalOfficeFormGroup(sampleWithNewData);

        const rentalOffice = service.getRentalOffice(formGroup);

        expect(rentalOffice).toMatchObject(sampleWithNewData);
      });

      it('should return NewRentalOffice for empty RentalOffice initial value', () => {
        const formGroup = service.createRentalOfficeFormGroup();

        const rentalOffice = service.getRentalOffice(formGroup);

        expect(rentalOffice).toMatchObject({});
      });

      it('should return IRentalOffice', () => {
        const formGroup = service.createRentalOfficeFormGroup(sampleWithRequiredData);

        const rentalOffice = service.getRentalOffice(formGroup);

        expect(rentalOffice).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRentalOffice should not enable id FormControl', () => {
        const formGroup = service.createRentalOfficeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRentalOffice should disable id FormControl', () => {
        const formGroup = service.createRentalOfficeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
