package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.RentalOffice;
import com.mycompany.myapp.repository.RentalOfficeRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.RentalOffice}.
 */
@RestController
@RequestMapping("/api/rental-offices")
@Transactional
public class RentalOfficeResource {

    private static final Logger LOG = LoggerFactory.getLogger(RentalOfficeResource.class);

    private static final String ENTITY_NAME = "rentalOffice";

    @Value("${jhipster.clientApp.name:lab8}")
    private String applicationName;

    private final RentalOfficeRepository rentalOfficeRepository;

    public RentalOfficeResource(RentalOfficeRepository rentalOfficeRepository) {
        this.rentalOfficeRepository = rentalOfficeRepository;
    }

    /**
     * {@code POST  /rental-offices} : Create a new rentalOffice.
     *
     * @param rentalOffice the rentalOffice to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rentalOffice, or with status {@code 400 (Bad Request)} if the rentalOffice has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<RentalOffice>> createRentalOffice(@Valid @RequestBody RentalOffice rentalOffice) throws URISyntaxException {
        LOG.debug("REST request to save RentalOffice : {}", rentalOffice);
        if (rentalOffice.getId() != null) {
            throw new BadRequestAlertException("A new rentalOffice cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rentalOfficeRepository.save(rentalOffice).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/rental-offices/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /rental-offices/:id} : Updates an existing rentalOffice.
     *
     * @param id the id of the rentalOffice to save.
     * @param rentalOffice the rentalOffice to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rentalOffice,
     * or with status {@code 400 (Bad Request)} if the rentalOffice is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rentalOffice couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<RentalOffice>> updateRentalOffice(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RentalOffice rentalOffice
    ) throws URISyntaxException {
        LOG.debug("REST request to update RentalOffice : {}, {}", id, rentalOffice);
        if (rentalOffice.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rentalOffice.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rentalOfficeRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return rentalOfficeRepository
                .save(rentalOffice)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /rental-offices/:id} : Partial updates given fields of an existing rentalOffice, field will ignore if it is null
     *
     * @param id the id of the rentalOffice to save.
     * @param rentalOffice the rentalOffice to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rentalOffice,
     * or with status {@code 400 (Bad Request)} if the rentalOffice is not valid,
     * or with status {@code 404 (Not Found)} if the rentalOffice is not found,
     * or with status {@code 500 (Internal Server Error)} if the rentalOffice couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RentalOffice>> partialUpdateRentalOffice(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RentalOffice rentalOffice
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RentalOffice partially : {}, {}", id, rentalOffice);
        if (rentalOffice.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rentalOffice.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rentalOfficeRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<RentalOffice> result = rentalOfficeRepository
                .findById(rentalOffice.getId())
                .map(existingRentalOffice -> {
                    updateIfPresent(existingRentalOffice::setOfficeName, rentalOffice.getOfficeName());
                    updateIfPresent(existingRentalOffice::setCity, rentalOffice.getCity());
                    updateIfPresent(existingRentalOffice::setStatus, rentalOffice.getStatus());

                    return existingRentalOffice;
                })
                .flatMap(rentalOfficeRepository::save);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /rental-offices} : get all the Rental Offices.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Rental Offices in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<RentalOffice>>> getAllRentalOffices(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of RentalOffices");
        return rentalOfficeRepository
            .count()
            .zipWith(rentalOfficeRepository.findAllBy(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /rental-offices/:id} : get the "id" rentalOffice.
     *
     * @param id the id of the rentalOffice to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rentalOffice, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<RentalOffice>> getRentalOffice(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RentalOffice : {}", id);
        Mono<RentalOffice> rentalOffice = rentalOfficeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(rentalOffice);
    }

    /**
     * {@code DELETE  /rental-offices/:id} : delete the "id" rentalOffice.
     *
     * @param id the id of the rentalOffice to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRentalOffice(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RentalOffice : {}", id);
        return rentalOfficeRepository
            .deleteById(id)

            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
