import { ICar, NewCar } from './car.model';

export const sampleWithRequiredData: ICar = {
  id: 4218,
  brand: 'dim yuck splendid',
  model: 'sans integer contrast',
  pricePerHour: 5067.89,
};

export const sampleWithPartialData: ICar = {
  id: 19509,
  brand: 'supportive',
  model: 'astride',
  pricePerHour: 10718.44,
};

export const sampleWithFullData: ICar = {
  id: 22796,
  brand: 'testimonial yahoo a',
  model: 'even parody',
  pricePerHour: 12743.15,
};

export const sampleWithNewData: NewCar = {
  brand: 'ick',
  model: 'consequently till ruin',
  pricePerHour: 5450.16,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
