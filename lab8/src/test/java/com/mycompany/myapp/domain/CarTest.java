package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CarTestSamples.*;
import static com.mycompany.myapp.domain.DriverTestSamples.*;
import static com.mycompany.myapp.domain.RentalOfficeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CarTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Car.class);
        Car car1 = getCarSample1();
        Car car2 = new Car();
        assertThat(car1).isNotEqualTo(car2);

        car2.setId(car1.getId());
        assertThat(car1).isEqualTo(car2);

        car2 = getCarSample2();
        assertThat(car1).isNotEqualTo(car2);
    }

    @Test
    void rentalOfficeTest() {
        Car car = getCarRandomSampleGenerator();
        RentalOffice rentalOfficeBack = getRentalOfficeRandomSampleGenerator();

        car.setRentalOffice(rentalOfficeBack);
        assertThat(car.getRentalOffice()).isEqualTo(rentalOfficeBack);

        car.rentalOffice(null);
        assertThat(car.getRentalOffice()).isNull();
    }

    @Test
    void driverTest() {
        Car car = getCarRandomSampleGenerator();
        Driver driverBack = getDriverRandomSampleGenerator();

        car.setDriver(driverBack);
        assertThat(car.getDriver()).isEqualTo(driverBack);
        assertThat(driverBack.getCurrentCar()).isEqualTo(car);

        car.driver(null);
        assertThat(car.getDriver()).isNull();
        assertThat(driverBack.getCurrentCar()).isNull();
    }
}
