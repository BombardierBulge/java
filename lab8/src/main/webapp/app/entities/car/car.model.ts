import { IRentalOffice } from 'app/entities/rental-office/rental-office.model';

export interface ICar {
  id: number;
  brand?: string | null;
  model?: string | null;
  pricePerHour?: number | null;
  rentalOffice?: IRentalOffice | null;
}

export type NewCar = Omit<ICar, 'id'> & { id: null };
