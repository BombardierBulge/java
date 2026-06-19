package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.OfficeStatus;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A RentalOffice.
 */
@Table("rental_office")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RentalOffice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("office_name")
    private String officeName;

    @NotNull
    @Column("city")
    private String city;

    @Column("status")
    private OfficeStatus status;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "rentalOffice", "driver" }, allowSetters = true)
    private Set<Car> cars = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "currentCar", "rentalOffices" }, allowSetters = true)
    private Set<Driver> drivers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RentalOffice id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOfficeName() {
        return this.officeName;
    }

    public RentalOffice officeName(String officeName) {
        this.setOfficeName(officeName);
        return this;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getCity() {
        return this.city;
    }

    public RentalOffice city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public OfficeStatus getStatus() {
        return this.status;
    }

    public RentalOffice status(OfficeStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OfficeStatus status) {
        this.status = status;
    }

    public Set<Car> getCars() {
        return this.cars;
    }

    public void setCars(Set<Car> cars) {
        if (this.cars != null) {
            this.cars.forEach(i -> i.setRentalOffice(null));
        }
        if (cars != null) {
            cars.forEach(i -> i.setRentalOffice(this));
        }
        this.cars = cars;
    }

    public RentalOffice cars(Set<Car> cars) {
        this.setCars(cars);
        return this;
    }

    public RentalOffice addCar(Car car) {
        this.cars.add(car);
        car.setRentalOffice(this);
        return this;
    }

    public RentalOffice removeCar(Car car) {
        this.cars.remove(car);
        car.setRentalOffice(null);
        return this;
    }

    public Set<Driver> getDrivers() {
        return this.drivers;
    }

    public void setDrivers(Set<Driver> drivers) {
        if (this.drivers != null) {
            this.drivers.forEach(i -> i.removeRentalOffice(this));
        }
        if (drivers != null) {
            drivers.forEach(i -> i.addRentalOffice(this));
        }
        this.drivers = drivers;
    }

    public RentalOffice drivers(Set<Driver> drivers) {
        this.setDrivers(drivers);
        return this;
    }

    public RentalOffice addDriver(Driver driver) {
        this.drivers.add(driver);
        driver.getRentalOffices().add(this);
        return this;
    }

    public RentalOffice removeDriver(Driver driver) {
        this.drivers.remove(driver);
        driver.getRentalOffices().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RentalOffice)) {
            return false;
        }
        return getId() != null && getId().equals(((RentalOffice) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RentalOffice{" +
            "id=" + getId() +
            ", officeName='" + getOfficeName() + "'" +
            ", city='" + getCity() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
