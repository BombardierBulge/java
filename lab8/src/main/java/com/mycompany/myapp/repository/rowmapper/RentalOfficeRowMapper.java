package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.RentalOffice;
import com.mycompany.myapp.domain.enumeration.OfficeStatus;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RentalOffice}, with proper type conversions.
 */
@Service
public class RentalOfficeRowMapper implements BiFunction<Row, String, RentalOffice> {

    private final ColumnConverter converter;

    public RentalOfficeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RentalOffice} stored in the database.
     */
    @Override
    public RentalOffice apply(Row row, String prefix) {
        RentalOffice entity = new RentalOffice();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setOfficeName(converter.fromRow(row, prefix + "_office_name", String.class));
        entity.setCity(converter.fromRow(row, prefix + "_city", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", OfficeStatus.class));
        return entity;
    }
}
