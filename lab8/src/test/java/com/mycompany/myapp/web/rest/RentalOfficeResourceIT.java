package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.RentalOfficeAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.RentalOffice;
import com.mycompany.myapp.domain.enumeration.OfficeStatus;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.RentalOfficeRepository;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link RentalOfficeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RentalOfficeResourceIT {

    private static final String DEFAULT_OFFICE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OFFICE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final OfficeStatus DEFAULT_STATUS = OfficeStatus.OPEN;
    private static final OfficeStatus UPDATED_STATUS = OfficeStatus.CLOSED;

    private static final String ENTITY_API_URL = "/api/rental-offices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RentalOfficeRepository rentalOfficeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RentalOffice rentalOffice;

    private RentalOffice insertedRentalOffice;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RentalOffice createEntity() {
        return new RentalOffice().officeName(DEFAULT_OFFICE_NAME).city(DEFAULT_CITY).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RentalOffice createUpdatedEntity() {
        return new RentalOffice().officeName(UPDATED_OFFICE_NAME).city(UPDATED_CITY).status(UPDATED_STATUS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RentalOffice.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        rentalOffice = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRentalOffice != null) {
            rentalOfficeRepository.delete(insertedRentalOffice).block();
            insertedRentalOffice = null;
        }
        deleteEntities(em);
    }

    @Test
    void createRentalOffice() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RentalOffice
        var returnedRentalOffice = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalOffice))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(RentalOffice.class)
            .returnResult()
            .getResponseBody();

        // Validate the RentalOffice in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRentalOfficeUpdatableFieldsEquals(returnedRentalOffice, getPersistedRentalOffice(returnedRentalOffice));

        insertedRentalOffice = returnedRentalOffice;
    }

    @Test
    void createRentalOfficeWithExistingId() throws Exception {
        // Create the RentalOffice with an existing ID
        rentalOffice.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalOffice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RentalOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkOfficeNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rentalOffice.setOfficeName(null);

        // Create the RentalOffice, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalOffice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rentalOffice.setCity(null);

        // Create the RentalOffice, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalOffice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllRentalOffices() {
        // Initialize the database
        insertedRentalOffice = rentalOfficeRepository.save(rentalOffice).block();

        // Get all the rentalOfficeList
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
            .value(hasItem(rentalOffice.getId().intValue()))
            .jsonPath("$.[*].officeName")
            .value(hasItem(DEFAULT_OFFICE_NAME))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getRentalOffice() {
        // Initialize the database
        insertedRentalOffice = rentalOfficeRepository.save(rentalOffice).block();

        // Get the rentalOffice
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rentalOffice.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rentalOffice.getId().intValue()))
            .jsonPath("$.officeName")
            .value(is(DEFAULT_OFFICE_NAME))
            .jsonPath("$.city")
            .value(is(DEFAULT_CITY))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingRentalOffice() {
        // Get the rentalOffice
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRentalOffice() throws Exception {
        // Initialize the database
        insertedRentalOffice = rentalOfficeRepository.save(rentalOffice).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rentalOffice
        RentalOffice updatedRentalOffice = rentalOfficeRepository.findById(rentalOffice.getId()).block();
        updatedRentalOffice.officeName(UPDATED_OFFICE_NAME).city(UPDATED_CITY).status(UPDATED_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRentalOffice.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedRentalOffice))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RentalOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRentalOfficeToMatchAllProperties(updatedRentalOffice);
    }

    @Test
    void putNonExistingRentalOffice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rentalOffice.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rentalOffice.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalOffice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RentalOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRentalOffice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rentalOffice.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalOffice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RentalOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRentalOffice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rentalOffice.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(rentalOffice))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RentalOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRentalOfficeWithPatch() throws Exception {
        // Initialize the database
        insertedRentalOffice = rentalOfficeRepository.save(rentalOffice).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rentalOffice using partial update
        RentalOffice partialUpdatedRentalOffice = new RentalOffice();
        partialUpdatedRentalOffice.setId(rentalOffice.getId());

        partialUpdatedRentalOffice.officeName(UPDATED_OFFICE_NAME).city(UPDATED_CITY).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRentalOffice.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRentalOffice))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RentalOffice in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRentalOfficeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRentalOffice, rentalOffice),
            getPersistedRentalOffice(rentalOffice)
        );
    }

    @Test
    void fullUpdateRentalOfficeWithPatch() throws Exception {
        // Initialize the database
        insertedRentalOffice = rentalOfficeRepository.save(rentalOffice).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rentalOffice using partial update
        RentalOffice partialUpdatedRentalOffice = new RentalOffice();
        partialUpdatedRentalOffice.setId(rentalOffice.getId());

        partialUpdatedRentalOffice.officeName(UPDATED_OFFICE_NAME).city(UPDATED_CITY).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRentalOffice.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRentalOffice))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RentalOffice in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRentalOfficeUpdatableFieldsEquals(partialUpdatedRentalOffice, getPersistedRentalOffice(partialUpdatedRentalOffice));
    }

    @Test
    void patchNonExistingRentalOffice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rentalOffice.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rentalOffice.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rentalOffice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RentalOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRentalOffice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rentalOffice.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rentalOffice))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RentalOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRentalOffice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rentalOffice.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(rentalOffice))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RentalOffice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRentalOffice() {
        // Initialize the database
        insertedRentalOffice = rentalOfficeRepository.save(rentalOffice).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the rentalOffice
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rentalOffice.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return rentalOfficeRepository.count().block();
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

    protected RentalOffice getPersistedRentalOffice(RentalOffice rentalOffice) {
        return rentalOfficeRepository.findById(rentalOffice.getId()).block();
    }

    protected void assertPersistedRentalOfficeToMatchAllProperties(RentalOffice expectedRentalOffice) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRentalOfficeAllPropertiesEquals(expectedRentalOffice, getPersistedRentalOffice(expectedRentalOffice));
        assertRentalOfficeUpdatableFieldsEquals(expectedRentalOffice, getPersistedRentalOffice(expectedRentalOffice));
    }

    protected void assertPersistedRentalOfficeToMatchUpdatableProperties(RentalOffice expectedRentalOffice) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRentalOfficeAllUpdatablePropertiesEquals(expectedRentalOffice, getPersistedRentalOffice(expectedRentalOffice));
        assertRentalOfficeUpdatableFieldsEquals(expectedRentalOffice, getPersistedRentalOffice(expectedRentalOffice));
    }
}
