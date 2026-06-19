import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../driver.test-samples';

import { DriverFormService } from './driver-form.service';

describe('Driver Form Service', () => {
  let service: DriverFormService;

  beforeEach(() => {
    service = TestBed.inject(DriverFormService);
  });

  describe('Service methods', () => {
    describe('createDriverFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDriverFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            licenseDate: expect.any(Object),
            currentCar: expect.any(Object),
            rentalOffices: expect.any(Object),
          }),
        );
      });

      it('passing IDriver should create a new form with FormGroup', () => {
        const formGroup = service.createDriverFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            licenseDate: expect.any(Object),
            currentCar: expect.any(Object),
            rentalOffices: expect.any(Object),
          }),
        );
      });
    });

    describe('getDriver', () => {
      it('should return NewDriver for default Driver initial value', () => {
        const formGroup = service.createDriverFormGroup(sampleWithNewData);

        const driver = service.getDriver(formGroup);

        expect(driver).toMatchObject(sampleWithNewData);
      });

      it('should return NewDriver for empty Driver initial value', () => {
        const formGroup = service.createDriverFormGroup();

        const driver = service.getDriver(formGroup);

        expect(driver).toMatchObject({});
      });

      it('should return IDriver', () => {
        const formGroup = service.createDriverFormGroup(sampleWithRequiredData);

        const driver = service.getDriver(formGroup);

        expect(driver).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDriver should not enable id FormControl', () => {
        const formGroup = service.createDriverFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDriver should disable id FormControl', () => {
        const formGroup = service.createDriverFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
