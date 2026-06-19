import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICar } from 'app/entities/car/car.model';
import { CarService } from 'app/entities/car/service/car.service';
import { IRentalOffice } from 'app/entities/rental-office/rental-office.model';
import { RentalOfficeService } from 'app/entities/rental-office/service/rental-office.service';
import { IDriver } from '../driver.model';
import { DriverService } from '../service/driver.service';

import { DriverFormService } from './driver-form.service';
import { DriverUpdate } from './driver-update';

describe('Driver Management Update Component', () => {
  let comp: DriverUpdate;
  let fixture: ComponentFixture<DriverUpdate>;
  let activatedRoute: ActivatedRoute;
  let driverFormService: DriverFormService;
  let driverService: DriverService;
  let carService: CarService;
  let rentalOfficeService: RentalOfficeService;

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

    fixture = TestBed.createComponent(DriverUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    driverFormService = TestBed.inject(DriverFormService);
    driverService = TestBed.inject(DriverService);
    carService = TestBed.inject(CarService);
    rentalOfficeService = TestBed.inject(RentalOfficeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call currentCar query and add missing value', () => {
      const driver: IDriver = { id: 7800 };
      const currentCar: ICar = { id: 30624 };
      driver.currentCar = currentCar;

      const currentCarCollection: ICar[] = [{ id: 30624 }];
      vitest.spyOn(carService, 'query').mockReturnValue(of(new HttpResponse({ body: currentCarCollection })));
      const expectedCollection: ICar[] = [currentCar, ...currentCarCollection];
      vitest.spyOn(carService, 'addCarToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ driver });
      comp.ngOnInit();

      expect(carService.query).toHaveBeenCalled();
      expect(carService.addCarToCollectionIfMissing).toHaveBeenCalledWith(currentCarCollection, currentCar);
      expect(comp.currentCarsCollection()).toEqual(expectedCollection);
    });

    it('should call RentalOffice query and add missing value', () => {
      const driver: IDriver = { id: 7800 };
      const rentalOffices: IRentalOffice[] = [{ id: 3069 }];
      driver.rentalOffices = rentalOffices;

      const rentalOfficeCollection: IRentalOffice[] = [{ id: 3069 }];
      vitest.spyOn(rentalOfficeService, 'query').mockReturnValue(of(new HttpResponse({ body: rentalOfficeCollection })));
      const additionalRentalOffices = [...rentalOffices];
      const expectedCollection: IRentalOffice[] = [...additionalRentalOffices, ...rentalOfficeCollection];
      vitest.spyOn(rentalOfficeService, 'addRentalOfficeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ driver });
      comp.ngOnInit();

      expect(rentalOfficeService.query).toHaveBeenCalled();
      expect(rentalOfficeService.addRentalOfficeToCollectionIfMissing).toHaveBeenCalledWith(
        rentalOfficeCollection,
        ...additionalRentalOffices.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.rentalOfficesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const driver: IDriver = { id: 7800 };
      const currentCar: ICar = { id: 30624 };
      driver.currentCar = currentCar;
      const rentalOffice: IRentalOffice = { id: 3069 };
      driver.rentalOffices = [rentalOffice];

      activatedRoute.data = of({ driver });
      comp.ngOnInit();

      expect(comp.currentCarsCollection()).toContainEqual(currentCar);
      expect(comp.rentalOfficesSharedCollection()).toContainEqual(rentalOffice);
      expect(comp.driver).toEqual(driver);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDriver>();
      const driver = { id: 27475 };
      vitest.spyOn(driverFormService, 'getDriver').mockReturnValue(driver);
      vitest.spyOn(driverService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ driver });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(driver);
      saveSubject.complete();

      // THEN
      expect(driverFormService.getDriver).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(driverService.update).toHaveBeenCalledWith(expect.objectContaining(driver));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDriver>();
      const driver = { id: 27475 };
      vitest.spyOn(driverFormService, 'getDriver').mockReturnValue({ id: null });
      vitest.spyOn(driverService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ driver: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(driver);
      saveSubject.complete();

      // THEN
      expect(driverFormService.getDriver).toHaveBeenCalled();
      expect(driverService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IDriver>();
      const driver = { id: 27475 };
      vitest.spyOn(driverService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ driver });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(driverService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCar', () => {
      it('should forward to carService', () => {
        const entity = { id: 30624 };
        const entity2 = { id: 14019 };
        vitest.spyOn(carService, 'compareCar');
        comp.compareCar(entity, entity2);
        expect(carService.compareCar).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareRentalOffice', () => {
      it('should forward to rentalOfficeService', () => {
        const entity = { id: 3069 };
        const entity2 = { id: 11405 };
        vitest.spyOn(rentalOfficeService, 'compareRentalOffice');
        comp.compareRentalOffice(entity, entity2);
        expect(rentalOfficeService.compareRentalOffice).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
