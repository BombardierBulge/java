import { IDriver } from 'app/entities/driver/driver.model';
import { OfficeStatus } from 'app/entities/enumerations/office-status.model';

export interface IRentalOffice {
  id: number;
  officeName?: string | null;
  city?: string | null;
  status?: keyof typeof OfficeStatus | null;
  drivers?: IDriver[] | null;
}

export type NewRentalOffice = Omit<IRentalOffice, 'id'> & { id: null };
