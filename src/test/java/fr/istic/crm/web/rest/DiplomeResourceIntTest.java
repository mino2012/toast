package fr.istic.crm.web.rest;

import fr.istic.crm.CrmisticApp;

import fr.istic.crm.domain.Diplome;
import fr.istic.crm.repository.DiplomeRepository;
import fr.istic.crm.service.DiplomeService;
import fr.istic.crm.repository.search.DiplomeSearchRepository;
import fr.istic.crm.service.dto.DiplomeDTO;
import fr.istic.crm.service.mapper.DiplomeMapper;

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
 * Test class for the DiplomeResource REST controller.
 *
 * @see DiplomeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrmisticApp.class)
public class DiplomeResourceIntTest {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Long DEFAULT_DEBUT_VERSION = 1L;
    private static final Long UPDATED_DEBUT_VERSION = 2L;

    private static final Long DEFAULT_FIN_VERSION = 1L;
    private static final Long UPDATED_FIN_VERSION = 2L;

    @Inject
    private DiplomeRepository diplomeRepository;

    @Inject
    private DiplomeMapper diplomeMapper;

    @Inject
    private DiplomeService diplomeService;

    @Inject
    private DiplomeSearchRepository diplomeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restDiplomeMockMvc;

    private Diplome diplome;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DiplomeResource diplomeResource = new DiplomeResource();
        ReflectionTestUtils.setField(diplomeResource, "diplomeService", diplomeService);
        this.restDiplomeMockMvc = MockMvcBuilders.standaloneSetup(diplomeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Diplome createEntity(EntityManager em) {
        Diplome diplome = new Diplome()
                .nom(DEFAULT_NOM)
                .debutVersion(DEFAULT_DEBUT_VERSION)
                .finVersion(DEFAULT_FIN_VERSION);
        return diplome;
    }

    @Before
    public void initTest() {
        diplomeSearchRepository.deleteAll();
        diplome = createEntity(em);
    }

    @Test
    @Transactional
    public void createDiplome() throws Exception {
        int databaseSizeBeforeCreate = diplomeRepository.findAll().size();

        // Create the Diplome
        DiplomeDTO diplomeDTO = diplomeMapper.diplomeToDiplomeDTO(diplome);

        restDiplomeMockMvc.perform(post("/api/diplomes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diplomeDTO)))
            .andExpect(status().isCreated());

        // Validate the Diplome in the database
        List<Diplome> diplomeList = diplomeRepository.findAll();
        assertThat(diplomeList).hasSize(databaseSizeBeforeCreate + 1);
        Diplome testDiplome = diplomeList.get(diplomeList.size() - 1);
        assertThat(testDiplome.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testDiplome.getDebutVersion()).isEqualTo(DEFAULT_DEBUT_VERSION);
        assertThat(testDiplome.getFinVersion()).isEqualTo(DEFAULT_FIN_VERSION);

        // Validate the Diplome in ElasticSearch
        Diplome diplomeEs = diplomeSearchRepository.findOne(testDiplome.getId());
        assertThat(diplomeEs).isEqualToComparingFieldByField(testDiplome);
    }

    @Test
    @Transactional
    public void createDiplomeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = diplomeRepository.findAll().size();

        // Create the Diplome with an existing ID
        Diplome existingDiplome = new Diplome();
        existingDiplome.setId(1L);
        DiplomeDTO existingDiplomeDTO = diplomeMapper.diplomeToDiplomeDTO(existingDiplome);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDiplomeMockMvc.perform(post("/api/diplomes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingDiplomeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Diplome> diplomeList = diplomeRepository.findAll();
        assertThat(diplomeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = diplomeRepository.findAll().size();
        // set the field null
        diplome.setNom(null);

        // Create the Diplome, which fails.
        DiplomeDTO diplomeDTO = diplomeMapper.diplomeToDiplomeDTO(diplome);

        restDiplomeMockMvc.perform(post("/api/diplomes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diplomeDTO)))
            .andExpect(status().isBadRequest());

        List<Diplome> diplomeList = diplomeRepository.findAll();
        assertThat(diplomeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDiplomes() throws Exception {
        // Initialize the database
        diplomeRepository.saveAndFlush(diplome);

        // Get all the diplomeList
        restDiplomeMockMvc.perform(get("/api/diplomes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(diplome.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].debutVersion").value(hasItem(DEFAULT_DEBUT_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].finVersion").value(hasItem(DEFAULT_FIN_VERSION.intValue())));
    }

    @Test
    @Transactional
    public void getDiplome() throws Exception {
        // Initialize the database
        diplomeRepository.saveAndFlush(diplome);

        // Get the diplome
        restDiplomeMockMvc.perform(get("/api/diplomes/{id}", diplome.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(diplome.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM.toString()))
            .andExpect(jsonPath("$.debutVersion").value(DEFAULT_DEBUT_VERSION.intValue()))
            .andExpect(jsonPath("$.finVersion").value(DEFAULT_FIN_VERSION.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingDiplome() throws Exception {
        // Get the diplome
        restDiplomeMockMvc.perform(get("/api/diplomes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDiplome() throws Exception {
        // Initialize the database
        diplomeRepository.saveAndFlush(diplome);
        diplomeSearchRepository.save(diplome);
        int databaseSizeBeforeUpdate = diplomeRepository.findAll().size();

        // Update the diplome
        Diplome updatedDiplome = diplomeRepository.findOne(diplome.getId());
        updatedDiplome
                .nom(UPDATED_NOM)
                .debutVersion(UPDATED_DEBUT_VERSION)
                .finVersion(UPDATED_FIN_VERSION);
        DiplomeDTO diplomeDTO = diplomeMapper.diplomeToDiplomeDTO(updatedDiplome);

        restDiplomeMockMvc.perform(put("/api/diplomes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diplomeDTO)))
            .andExpect(status().isOk());

        // Validate the Diplome in the database
        List<Diplome> diplomeList = diplomeRepository.findAll();
        assertThat(diplomeList).hasSize(databaseSizeBeforeUpdate);
        Diplome testDiplome = diplomeList.get(diplomeList.size() - 1);
        assertThat(testDiplome.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testDiplome.getDebutVersion()).isEqualTo(UPDATED_DEBUT_VERSION);
        assertThat(testDiplome.getFinVersion()).isEqualTo(UPDATED_FIN_VERSION);

        // Validate the Diplome in ElasticSearch
        Diplome diplomeEs = diplomeSearchRepository.findOne(testDiplome.getId());
        assertThat(diplomeEs).isEqualToComparingFieldByField(testDiplome);
    }

    @Test
    @Transactional
    public void updateNonExistingDiplome() throws Exception {
        int databaseSizeBeforeUpdate = diplomeRepository.findAll().size();

        // Create the Diplome
        DiplomeDTO diplomeDTO = diplomeMapper.diplomeToDiplomeDTO(diplome);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDiplomeMockMvc.perform(put("/api/diplomes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diplomeDTO)))
            .andExpect(status().isCreated());

        // Validate the Diplome in the database
        List<Diplome> diplomeList = diplomeRepository.findAll();
        assertThat(diplomeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteDiplome() throws Exception {
        // Initialize the database
        diplomeRepository.saveAndFlush(diplome);
        diplomeSearchRepository.save(diplome);
        int databaseSizeBeforeDelete = diplomeRepository.findAll().size();

        // Get the diplome
        restDiplomeMockMvc.perform(delete("/api/diplomes/{id}", diplome.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean diplomeExistsInEs = diplomeSearchRepository.exists(diplome.getId());
        assertThat(diplomeExistsInEs).isFalse();

        // Validate the database is empty
        List<Diplome> diplomeList = diplomeRepository.findAll();
        assertThat(diplomeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchDiplome() throws Exception {
        // Initialize the database
        diplomeRepository.saveAndFlush(diplome);
        diplomeSearchRepository.save(diplome);

        // Search the diplome
        restDiplomeMockMvc.perform(get("/api/_search/diplomes?query=id:" + diplome.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(diplome.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].debutVersion").value(hasItem(DEFAULT_DEBUT_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].finVersion").value(hasItem(DEFAULT_FIN_VERSION.intValue())));
    }
}
