package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Driver;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Driver}, with proper type conversions.
 */
@Service
public class DriverRowMapper implements BiFunction<Row, String, Driver> {

    private final ColumnConverter converter;

    public DriverRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Driver} stored in the database.
     */
    @Override
    public Driver apply(Row row, String prefix) {
        Driver entity = new Driver();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setLicenseDate(converter.fromRow(row, prefix + "_license_date", LocalDate.class));
        entity.setCurrentCarId(converter.fromRow(row, prefix + "_current_car_id", Long.class));
        return entity;
    }
}
