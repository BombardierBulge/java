package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.DriverAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Driver;
import com.mycompany.myapp.repository.DriverRepository;
import com.mycompany.myapp.repository.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link DriverResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DriverResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_LICENSE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LICENSE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/drivers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DriverRepository driverRepository;

    @Mock
    private DriverRepository driverRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Driver driver;

    private Driver insertedDriver;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Driver createEntity() {
        return new Driver().firstName(DEFAULT_FIRST_NAME).lastName(DEFAULT_LAST_NAME).licenseDate(DEFAULT_LICENSE_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Driver createUpdatedEntity() {
        return new Driver().firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).licenseDate(UPDATED_LICENSE_DATE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_driver__rental_office").block();
            em.deleteAll(Driver.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        driver = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDriver != null) {
            driverRepository.delete(insertedDriver).block();
            insertedDriver = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDriver() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Driver
        var returnedDriver = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(driver))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Driver.class)
            .returnResult()
            .getResponseBody();

        // Validate the Driver in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDriverUpdatableFieldsEquals(returnedDriver, getPersistedDriver(returnedDriver));

        insertedDriver = returnedDriver;
    }

    @Test
    void createDriverWithExistingId() throws Exception {
        // Create the Driver with an existing ID
        driver.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(driver))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        driver.setFirstName(null);

        // Create the Driver, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(driver))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        driver.setLastName(null);

        // Create the Driver, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(driver))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDrivers() {
        // Initialize the database
        insertedDriver = driverRepository.save(driver).block();

        // Get all the driverList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(driver.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].licenseDate")
            .value(hasItem(DEFAULT_LICENSE_DATE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDriversWithEagerRelationshipsIsEnabled() {
        when(driverRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=true")
            .exchange()
            .expectStatus()
            .isOk();

        verify(driverRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDriversWithEagerRelationshipsIsNotEnabled() {
        when(driverRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?eagerload=false")
            .exchange()
            .expectStatus()
            .isOk();
        verify(driverRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getDriver() {
        // Initialize the database
        insertedDriver = driverRepository.save(driver).block();

        // Get the driver
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, driver.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(driver.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.licenseDate")
            .value(is(DEFAULT_LICENSE_DATE.toString()));
    }

    @Test
    void getNonExistingDriver() {
        // Get the driver
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDriver() throws Exception {
        // Initialize the database
        insertedDriver = driverRepository.save(driver).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the driver
        Driver updatedDriver = driverRepository.findById(driver.getId()).block();
        updatedDriver.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).licenseDate(UPDATED_LICENSE_DATE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedDriver.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedDriver))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDriverToMatchAllProperties(updatedDriver);
    }

    @Test
    void putNonExistingDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        driver.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, driver.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(driver))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        driver.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(driver))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        driver.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(driver))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDriverWithPatch() throws Exception {
        // Initialize the database
        insertedDriver = driverRepository.save(driver).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the driver using partial update
        Driver partialUpdatedDriver = new Driver();
        partialUpdatedDriver.setId(driver.getId());

        partialUpdatedDriver.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).licenseDate(UPDATED_LICENSE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDriver.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDriver))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Driver in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDriverUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDriver, driver), getPersistedDriver(driver));
    }

    @Test
    void fullUpdateDriverWithPatch() throws Exception {
        // Initialize the database
        insertedDriver = driverRepository.save(driver).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the driver using partial update
        Driver partialUpdatedDriver = new Driver();
        partialUpdatedDriver.setId(driver.getId());

        partialUpdatedDriver.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME).licenseDate(UPDATED_LICENSE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDriver.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDriver))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Driver in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDriverUpdatableFieldsEquals(partialUpdatedDriver, getPersistedDriver(partialUpdatedDriver));
    }

    @Test
    void patchNonExistingDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        driver.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, driver.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(driver))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        driver.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(driver))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDriver() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        driver.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(driver))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Driver in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDriver() {
        // Initialize the database
        insertedDriver = driverRepository.save(driver).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the driver
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, driver.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return driverRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Driver getPersistedDriver(Driver driver) {
        return driverRepository.findById(driver.getId()).block();
    }

    protected void assertPersistedDriverToMatchAllProperties(Driver expectedDriver) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDriverAllPropertiesEquals(expectedDriver, getPersistedDriver(expectedDriver));
        assertDriverUpdatableFieldsEquals(expectedDriver, getPersistedDriver(expectedDriver));
    }

    protected void assertPersistedDriverToMatchUpdatableProperties(Driver expectedDriver) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDriverAllUpdatablePropertiesEquals(expectedDriver, getPersistedDriver(expectedDriver));
        assertDriverUpdatableFieldsEquals(expectedDriver, getPersistedDriver(expectedDriver));
    }
}
