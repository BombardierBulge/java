import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IDriver } from 'app/entities/driver/driver.model';
import { DriverService } from 'app/entities/driver/service/driver.service';
import { IRentalOffice } from '../rental-office.model';
import { RentalOfficeService } from '../service/rental-office.service';

import { RentalOfficeFormService } from './rental-office-form.service';
import { RentalOfficeUpdate } from './rental-office-update';

describe('RentalOffice Management Update Component', () => {
  let comp: RentalOfficeUpdate;
  let fixture: ComponentFixture<RentalOfficeUpdate>;
  let activatedRoute: ActivatedRoute;
  let rentalOfficeFormService: RentalOfficeFormService;
  let rentalOfficeService: RentalOfficeService;
  let driverService: DriverService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(RentalOfficeUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    rentalOfficeFormService = TestBed.inject(RentalOfficeFormService);
    rentalOfficeService = TestBed.inject(RentalOfficeService);
    driverService = TestBed.inject(DriverService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Driver query and add missing value', () => {
      const rentalOffice: IRentalOffice = { id: 11405 };
      const drivers: IDriver[] = [{ id: 27475 }];
      rentalOffice.drivers = drivers;

      const driverCollection: IDriver[] = [{ id: 27475 }];
      vitest.spyOn(driverService, 'query').mockReturnValue(of(new HttpResponse({ body: driverCollection })));
      const additionalDrivers = [...drivers];
      const expectedCollection: IDriver[] = [...additionalDrivers, ...driverCollection];
      vitest.spyOn(driverService, 'addDriverToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rentalOffice });
      comp.ngOnInit();

      expect(driverService.query).toHaveBeenCalled();
      expect(driverService.addDriverToCollectionIfMissing).toHaveBeenCalledWith(
        driverCollection,
        ...additionalDrivers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.driversSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const rentalOffice: IRentalOffice = { id: 11405 };
      const driver: IDriver = { id: 27475 };
      rentalOffice.drivers = [driver];

      activatedRoute.data = of({ rentalOffice });
      comp.ngOnInit();

      expect(comp.driversSharedCollection()).toContainEqual(driver);
      expect(comp.rentalOffice).toEqual(rentalOffice);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRentalOffice>();
      const rentalOffice = { id: 3069 };
      vitest.spyOn(rentalOfficeFormService, 'getRentalOffice').mockReturnValue(rentalOffice);
      vitest.spyOn(rentalOfficeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rentalOffice });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(rentalOffice);
      saveSubject.complete();

      // THEN
      expect(rentalOfficeFormService.getRentalOffice).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(rentalOfficeService.update).toHaveBeenCalledWith(expect.objectContaining(rentalOffice));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRentalOffice>();
      const rentalOffice = { id: 3069 };
      vitest.spyOn(rentalOfficeFormService, 'getRentalOffice').mockReturnValue({ id: null });
      vitest.spyOn(rentalOfficeService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rentalOffice: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(rentalOffice);
      saveSubject.complete();

      // THEN
      expect(rentalOfficeFormService.getRentalOffice).toHaveBeenCalled();
      expect(rentalOfficeService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IRentalOffice>();
      const rentalOffice = { id: 3069 };
      vitest.spyOn(rentalOfficeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rentalOffice });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(rentalOfficeService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDriver', () => {
      it('should forward to driverService', () => {
        const entity = { id: 27475 };
        const entity2 = { id: 7800 };
        vitest.spyOn(driverService, 'compareDriver');
        comp.compareDriver(entity, entity2);
        expect(driverService.compareDriver).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
