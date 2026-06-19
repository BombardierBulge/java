package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Car.
 */
@Table("car")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Car implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("brand")
    private String brand;

    @NotNull
    @Column("model")
    private String model;

    @NotNull
    @Column("price_per_hour")
    private Float pricePerHour;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "cars", "drivers" }, allowSetters = true)
    private RentalOffice rentalOffice;

    @org.springframework.data.annotation.Transient
    private Driver driver;

    @Column("rental_office_id")
    private Long rentalOfficeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Car id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return this.brand;
    }

    public Car brand(String brand) {
        this.setBrand(brand);
        return this;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return this.model;
    }

    public Car model(String model) {
        this.setModel(model);
        return this;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Float getPricePerHour() {
        return this.pricePerHour;
    }

    public Car pricePerHour(Float pricePerHour) {
        this.setPricePerHour(pricePerHour);
        return this;
    }

    public void setPricePerHour(Float pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public RentalOffice getRentalOffice() {
        return this.rentalOffice;
    }

    public void setRentalOffice(RentalOffice rentalOffice) {
        this.rentalOffice = rentalOffice;
        this.rentalOfficeId = rentalOffice != null ? rentalOffice.getId() : null;
    }

    public Car rentalOffice(RentalOffice rentalOffice) {
        this.setRentalOffice(rentalOffice);
        return this;
    }

    public Driver getDriver() {
        return this.driver;
    }

    public void setDriver(Driver driver) {
        if (this.driver != null) {
            this.driver.setCurrentCar(null);
        }
        if (driver != null) {
            driver.setCurrentCar(this);
        }
        this.driver = driver;
    }

    public Car driver(Driver driver) {
        this.setDriver(driver);
        return this;
    }

    public Long getRentalOfficeId() {
        return this.rentalOfficeId;
    }

    public void setRentalOfficeId(Long rentalOffice) {
        this.rentalOfficeId = rentalOffice;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Car)) {
            return false;
        }
        return getId() != null && getId().equals(((Car) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Car{" +
            "id=" + getId() +
            ", brand='" + getBrand() + "'" +
            ", model='" + getModel() + "'" +
            ", pricePerHour=" + getPricePerHour() +
            "}";
    }
}
