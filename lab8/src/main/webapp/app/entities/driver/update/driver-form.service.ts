import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDriver, NewDriver } from '../driver.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDriver for edit and NewDriverFormGroupInput for create.
 */
type DriverFormGroupInput = IDriver | PartialWithRequiredKeyOf<NewDriver>;

type DriverFormDefaults = Pick<NewDriver, 'id' | 'rentalOffices'>;

type DriverFormGroupContent = {
  id: FormControl<IDriver['id'] | NewDriver['id']>;
  firstName: FormControl<IDriver['firstName']>;
  lastName: FormControl<IDriver['lastName']>;
  licenseDate: FormControl<IDriver['licenseDate']>;
  currentCar: FormControl<IDriver['currentCar']>;
  rentalOffices: FormControl<IDriver['rentalOffices']>;
};

export type DriverFormGroup = FormGroup<DriverFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DriverFormService {
  createDriverFormGroup(driver?: DriverFormGroupInput): DriverFormGroup {
    const driverRawValue = {
      ...this.getFormDefaults(),
      ...(driver ?? { id: null }),
    };
    return new FormGroup<DriverFormGroupContent>({
      id: new FormControl(
        { value: driverRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      firstName: new FormControl(driverRawValue.firstName, {
        validators: [Validators.required],
      }),
      lastName: new FormControl(driverRawValue.lastName, {
        validators: [Validators.required],
      }),
      licenseDate: new FormControl(driverRawValue.licenseDate),
      currentCar: new FormControl(driverRawValue.currentCar),
      rentalOffices: new FormControl(driverRawValue.rentalOffices ?? []),
    });
  }

  getDriver(form: DriverFormGroup): IDriver | NewDriver {
    return form.getRawValue();
  }

  resetForm(form: DriverFormGroup, driver: DriverFormGroupInput): void {
    const driverRawValue = { ...this.getFormDefaults(), ...driver };
    form.reset({
      ...driverRawValue,
      id: { value: driverRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): DriverFormDefaults {
    return {
      id: null,
      rentalOffices: [],
    };
  }
}
