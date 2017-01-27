package fr.istic.crm.web.rest;

import fr.istic.crm.CrmisticApp;

import fr.istic.crm.domain.Taxe;
import fr.istic.crm.repository.TaxeRepository;
import fr.istic.crm.service.TaxeService;
import fr.istic.crm.repository.search.TaxeSearchRepository;
import fr.istic.crm.service.dto.TaxeDTO;
import fr.istic.crm.service.mapper.TaxeMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static fr.istic.crm.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TaxeResource REST controller.
 *
 * @see TaxeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrmisticApp.class)
public class TaxeResourceIntTest {

    private static final Double DEFAULT_MONTANT = 1D;
    private static final Double UPDATED_MONTANT = 2D;

    private static final ZonedDateTime DEFAULT_ANNEE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ANNEE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Inject
    private TaxeRepository taxeRepository;

    @Inject
    private TaxeMapper taxeMapper;

    @Inject
    private TaxeService taxeService;

    @Inject
    private TaxeSearchRepository taxeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restTaxeMockMvc;

    private Taxe taxe;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TaxeResource taxeResource = new TaxeResource();
        ReflectionTestUtils.setField(taxeResource, "taxeService", taxeService);
        this.restTaxeMockMvc = MockMvcBuilders.standaloneSetup(taxeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Taxe createEntity(EntityManager em) {
        Taxe taxe = new Taxe()
                .montant(DEFAULT_MONTANT)
                .annee(DEFAULT_ANNEE);
        return taxe;
    }

    @Before
    public void initTest() {
        taxeSearchRepository.deleteAll();
        taxe = createEntity(em);
    }

    @Test
    @Transactional
    public void createTaxe() throws Exception {
        int databaseSizeBeforeCreate = taxeRepository.findAll().size();

        // Create the Taxe
        TaxeDTO taxeDTO = taxeMapper.taxeToTaxeDTO(taxe);

        restTaxeMockMvc.perform(post("/api/taxes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taxeDTO)))
            .andExpect(status().isCreated());

        // Validate the Taxe in the database
        List<Taxe> taxeList = taxeRepository.findAll();
        assertThat(taxeList).hasSize(databaseSizeBeforeCreate + 1);
        Taxe testTaxe = taxeList.get(taxeList.size() - 1);
        assertThat(testTaxe.getMontant()).isEqualTo(DEFAULT_MONTANT);
        assertThat(testTaxe.getAnnee()).isEqualTo(DEFAULT_ANNEE);

        // Validate the Taxe in ElasticSearch
        Taxe taxeEs = taxeSearchRepository.findOne(testTaxe.getId());
        assertThat(taxeEs).isEqualToComparingFieldByField(testTaxe);
    }

    @Test
    @Transactional
    public void createTaxeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = taxeRepository.findAll().size();

        // Create the Taxe with an existing ID
        Taxe existingTaxe = new Taxe();
        existingTaxe.setId(1L);
        TaxeDTO existingTaxeDTO = taxeMapper.taxeToTaxeDTO(existingTaxe);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaxeMockMvc.perform(post("/api/taxes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingTaxeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Taxe> taxeList = taxeRepository.findAll();
        assertThat(taxeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllTaxes() throws Exception {
        // Initialize the database
        taxeRepository.saveAndFlush(taxe);

        // Get all the taxeList
        restTaxeMockMvc.perform(get("/api/taxes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taxe.getId().intValue())))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.doubleValue())))
            .andExpect(jsonPath("$.[*].annee").value(hasItem(sameInstant(DEFAULT_ANNEE))));
    }

    @Test
    @Transactional
    public void getTaxe() throws Exception {
        // Initialize the database
        taxeRepository.saveAndFlush(taxe);

        // Get the taxe
        restTaxeMockMvc.perform(get("/api/taxes/{id}", taxe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(taxe.getId().intValue()))
            .andExpect(jsonPath("$.montant").value(DEFAULT_MONTANT.doubleValue()))
            .andExpect(jsonPath("$.annee").value(sameInstant(DEFAULT_ANNEE)));
    }

    @Test
    @Transactional
    public void getNonExistingTaxe() throws Exception {
        // Get the taxe
        restTaxeMockMvc.perform(get("/api/taxes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTaxe() throws Exception {
        // Initialize the database
        taxeRepository.saveAndFlush(taxe);
        taxeSearchRepository.save(taxe);
        int databaseSizeBeforeUpdate = taxeRepository.findAll().size();

        // Update the taxe
        Taxe updatedTaxe = taxeRepository.findOne(taxe.getId());
        updatedTaxe
                .montant(UPDATED_MONTANT)
                .annee(UPDATED_ANNEE);
        TaxeDTO taxeDTO = taxeMapper.taxeToTaxeDTO(updatedTaxe);

        restTaxeMockMvc.perform(put("/api/taxes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taxeDTO)))
            .andExpect(status().isOk());

        // Validate the Taxe in the database
        List<Taxe> taxeList = taxeRepository.findAll();
        assertThat(taxeList).hasSize(databaseSizeBeforeUpdate);
        Taxe testTaxe = taxeList.get(taxeList.size() - 1);
        assertThat(testTaxe.getMontant()).isEqualTo(UPDATED_MONTANT);
        assertThat(testTaxe.getAnnee()).isEqualTo(UPDATED_ANNEE);

        // Validate the Taxe in ElasticSearch
        Taxe taxeEs = taxeSearchRepository.findOne(testTaxe.getId());
        assertThat(taxeEs).isEqualToComparingFieldByField(testTaxe);
    }

    @Test
    @Transactional
    public void updateNonExistingTaxe() throws Exception {
        int databaseSizeBeforeUpdate = taxeRepository.findAll().size();

        // Create the Taxe
        TaxeDTO taxeDTO = taxeMapper.taxeToTaxeDTO(taxe);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTaxeMockMvc.perform(put("/api/taxes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taxeDTO)))
            .andExpect(status().isCreated());

        // Validate the Taxe in the database
        List<Taxe> taxeList = taxeRepository.findAll();
        assertThat(taxeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTaxe() throws Exception {
        // Initialize the database
        taxeRepository.saveAndFlush(taxe);
        taxeSearchRepository.save(taxe);
        int databaseSizeBeforeDelete = taxeRepository.findAll().size();

        // Get the taxe
        restTaxeMockMvc.perform(delete("/api/taxes/{id}", taxe.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean taxeExistsInEs = taxeSearchRepository.exists(taxe.getId());
        assertThat(taxeExistsInEs).isFalse();

        // Validate the database is empty
        List<Taxe> taxeList = taxeRepository.findAll();
        assertThat(taxeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTaxe() throws Exception {
        // Initialize the database
        taxeRepository.saveAndFlush(taxe);
        taxeSearchRepository.save(taxe);

        // Search the taxe
        restTaxeMockMvc.perform(get("/api/_search/taxes?query=id:" + taxe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taxe.getId().intValue())))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.doubleValue())))
            .andExpect(jsonPath("$.[*].annee").value(hasItem(sameInstant(DEFAULT_ANNEE))));
    }
}
