import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IRentalOffice } from 'app/entities/rental-office/rental-office.model';
import { RentalOfficeService } from 'app/entities/rental-office/service/rental-office.service';
import { ICar } from '../car.model';
import { CarService } from '../service/car.service';

import { CarFormService } from './car-form.service';
import { CarUpdate } from './car-update';

describe('Car Management Update Component', () => {
  let comp: CarUpdate;
  let fixture: ComponentFixture<CarUpdate>;
  let activatedRoute: ActivatedRoute;
  let carFormService: CarFormService;
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

    fixture = TestBed.createComponent(CarUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    carFormService = TestBed.inject(CarFormService);
    carService = TestBed.inject(CarService);
    rentalOfficeService = TestBed.inject(RentalOfficeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call RentalOffice query and add missing value', () => {
      const car: ICar = { id: 14019 };
      const rentalOffice: IRentalOffice = { id: 3069 };
      car.rentalOffice = rentalOffice;

      const rentalOfficeCollection: IRentalOffice[] = [{ id: 3069 }];
      vitest.spyOn(rentalOfficeService, 'query').mockReturnValue(of(new HttpResponse({ body: rentalOfficeCollection })));
      const additionalRentalOffices = [rentalOffice];
      const expectedCollection: IRentalOffice[] = [...additionalRentalOffices, ...rentalOfficeCollection];
      vitest.spyOn(rentalOfficeService, 'addRentalOfficeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ car });
      comp.ngOnInit();

      expect(rentalOfficeService.query).toHaveBeenCalled();
      expect(rentalOfficeService.addRentalOfficeToCollectionIfMissing).toHaveBeenCalledWith(
        rentalOfficeCollection,
        ...additionalRentalOffices.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.rentalOfficesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const car: ICar = { id: 14019 };
      const rentalOffice: IRentalOffice = { id: 3069 };
      car.rentalOffice = rentalOffice;

      activatedRoute.data = of({ car });
      comp.ngOnInit();

      expect(comp.rentalOfficesSharedCollection()).toContainEqual(rentalOffice);
      expect(comp.car).toEqual(car);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICar>();
      const car = { id: 30624 };
      vitest.spyOn(carFormService, 'getCar').mockReturnValue(car);
      vitest.spyOn(carService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ car });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(car);
      saveSubject.complete();

      // THEN
      expect(carFormService.getCar).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(carService.update).toHaveBeenCalledWith(expect.objectContaining(car));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICar>();
      const car = { id: 30624 };
      vitest.spyOn(carFormService, 'getCar').mockReturnValue({ id: null });
      vitest.spyOn(carService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ car: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(car);
      saveSubject.complete();

      // THEN
      expect(carFormService.getCar).toHaveBeenCalled();
      expect(carService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICar>();
      const car = { id: 30624 };
      vitest.spyOn(carService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ car });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(carService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
