package fr.istic.crm.web.rest;

import fr.istic.crm.CrmisticApp;

import fr.istic.crm.domain.Partenariat;
import fr.istic.crm.repository.PartenariatRepository;
import fr.istic.crm.service.PartenariatService;
import fr.istic.crm.repository.search.PartenariatSearchRepository;
import fr.istic.crm.service.dto.PartenariatDTO;
import fr.istic.crm.service.mapper.PartenariatMapper;

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
 * Test class for the PartenariatResource REST controller.
 *
 * @see PartenariatResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrmisticApp.class)
public class PartenariatResourceIntTest {

    private static final ZonedDateTime DEFAULT_DATE_DEBUT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_DEBUT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_DATE_FIN = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE_FIN = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Inject
    private PartenariatRepository partenariatRepository;

    @Inject
    private PartenariatMapper partenariatMapper;

    @Inject
    private PartenariatService partenariatService;

    @Inject
    private PartenariatSearchRepository partenariatSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPartenariatMockMvc;

    private Partenariat partenariat;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PartenariatResource partenariatResource = new PartenariatResource();
        ReflectionTestUtils.setField(partenariatResource, "partenariatService", partenariatService);
        this.restPartenariatMockMvc = MockMvcBuilders.standaloneSetup(partenariatResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Partenariat createEntity(EntityManager em) {
        Partenariat partenariat = new Partenariat()
                .dateDebut(DEFAULT_DATE_DEBUT)
                .dateFin(DEFAULT_DATE_FIN);
        return partenariat;
    }

    @Before
    public void initTest() {
        partenariatSearchRepository.deleteAll();
        partenariat = createEntity(em);
    }

    @Test
    @Transactional
    public void createPartenariat() throws Exception {
        int databaseSizeBeforeCreate = partenariatRepository.findAll().size();

        // Create the Partenariat
        PartenariatDTO partenariatDTO = partenariatMapper.partenariatToPartenariatDTO(partenariat);

        restPartenariatMockMvc.perform(post("/api/partenariats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(partenariatDTO)))
            .andExpect(status().isCreated());

        // Validate the Partenariat in the database
        List<Partenariat> partenariatList = partenariatRepository.findAll();
        assertThat(partenariatList).hasSize(databaseSizeBeforeCreate + 1);
        Partenariat testPartenariat = partenariatList.get(partenariatList.size() - 1);
        assertThat(testPartenariat.getDateDebut()).isEqualTo(DEFAULT_DATE_DEBUT);
        assertThat(testPartenariat.getDateFin()).isEqualTo(DEFAULT_DATE_FIN);

        // Validate the Partenariat in ElasticSearch
        Partenariat partenariatEs = partenariatSearchRepository.findOne(testPartenariat.getId());
        assertThat(partenariatEs).isEqualToComparingFieldByField(testPartenariat);
    }

    @Test
    @Transactional
    public void createPartenariatWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = partenariatRepository.findAll().size();

        // Create the Partenariat with an existing ID
        Partenariat existingPartenariat = new Partenariat();
        existingPartenariat.setId(1L);
        PartenariatDTO existingPartenariatDTO = partenariatMapper.partenariatToPartenariatDTO(existingPartenariat);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartenariatMockMvc.perform(post("/api/partenariats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingPartenariatDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Partenariat> partenariatList = partenariatRepository.findAll();
        assertThat(partenariatList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllPartenariats() throws Exception {
        // Initialize the database
        partenariatRepository.saveAndFlush(partenariat);

        // Get all the partenariatList
        restPartenariatMockMvc.perform(get("/api/partenariats?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partenariat.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(sameInstant(DEFAULT_DATE_DEBUT))))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(sameInstant(DEFAULT_DATE_FIN))));
    }

    @Test
    @Transactional
    public void getPartenariat() throws Exception {
        // Initialize the database
        partenariatRepository.saveAndFlush(partenariat);

        // Get the partenariat
        restPartenariatMockMvc.perform(get("/api/partenariats/{id}", partenariat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(partenariat.getId().intValue()))
            .andExpect(jsonPath("$.dateDebut").value(sameInstant(DEFAULT_DATE_DEBUT)))
            .andExpect(jsonPath("$.dateFin").value(sameInstant(DEFAULT_DATE_FIN)));
    }

    @Test
    @Transactional
    public void getNonExistingPartenariat() throws Exception {
        // Get the partenariat
        restPartenariatMockMvc.perform(get("/api/partenariats/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePartenariat() throws Exception {
        // Initialize the database
        partenariatRepository.saveAndFlush(partenariat);
        partenariatSearchRepository.save(partenariat);
        int databaseSizeBeforeUpdate = partenariatRepository.findAll().size();

        // Update the partenariat
        Partenariat updatedPartenariat = partenariatRepository.findOne(partenariat.getId());
        updatedPartenariat
                .dateDebut(UPDATED_DATE_DEBUT)
                .dateFin(UPDATED_DATE_FIN);
        PartenariatDTO partenariatDTO = partenariatMapper.partenariatToPartenariatDTO(updatedPartenariat);

        restPartenariatMockMvc.perform(put("/api/partenariats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(partenariatDTO)))
            .andExpect(status().isOk());

        // Validate the Partenariat in the database
        List<Partenariat> partenariatList = partenariatRepository.findAll();
        assertThat(partenariatList).hasSize(databaseSizeBeforeUpdate);
        Partenariat testPartenariat = partenariatList.get(partenariatList.size() - 1);
        assertThat(testPartenariat.getDateDebut()).isEqualTo(UPDATED_DATE_DEBUT);
        assertThat(testPartenariat.getDateFin()).isEqualTo(UPDATED_DATE_FIN);

        // Validate the Partenariat in ElasticSearch
        Partenariat partenariatEs = partenariatSearchRepository.findOne(testPartenariat.getId());
        assertThat(partenariatEs).isEqualToComparingFieldByField(testPartenariat);
    }

    @Test
    @Transactional
    public void updateNonExistingPartenariat() throws Exception {
        int databaseSizeBeforeUpdate = partenariatRepository.findAll().size();

        // Create the Partenariat
        PartenariatDTO partenariatDTO = partenariatMapper.partenariatToPartenariatDTO(partenariat);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPartenariatMockMvc.perform(put("/api/partenariats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(partenariatDTO)))
            .andExpect(status().isCreated());

        // Validate the Partenariat in the database
        List<Partenariat> partenariatList = partenariatRepository.findAll();
        assertThat(partenariatList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePartenariat() throws Exception {
        // Initialize the database
        partenariatRepository.saveAndFlush(partenariat);
        partenariatSearchRepository.save(partenariat);
        int databaseSizeBeforeDelete = partenariatRepository.findAll().size();

        // Get the partenariat
        restPartenariatMockMvc.perform(delete("/api/partenariats/{id}", partenariat.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean partenariatExistsInEs = partenariatSearchRepository.exists(partenariat.getId());
        assertThat(partenariatExistsInEs).isFalse();

        // Validate the database is empty
        List<Partenariat> partenariatList = partenariatRepository.findAll();
        assertThat(partenariatList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPartenariat() throws Exception {
        // Initialize the database
        partenariatRepository.saveAndFlush(partenariat);
        partenariatSearchRepository.save(partenariat);

        // Search the partenariat
        restPartenariatMockMvc.perform(get("/api/_search/partenariats?query=id:" + partenariat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partenariat.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(sameInstant(DEFAULT_DATE_DEBUT))))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(sameInstant(DEFAULT_DATE_FIN))));
    }
}
