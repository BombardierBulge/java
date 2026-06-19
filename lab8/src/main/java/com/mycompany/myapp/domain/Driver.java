package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Driver.
 */
@Table("driver")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Driver implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull
    @Column("first_name")
    private String firstName;

    @NotNull
    @Column("last_name")
    private String lastName;

    @Column("license_date")
    private LocalDate licenseDate;

    @org.springframework.data.annotation.Transient
    private Car currentCar;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "cars", "drivers" }, allowSetters = true)
    private Set<RentalOffice> rentalOffices = new HashSet<>();

    @Column("current_car_id")
    private Long currentCarId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Driver id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Driver firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Driver lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getLicenseDate() {
        return this.licenseDate;
    }

    public Driver licenseDate(LocalDate licenseDate) {
        this.setLicenseDate(licenseDate);
        return this;
    }

    public void setLicenseDate(LocalDate licenseDate) {
        this.licenseDate = licenseDate;
    }

    public Car getCurrentCar() {
        return this.currentCar;
    }

    public void setCurrentCar(Car car) {
        this.currentCar = car;
        this.currentCarId = car != null ? car.getId() : null;
    }

    public Driver currentCar(Car car) {
        this.setCurrentCar(car);
        return this;
    }

    public Set<RentalOffice> getRentalOffices() {
        return this.rentalOffices;
    }

    public void setRentalOffices(Set<RentalOffice> rentalOffices) {
        this.rentalOffices = rentalOffices;
    }

    public Driver rentalOffices(Set<RentalOffice> rentalOffices) {
        this.setRentalOffices(rentalOffices);
        return this;
    }

    public Driver addRentalOffice(RentalOffice rentalOffice) {
        this.rentalOffices.add(rentalOffice);
        return this;
    }

    public Driver removeRentalOffice(RentalOffice rentalOffice) {
        this.rentalOffices.remove(rentalOffice);
        return this;
    }

    public Long getCurrentCarId() {
        return this.currentCarId;
    }

    public void setCurrentCarId(Long car) {
        this.currentCarId = car;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Driver)) {
            return false;
        }
        return getId() != null && getId().equals(((Driver) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Driver{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", licenseDate='" + getLicenseDate() + "'" +
            "}";
    }
}
