package fr.istic.crm.web.rest;

import fr.istic.crm.CrmisticApp;

import fr.istic.crm.domain.Filiere;
import fr.istic.crm.repository.FiliereRepository;
import fr.istic.crm.service.FiliereService;
import fr.istic.crm.repository.search.FiliereSearchRepository;
import fr.istic.crm.service.dto.FiliereDTO;
import fr.istic.crm.service.mapper.FiliereMapper;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FiliereResource REST controller.
 *
 * @see FiliereResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrmisticApp.class)
public class FiliereResourceIntTest {

    private static final String DEFAULT_NIVEAU = "AAAAAAAAAA";
    private static final String UPDATED_NIVEAU = "BBBBBBBBBB";

    @Inject
    private FiliereRepository filiereRepository;

    @Inject
    private FiliereMapper filiereMapper;

    @Inject
    private FiliereService filiereService;

    @Inject
    private FiliereSearchRepository filiereSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restFiliereMockMvc;

    private Filiere filiere;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FiliereResource filiereResource = new FiliereResource();
        ReflectionTestUtils.setField(filiereResource, "filiereService", filiereService);
        this.restFiliereMockMvc = MockMvcBuilders.standaloneSetup(filiereResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Filiere createEntity(EntityManager em) {
        Filiere filiere = new Filiere()
                .niveau(DEFAULT_NIVEAU);
        return filiere;
    }

    @Before
    public void initTest() {
        filiereSearchRepository.deleteAll();
        filiere = createEntity(em);
    }

    @Test
    @Transactional
    public void createFiliere() throws Exception {
        int databaseSizeBeforeCreate = filiereRepository.findAll().size();

        // Create the Filiere
        FiliereDTO filiereDTO = filiereMapper.filiereToFiliereDTO(filiere);

        restFiliereMockMvc.perform(post("/api/filieres")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(filiereDTO)))
            .andExpect(status().isCreated());

        // Validate the Filiere in the database
        List<Filiere> filiereList = filiereRepository.findAll();
        assertThat(filiereList).hasSize(databaseSizeBeforeCreate + 1);
        Filiere testFiliere = filiereList.get(filiereList.size() - 1);
        assertThat(testFiliere.getNiveau()).isEqualTo(DEFAULT_NIVEAU);

        // Validate the Filiere in ElasticSearch
        Filiere filiereEs = filiereSearchRepository.findOne(testFiliere.getId());
        assertThat(filiereEs).isEqualToComparingFieldByField(testFiliere);
    }

    @Test
    @Transactional
    public void createFiliereWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = filiereRepository.findAll().size();

        // Create the Filiere with an existing ID
        Filiere existingFiliere = new Filiere();
        existingFiliere.setId(1L);
        FiliereDTO existingFiliereDTO = filiereMapper.filiereToFiliereDTO(existingFiliere);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFiliereMockMvc.perform(post("/api/filieres")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingFiliereDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Filiere> filiereList = filiereRepository.findAll();
        assertThat(filiereList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNiveauIsRequired() throws Exception {
        int databaseSizeBeforeTest = filiereRepository.findAll().size();
        // set the field null
        filiere.setNiveau(null);

        // Create the Filiere, which fails.
        FiliereDTO filiereDTO = filiereMapper.filiereToFiliereDTO(filiere);

        restFiliereMockMvc.perform(post("/api/filieres")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(filiereDTO)))
            .andExpect(status().isBadRequest());

        List<Filiere> filiereList = filiereRepository.findAll();
        assertThat(filiereList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFilieres() throws Exception {
        // Initialize the database
        filiereRepository.saveAndFlush(filiere);

        // Get all the filiereList
        restFiliereMockMvc.perform(get("/api/filieres?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(filiere.getId().intValue())))
            .andExpect(jsonPath("$.[*].niveau").value(hasItem(DEFAULT_NIVEAU.toString())));
    }

    @Test
    @Transactional
    public void getFiliere() throws Exception {
        // Initialize the database
        filiereRepository.saveAndFlush(filiere);

        // Get the filiere
        restFiliereMockMvc.perform(get("/api/filieres/{id}", filiere.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(filiere.getId().intValue()))
            .andExpect(jsonPath("$.niveau").value(DEFAULT_NIVEAU.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFiliere() throws Exception {
        // Get the filiere
        restFiliereMockMvc.perform(get("/api/filieres/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFiliere() throws Exception {
        // Initialize the database
        filiereRepository.saveAndFlush(filiere);
        filiereSearchRepository.save(filiere);
        int databaseSizeBeforeUpdate = filiereRepository.findAll().size();

        // Update the filiere
        Filiere updatedFiliere = filiereRepository.findOne(filiere.getId());
        updatedFiliere
                .niveau(UPDATED_NIVEAU);
        FiliereDTO filiereDTO = filiereMapper.filiereToFiliereDTO(updatedFiliere);

        restFiliereMockMvc.perform(put("/api/filieres")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(filiereDTO)))
            .andExpect(status().isOk());

        // Validate the Filiere in the database
        List<Filiere> filiereList = filiereRepository.findAll();
        assertThat(filiereList).hasSize(databaseSizeBeforeUpdate);
        Filiere testFiliere = filiereList.get(filiereList.size() - 1);
        assertThat(testFiliere.getNiveau()).isEqualTo(UPDATED_NIVEAU);

        // Validate the Filiere in ElasticSearch
        Filiere filiereEs = filiereSearchRepository.findOne(testFiliere.getId());
        assertThat(filiereEs).isEqualToComparingFieldByField(testFiliere);
    }

    @Test
    @Transactional
    public void updateNonExistingFiliere() throws Exception {
        int databaseSizeBeforeUpdate = filiereRepository.findAll().size();

        // Create the Filiere
        FiliereDTO filiereDTO = filiereMapper.filiereToFiliereDTO(filiere);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restFiliereMockMvc.perform(put("/api/filieres")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(filiereDTO)))
            .andExpect(status().isCreated());

        // Validate the Filiere in the database
        List<Filiere> filiereList = filiereRepository.findAll();
        assertThat(filiereList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteFiliere() throws Exception {
        // Initialize the database
        filiereRepository.saveAndFlush(filiere);
        filiereSearchRepository.save(filiere);
        int databaseSizeBeforeDelete = filiereRepository.findAll().size();

        // Get the filiere
        restFiliereMockMvc.perform(delete("/api/filieres/{id}", filiere.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean filiereExistsInEs = filiereSearchRepository.exists(filiere.getId());
        assertThat(filiereExistsInEs).isFalse();

        // Validate the database is empty
        List<Filiere> filiereList = filiereRepository.findAll();
        assertThat(filiereList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFiliere() throws Exception {
        // Initialize the database
        filiereRepository.saveAndFlush(filiere);
        filiereSearchRepository.save(filiere);

        // Search the filiere
        restFiliereMockMvc.perform(get("/api/_search/filieres?query=id:" + filiere.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(filiere.getId().intValue())))
            .andExpect(jsonPath("$.[*].niveau").value(hasItem(DEFAULT_NIVEAU.toString())));
    }
}
