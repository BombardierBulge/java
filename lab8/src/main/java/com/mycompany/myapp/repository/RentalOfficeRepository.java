package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.RentalOffice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the RentalOffice entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RentalOfficeRepository extends ReactiveCrudRepository<RentalOffice, Long>, RentalOfficeRepositoryInternal {
    Flux<RentalOffice> findAllBy(Pageable pageable);

    @Override
    <S extends RentalOffice> Mono<S> save(S entity);

    @Override
    Flux<RentalOffice> findAll();

    @Override
    Mono<RentalOffice> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RentalOfficeRepositoryInternal {
    <S extends RentalOffice> Mono<S> save(S entity);

    Flux<RentalOffice> findAllBy(Pageable pageable);

    Flux<RentalOffice> findAll();

    Mono<RentalOffice> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<RentalOffice> findAllBy(Pageable pageable, Criteria criteria);
}
