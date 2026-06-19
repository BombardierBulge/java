package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Car;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Car}, with proper type conversions.
 */
@Service
public class CarRowMapper implements BiFunction<Row, String, Car> {

    private final ColumnConverter converter;

    public CarRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Car} stored in the database.
     */
    @Override
    public Car apply(Row row, String prefix) {
        Car entity = new Car();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setBrand(converter.fromRow(row, prefix + "_brand", String.class));
        entity.setModel(converter.fromRow(row, prefix + "_model", String.class));
        entity.setPricePerHour(converter.fromRow(row, prefix + "_price_per_hour", Float.class));
        entity.setRentalOfficeId(converter.fromRow(row, prefix + "_rental_office_id", Long.class));
        return entity;
    }
}
