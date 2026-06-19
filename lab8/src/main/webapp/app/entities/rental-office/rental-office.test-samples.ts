import { IRentalOffice, NewRentalOffice } from './rental-office.model';

export const sampleWithRequiredData: IRentalOffice = {
  id: 32346,
  officeName: 'yum hourly sun',
  city: 'Pittsfield',
};

export const sampleWithPartialData: IRentalOffice = {
  id: 28606,
  officeName: 'confused',
  city: 'Lynchburg',
};

export const sampleWithFullData: IRentalOffice = {
  id: 5464,
  officeName: 'below average',
  city: 'Fort Mattietown',
  status: 'MAINTENANCE',
};

export const sampleWithNewData: NewRentalOffice = {
  officeName: 'meh irritably',
  city: 'Rocky Mount',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
