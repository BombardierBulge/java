import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ICar } from 'app/entities/car/car.model';
import { CarService } from 'app/entities/car/service/car.service';
import { IRentalOffice } from 'app/entities/rental-office/rental-office.model';
import { RentalOfficeService } from 'app/entities/rental-office/service/rental-office.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IDriver } from '../driver.model';
import { DriverService } from '../service/driver.service';

import { DriverFormGroup, DriverFormService } from './driver-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-driver-update',
  templateUrl: './driver-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class DriverUpdate implements OnInit {
  readonly isSaving = signal(false);
  driver: IDriver | null = null;

  currentCarsCollection = signal<ICar[]>([]);
  rentalOfficesSharedCollection = signal<IRentalOffice[]>([]);

  protected driverService = inject(DriverService);
  protected driverFormService = inject(DriverFormService);
  protected carService = inject(CarService);
  protected rentalOfficeService = inject(RentalOfficeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DriverFormGroup = this.driverFormService.createDriverFormGroup();

  compareCar = (o1: ICar | null, o2: ICar | null): boolean => this.carService.compareCar(o1, o2);

  compareRentalOffice = (o1: IRentalOffice | null, o2: IRentalOffice | null): boolean =>
    this.rentalOfficeService.compareRentalOffice(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ driver }) => {
      this.driver = driver;
      if (driver) {
        this.updateForm(driver);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const driver = this.driverFormService.getDriver(this.editForm);
    if (driver.id === null) {
      this.subscribeToSaveResponse(this.driverService.create(driver));
    } else {
      this.subscribeToSaveResponse(this.driverService.update(driver));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IDriver | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(driver: IDriver): void {
    this.driver = driver;
    this.driverFormService.resetForm(this.editForm, driver);

    this.currentCarsCollection.set(this.carService.addCarToCollectionIfMissing<ICar>(this.currentCarsCollection(), driver.currentCar));
    this.rentalOfficesSharedCollection.update(rentalOffices =>
      this.rentalOfficeService.addRentalOfficeToCollectionIfMissing<IRentalOffice>(rentalOffices, ...(driver.rentalOffices ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.carService
      .query({ filter: 'driver-is-null' })
      .pipe(map((res: HttpResponse<ICar[]>) => res.body ?? []))
      .pipe(map((cars: ICar[]) => this.carService.addCarToCollectionIfMissing<ICar>(cars, this.driver?.currentCar)))
      .subscribe((cars: ICar[]) => this.currentCarsCollection.set(cars));

    this.rentalOfficeService
      .query()
      .pipe(map((res: HttpResponse<IRentalOffice[]>) => res.body ?? []))
      .pipe(
        map((rentalOffices: IRentalOffice[]) =>
          this.rentalOfficeService.addRentalOfficeToCollectionIfMissing<IRentalOffice>(
            rentalOffices,
            ...(this.driver?.rentalOffices ?? []),
          ),
        ),
      )
      .subscribe((rentalOffices: IRentalOffice[]) => this.rentalOfficesSharedCollection.set(rentalOffices));
  }
}
