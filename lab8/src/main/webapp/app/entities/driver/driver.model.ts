import dayjs from 'dayjs/esm';

import { ICar } from 'app/entities/car/car.model';
import { IRentalOffice } from 'app/entities/rental-office/rental-office.model';

export interface IDriver {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  licenseDate?: dayjs.Dayjs | null;
  currentCar?: ICar | null;
  rentalOffices?: IRentalOffice[] | null;
}

export type NewDriver = Omit<IDriver, 'id'> & { id: null };
