package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Driver;
import com.mycompany.myapp.repository.DriverRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Driver}.
 */
@RestController
@RequestMapping("/api/drivers")
@Transactional
public class DriverResource {

    private static final Logger LOG = LoggerFactory.getLogger(DriverResource.class);

    private static final String ENTITY_NAME = "driver";

    @Value("${jhipster.clientApp.name:lab8}")
    private String applicationName;

    private final DriverRepository driverRepository;

    public DriverResource(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    /**
     * {@code POST  /drivers} : Create a new driver.
     *
     * @param driver the driver to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new driver, or with status {@code 400 (Bad Request)} if the driver has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Driver>> createDriver(@Valid @RequestBody Driver driver) throws URISyntaxException {
        LOG.debug("REST request to save Driver : {}", driver);
        if (driver.getId() != null) {
            throw new BadRequestAlertException("A new driver cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return driverRepository.save(driver).map(result -> {
            try {
                return ResponseEntity.created(new URI("/api/drivers/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                    .body(result);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * {@code PUT  /drivers/:id} : Updates an existing driver.
     *
     * @param id the id of the driver to save.
     * @param driver the driver to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated driver,
     * or with status {@code 400 (Bad Request)} if the driver is not valid,
     * or with status {@code 500 (Internal Server Error)} if the driver couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Driver>> updateDriver(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Driver driver
    ) throws URISyntaxException {
        LOG.debug("REST request to update Driver : {}, {}", id, driver);
        if (driver.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, driver.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return driverRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            return driverRepository
                .save(driver)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(result ->
                    ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result)
                );
        });
    }

    /**
     * {@code PATCH  /drivers/:id} : Partial updates given fields of an existing driver, field will ignore if it is null
     *
     * @param id the id of the driver to save.
     * @param driver the driver to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated driver,
     * or with status {@code 400 (Bad Request)} if the driver is not valid,
     * or with status {@code 404 (Not Found)} if the driver is not found,
     * or with status {@code 500 (Internal Server Error)} if the driver couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Driver>> partialUpdateDriver(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Driver driver
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Driver partially : {}, {}", id, driver);
        if (driver.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, driver.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return driverRepository.existsById(id).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
            }

            Mono<Driver> result = driverRepository
                .findById(driver.getId())
                .map(existingDriver -> {
                    updateIfPresent(existingDriver::setFirstName, driver.getFirstName());
                    updateIfPresent(existingDriver::setLastName, driver.getLastName());
                    updateIfPresent(existingDriver::setLicenseDate, driver.getLicenseDate());

                    return existingDriver;
                })
                .flatMap(driverRepository::save);

            return result.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))).map(res ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                    .body(res)
            );
        });
    }

    /**
     * {@code GET  /drivers} : get all the Drivers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Drivers in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Driver>>> getAllDrivers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Drivers");
        return driverRepository
            .count()
            .zipWith(driverRepository.findAllBy(pageable).collectList())
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
     * {@code GET  /drivers/:id} : get the "id" driver.
     *
     * @param id the id of the driver to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the driver, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Driver>> getDriver(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Driver : {}", id);
        Mono<Driver> driver = driverRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(driver);
    }

    /**
     * {@code DELETE  /drivers/:id} : delete the "id" driver.
     *
     * @param id the id of the driver to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDriver(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Driver : {}", id);
        return driverRepository
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
