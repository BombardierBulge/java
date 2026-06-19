import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IRentalOffice, NewRentalOffice } from '../rental-office.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRentalOffice for edit and NewRentalOfficeFormGroupInput for create.
 */
type RentalOfficeFormGroupInput = IRentalOffice | PartialWithRequiredKeyOf<NewRentalOffice>;

type RentalOfficeFormDefaults = Pick<NewRentalOffice, 'id' | 'drivers'>;

type RentalOfficeFormGroupContent = {
  id: FormControl<IRentalOffice['id'] | NewRentalOffice['id']>;
  officeName: FormControl<IRentalOffice['officeName']>;
  city: FormControl<IRentalOffice['city']>;
  status: FormControl<IRentalOffice['status']>;
  drivers: FormControl<IRentalOffice['drivers']>;
};

export type RentalOfficeFormGroup = FormGroup<RentalOfficeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RentalOfficeFormService {
  createRentalOfficeFormGroup(rentalOffice?: RentalOfficeFormGroupInput): RentalOfficeFormGroup {
    const rentalOfficeRawValue = {
      ...this.getFormDefaults(),
      ...(rentalOffice ?? { id: null }),
    };
    return new FormGroup<RentalOfficeFormGroupContent>({
      id: new FormControl(
        { value: rentalOfficeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      officeName: new FormControl(rentalOfficeRawValue.officeName, {
        validators: [Validators.required],
      }),
      city: new FormControl(rentalOfficeRawValue.city, {
        validators: [Validators.required],
      }),
      status: new FormControl(rentalOfficeRawValue.status),
      drivers: new FormControl(rentalOfficeRawValue.drivers ?? []),
    });
  }

  getRentalOffice(form: RentalOfficeFormGroup): IRentalOffice | NewRentalOffice {
    return form.getRawValue();
  }

  resetForm(form: RentalOfficeFormGroup, rentalOffice: RentalOfficeFormGroupInput): void {
    const rentalOfficeRawValue = { ...this.getFormDefaults(), ...rentalOffice };
    form.reset({
      ...rentalOfficeRawValue,
      id: { value: rentalOfficeRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): RentalOfficeFormDefaults {
    return {
      id: null,
      drivers: [],
    };
  }
}
