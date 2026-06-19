package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Driver;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Driver entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DriverRepository extends ReactiveCrudRepository<Driver, Long>, DriverRepositoryInternal {
    Flux<Driver> findAllBy(Pageable pageable);

    @Override
    Mono<Driver> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Driver> findAllWithEagerRelationships();

    @Override
    Flux<Driver> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM driver entity WHERE entity.current_car_id = :id")
    Flux<Driver> findByCurrentCar(Long id);

    @Query("SELECT * FROM driver entity WHERE entity.current_car_id IS NULL")
    Flux<Driver> findAllWhereCurrentCarIsNull();

    @Query(
        "SELECT entity.* FROM driver entity JOIN rel_driver__rental_office joinTable ON entity.id = joinTable.rental_office_id WHERE joinTable.rental_office_id = :id"
    )
    Flux<Driver> findByRentalOffice(Long id);

    @Override
    <S extends Driver> Mono<S> save(S entity);

    @Override
    Flux<Driver> findAll();

    @Override
    Mono<Driver> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DriverRepositoryInternal {
    <S extends Driver> Mono<S> save(S entity);

    Flux<Driver> findAllBy(Pageable pageable);

    Flux<Driver> findAll();

    Mono<Driver> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Driver> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Driver> findOneWithEagerRelationships(Long id);

    Flux<Driver> findAllWithEagerRelationships();

    Flux<Driver> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
