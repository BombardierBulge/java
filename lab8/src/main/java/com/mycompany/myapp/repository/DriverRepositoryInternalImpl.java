package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Driver;
import com.mycompany.myapp.domain.RentalOffice;
import com.mycompany.myapp.repository.rowmapper.CarRowMapper;
import com.mycompany.myapp.repository.rowmapper.DriverRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Driver entity.
 */
@SuppressWarnings("unused")
class DriverRepositoryInternalImpl extends SimpleR2dbcRepository<Driver, Long> implements DriverRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CarRowMapper carMapper;
    private final DriverRowMapper driverMapper;

    private static final Table entityTable = Table.aliased("driver", EntityManager.ENTITY_ALIAS);
    private static final Table currentCarTable = Table.aliased("car", "currentCar");

    private static final EntityManager.LinkTable rentalOfficeLink = new EntityManager.LinkTable(
        "rel_driver__rental_office",
        "driver_id",
        "rental_office_id"
    );

    public DriverRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CarRowMapper carMapper,
        DriverRowMapper driverMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Driver.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.carMapper = carMapper;
        this.driverMapper = driverMapper;
    }

    @Override
    public Flux<Driver> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Driver> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = DriverSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CarSqlHelper.getColumns(currentCarTable, "currentCar"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(currentCarTable)
            .on(Column.create("current_car_id", entityTable))
            .equals(Column.create("id", currentCarTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Driver.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Driver> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Driver> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Driver> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Driver> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Driver> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Driver process(Row row, RowMetadata metadata) {
        Driver entity = driverMapper.apply(row, "e");
        entity.setCurrentCar(carMapper.apply(row, "currentCar"));
        return entity;
    }

    @Override
    public <S extends Driver> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Driver> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(rentalOfficeLink, entity.getId(), entity.getRentalOffices().stream().map(RentalOffice::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(rentalOfficeLink, entityId);
    }
}
