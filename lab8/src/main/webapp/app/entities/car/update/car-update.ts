import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IRentalOffice } from 'app/entities/rental-office/rental-office.model';
import { RentalOfficeService } from 'app/entities/rental-office/service/rental-office.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICar } from '../car.model';
import { CarService } from '../service/car.service';

import { CarFormGroup, CarFormService } from './car-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-car-update',
  templateUrl: './car-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CarUpdate implements OnInit {
  readonly isSaving = signal(false);
  car: ICar | null = null;

  rentalOfficesSharedCollection = signal<IRentalOffice[]>([]);

  protected carService = inject(CarService);
  protected carFormService = inject(CarFormService);
  protected rentalOfficeService = inject(RentalOfficeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CarFormGroup = this.carFormService.createCarFormGroup();

  compareRentalOffice = (o1: IRentalOffice | null, o2: IRentalOffice | null): boolean =>
    this.rentalOfficeService.compareRentalOffice(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ car }) => {
      this.car = car;
      if (car) {
        this.updateForm(car);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const car = this.carFormService.getCar(this.editForm);
    if (car.id === null) {
      this.subscribeToSaveResponse(this.carService.create(car));
    } else {
      this.subscribeToSaveResponse(this.carService.update(car));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICar | null>): void {
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

  protected updateForm(car: ICar): void {
    this.car = car;
    this.carFormService.resetForm(this.editForm, car);

    this.rentalOfficesSharedCollection.update(rentalOffices =>
      this.rentalOfficeService.addRentalOfficeToCollectionIfMissing<IRentalOffice>(rentalOffices, car.rentalOffice),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.rentalOfficeService
      .query()
      .pipe(map((res: HttpResponse<IRentalOffice[]>) => res.body ?? []))
      .pipe(
        map((rentalOffices: IRentalOffice[]) =>
          this.rentalOfficeService.addRentalOfficeToCollectionIfMissing<IRentalOffice>(rentalOffices, this.car?.rentalOffice),
        ),
      )
      .subscribe((rentalOffices: IRentalOffice[]) => this.rentalOfficesSharedCollection.set(rentalOffices));
  }
}
