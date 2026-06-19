package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CarTestSamples.*;
import static com.mycompany.myapp.domain.DriverTestSamples.*;
import static com.mycompany.myapp.domain.RentalOfficeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RentalOfficeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RentalOffice.class);
        RentalOffice rentalOffice1 = getRentalOfficeSample1();
        RentalOffice rentalOffice2 = new RentalOffice();
        assertThat(rentalOffice1).isNotEqualTo(rentalOffice2);

        rentalOffice2.setId(rentalOffice1.getId());
        assertThat(rentalOffice1).isEqualTo(rentalOffice2);

        rentalOffice2 = getRentalOfficeSample2();
        assertThat(rentalOffice1).isNotEqualTo(rentalOffice2);
    }

    @Test
    void carTest() {
        RentalOffice rentalOffice = getRentalOfficeRandomSampleGenerator();
        Car carBack = getCarRandomSampleGenerator();

        rentalOffice.addCar(carBack);
        assertThat(rentalOffice.getCars()).containsOnly(carBack);
        assertThat(carBack.getRentalOffice()).isEqualTo(rentalOffice);

        rentalOffice.removeCar(carBack);
        assertThat(rentalOffice.getCars()).doesNotContain(carBack);
        assertThat(carBack.getRentalOffice()).isNull();

        rentalOffice.cars(new HashSet<>(Set.of(carBack)));
        assertThat(rentalOffice.getCars()).containsOnly(carBack);
        assertThat(carBack.getRentalOffice()).isEqualTo(rentalOffice);

        rentalOffice.setCars(new HashSet<>());
        assertThat(rentalOffice.getCars()).doesNotContain(carBack);
        assertThat(carBack.getRentalOffice()).isNull();
    }

    @Test
    void driverTest() {
        RentalOffice rentalOffice = getRentalOfficeRandomSampleGenerator();
        Driver driverBack = getDriverRandomSampleGenerator();

        rentalOffice.addDriver(driverBack);
        assertThat(rentalOffice.getDrivers()).containsOnly(driverBack);
        assertThat(driverBack.getRentalOffices()).containsOnly(rentalOffice);

        rentalOffice.removeDriver(driverBack);
        assertThat(rentalOffice.getDrivers()).doesNotContain(driverBack);
        assertThat(driverBack.getRentalOffices()).doesNotContain(rentalOffice);

        rentalOffice.drivers(new HashSet<>(Set.of(driverBack)));
        assertThat(rentalOffice.getDrivers()).containsOnly(driverBack);
        assertThat(driverBack.getRentalOffices()).containsOnly(rentalOffice);

        rentalOffice.setDrivers(new HashSet<>());
        assertThat(rentalOffice.getDrivers()).doesNotContain(driverBack);
        assertThat(driverBack.getRentalOffices()).doesNotContain(rentalOffice);
    }
}
