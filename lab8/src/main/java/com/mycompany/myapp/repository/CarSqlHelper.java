package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CarSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("brand", table, columnPrefix + "_brand"));
        columns.add(Column.aliased("model", table, columnPrefix + "_model"));
        columns.add(Column.aliased("price_per_hour", table, columnPrefix + "_price_per_hour"));

        columns.add(Column.aliased("rental_office_id", table, columnPrefix + "_rental_office_id"));
        return columns;
    }
}
