package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.CarTestSamples.*;
import static com.mycompany.myapp.domain.DriverTestSamples.*;
import static com.mycompany.myapp.domain.RentalOfficeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DriverTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Driver.class);
        Driver driver1 = getDriverSample1();
        Driver driver2 = new Driver();
        assertThat(driver1).isNotEqualTo(driver2);

        driver2.setId(driver1.getId());
        assertThat(driver1).isEqualTo(driver2);

        driver2 = getDriverSample2();
        assertThat(driver1).isNotEqualTo(driver2);
    }

    @Test
    void currentCarTest() {
        Driver driver = getDriverRandomSampleGenerator();
        Car carBack = getCarRandomSampleGenerator();

        driver.setCurrentCar(carBack);
        assertThat(driver.getCurrentCar()).isEqualTo(carBack);

        driver.currentCar(null);
        assertThat(driver.getCurrentCar()).isNull();
    }

    @Test
    void rentalOfficeTest() {
        Driver driver = getDriverRandomSampleGenerator();
        RentalOffice rentalOfficeBack = getRentalOfficeRandomSampleGenerator();

        driver.addRentalOffice(rentalOfficeBack);
        assertThat(driver.getRentalOffices()).containsOnly(rentalOfficeBack);

        driver.removeRentalOffice(rentalOfficeBack);
        assertThat(driver.getRentalOffices()).doesNotContain(rentalOfficeBack);

        driver.rentalOffices(new HashSet<>(Set.of(rentalOfficeBack)));
        assertThat(driver.getRentalOffices()).containsOnly(rentalOfficeBack);

        driver.setRentalOffices(new HashSet<>());
        assertThat(driver.getRentalOffices()).doesNotContain(rentalOfficeBack);
    }
}
