import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IDriver } from 'app/entities/driver/driver.model';
import { DriverService } from 'app/entities/driver/service/driver.service';
import { OfficeStatus } from 'app/entities/enumerations/office-status.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IRentalOffice } from '../rental-office.model';
import { RentalOfficeService } from '../service/rental-office.service';

import { RentalOfficeFormGroup, RentalOfficeFormService } from './rental-office-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-rental-office-update',
  templateUrl: './rental-office-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class RentalOfficeUpdate implements OnInit {
  readonly isSaving = signal(false);
  rentalOffice: IRentalOffice | null = null;
  officeStatusValues = Object.keys(OfficeStatus);

  driversSharedCollection = signal<IDriver[]>([]);

  protected rentalOfficeService = inject(RentalOfficeService);
  protected rentalOfficeFormService = inject(RentalOfficeFormService);
  protected driverService = inject(DriverService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RentalOfficeFormGroup = this.rentalOfficeFormService.createRentalOfficeFormGroup();

  compareDriver = (o1: IDriver | null, o2: IDriver | null): boolean => this.driverService.compareDriver(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rentalOffice }) => {
      this.rentalOffice = rentalOffice;
      if (rentalOffice) {
        this.updateForm(rentalOffice);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const rentalOffice = this.rentalOfficeFormService.getRentalOffice(this.editForm);
    if (rentalOffice.id === null) {
      this.subscribeToSaveResponse(this.rentalOfficeService.create(rentalOffice));
    } else {
      this.subscribeToSaveResponse(this.rentalOfficeService.update(rentalOffice));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IRentalOffice | null>): void {
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

  protected updateForm(rentalOffice: IRentalOffice): void {
    this.rentalOffice = rentalOffice;
    this.rentalOfficeFormService.resetForm(this.editForm, rentalOffice);

    this.driversSharedCollection.update(drivers =>
      this.driverService.addDriverToCollectionIfMissing<IDriver>(drivers, ...(rentalOffice.drivers ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.driverService
      .query()
      .pipe(map((res: HttpResponse<IDriver[]>) => res.body ?? []))
      .pipe(
        map((drivers: IDriver[]) =>
          this.driverService.addDriverToCollectionIfMissing<IDriver>(drivers, ...(this.rentalOffice?.drivers ?? [])),
        ),
      )
      .subscribe((drivers: IDriver[]) => this.driversSharedCollection.set(drivers));
  }
}
