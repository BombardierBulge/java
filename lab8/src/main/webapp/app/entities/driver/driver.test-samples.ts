import dayjs from 'dayjs/esm';

import { IDriver, NewDriver } from './driver.model';

export const sampleWithRequiredData: IDriver = {
  id: 24659,
  firstName: 'Orin',
  lastName: 'Pacocha',
};

export const sampleWithPartialData: IDriver = {
  id: 3846,
  firstName: 'Christelle',
  lastName: 'Sawayn',
  licenseDate: dayjs('2026-06-11'),
};

export const sampleWithFullData: IDriver = {
  id: 28897,
  firstName: 'Fay',
  lastName: 'Oberbrunner',
  licenseDate: dayjs('2026-06-11'),
};

export const sampleWithNewData: NewDriver = {
  firstName: 'Lewis',
  lastName: 'Ankunding',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
