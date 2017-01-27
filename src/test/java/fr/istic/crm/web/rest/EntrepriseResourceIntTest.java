package fr.istic.crm.web.rest;

import fr.istic.crm.CrmisticApp;

import fr.istic.crm.domain.Entreprise;
import fr.istic.crm.repository.EntrepriseRepository;
import fr.istic.crm.service.EntrepriseService;
import fr.istic.crm.repository.search.EntrepriseSearchRepository;
import fr.istic.crm.service.dto.EntrepriseDTO;
import fr.istic.crm.service.mapper.EntrepriseMapper;

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
 * Test class for the EntrepriseResource REST controller.
 *
 * @see EntrepriseResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrmisticApp.class)
public class EntrepriseResourceIntTest {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PAYS = "AAAAAAAAAA";
    private static final String UPDATED_PAYS = "BBBBBBBBBB";

    private static final String DEFAULT_NUM_SIRET = "AAAAAAAAAA";
    private static final String UPDATED_NUM_SIRET = "BBBBBBBBBB";

    private static final String DEFAULT_NUM_SIREN = "AAAAAAAAAA";
    private static final String UPDATED_NUM_SIREN = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final Long DEFAULT_DEBUT_VERSION = 1L;
    private static final Long UPDATED_DEBUT_VERSION = 2L;

    private static final Long DEFAULT_FIN_VERSION = 1L;
    private static final Long UPDATED_FIN_VERSION = 2L;

    @Inject
    private EntrepriseRepository entrepriseRepository;

    @Inject
    private EntrepriseMapper entrepriseMapper;

    @Inject
    private EntrepriseService entrepriseService;

    @Inject
    private EntrepriseSearchRepository entrepriseSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restEntrepriseMockMvc;

    private Entreprise entreprise;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EntrepriseResource entrepriseResource = new EntrepriseResource();
        ReflectionTestUtils.setField(entrepriseResource, "entrepriseService", entrepriseService);
        this.restEntrepriseMockMvc = MockMvcBuilders.standaloneSetup(entrepriseResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Entreprise createEntity(EntityManager em) {
        Entreprise entreprise = new Entreprise()
                .nom(DEFAULT_NOM)
                .pays(DEFAULT_PAYS)
                .numSiret(DEFAULT_NUM_SIRET)
                .numSiren(DEFAULT_NUM_SIREN)
                .telephone(DEFAULT_TELEPHONE)
                .debutVersion(DEFAULT_DEBUT_VERSION)
                .finVersion(DEFAULT_FIN_VERSION);
        return entreprise;
    }

    @Before
    public void initTest() {
        entrepriseSearchRepository.deleteAll();
        entreprise = createEntity(em);
    }

    @Test
    @Transactional
    public void createEntreprise() throws Exception {
        int databaseSizeBeforeCreate = entrepriseRepository.findAll().size();

        // Create the Entreprise
        EntrepriseDTO entrepriseDTO = entrepriseMapper.entrepriseToEntrepriseDTO(entreprise);

        restEntrepriseMockMvc.perform(post("/api/entreprises")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(entrepriseDTO)))
            .andExpect(status().isCreated());

        // Validate the Entreprise in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeCreate + 1);
        Entreprise testEntreprise = entrepriseList.get(entrepriseList.size() - 1);
        assertThat(testEntreprise.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testEntreprise.getPays()).isEqualTo(DEFAULT_PAYS);
        assertThat(testEntreprise.getNumSiret()).isEqualTo(DEFAULT_NUM_SIRET);
        assertThat(testEntreprise.getNumSiren()).isEqualTo(DEFAULT_NUM_SIREN);
        assertThat(testEntreprise.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testEntreprise.getDebutVersion()).isEqualTo(DEFAULT_DEBUT_VERSION);
        assertThat(testEntreprise.getFinVersion()).isEqualTo(DEFAULT_FIN_VERSION);

        // Validate the Entreprise in ElasticSearch
        Entreprise entrepriseEs = entrepriseSearchRepository.findOne(testEntreprise.getId());
        assertThat(entrepriseEs).isEqualToComparingFieldByField(testEntreprise);
    }

    @Test
    @Transactional
    public void createEntrepriseWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = entrepriseRepository.findAll().size();

        // Create the Entreprise with an existing ID
        Entreprise existingEntreprise = new Entreprise();
        existingEntreprise.setId(1L);
        EntrepriseDTO existingEntrepriseDTO = entrepriseMapper.entrepriseToEntrepriseDTO(existingEntreprise);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEntrepriseMockMvc.perform(post("/api/entreprises")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingEntrepriseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNumSiretIsRequired() throws Exception {
        int databaseSizeBeforeTest = entrepriseRepository.findAll().size();
        // set the field null
        entreprise.setNumSiret(null);

        // Create the Entreprise, which fails.
        EntrepriseDTO entrepriseDTO = entrepriseMapper.entrepriseToEntrepriseDTO(entreprise);

        restEntrepriseMockMvc.perform(post("/api/entreprises")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(entrepriseDTO)))
            .andExpect(status().isBadRequest());

        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNumSirenIsRequired() throws Exception {
        int databaseSizeBeforeTest = entrepriseRepository.findAll().size();
        // set the field null
        entreprise.setNumSiren(null);

        // Create the Entreprise, which fails.
        EntrepriseDTO entrepriseDTO = entrepriseMapper.entrepriseToEntrepriseDTO(entreprise);

        restEntrepriseMockMvc.perform(post("/api/entreprises")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(entrepriseDTO)))
            .andExpect(status().isBadRequest());

        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEntreprises() throws Exception {
        // Initialize the database
        entrepriseRepository.saveAndFlush(entreprise);

        // Get all the entrepriseList
        restEntrepriseMockMvc.perform(get("/api/entreprises?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entreprise.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].pays").value(hasItem(DEFAULT_PAYS.toString())))
            .andExpect(jsonPath("$.[*].numSiret").value(hasItem(DEFAULT_NUM_SIRET.toString())))
            .andExpect(jsonPath("$.[*].numSiren").value(hasItem(DEFAULT_NUM_SIREN.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE.toString())))
            .andExpect(jsonPath("$.[*].debutVersion").value(hasItem(DEFAULT_DEBUT_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].finVersion").value(hasItem(DEFAULT_FIN_VERSION.intValue())));
    }

    @Test
    @Transactional
    public void getEntreprise() throws Exception {
        // Initialize the database
        entrepriseRepository.saveAndFlush(entreprise);

        // Get the entreprise
        restEntrepriseMockMvc.perform(get("/api/entreprises/{id}", entreprise.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(entreprise.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM.toString()))
            .andExpect(jsonPath("$.pays").value(DEFAULT_PAYS.toString()))
            .andExpect(jsonPath("$.numSiret").value(DEFAULT_NUM_SIRET.toString()))
            .andExpect(jsonPath("$.numSiren").value(DEFAULT_NUM_SIREN.toString()))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE.toString()))
            .andExpect(jsonPath("$.debutVersion").value(DEFAULT_DEBUT_VERSION.intValue()))
            .andExpect(jsonPath("$.finVersion").value(DEFAULT_FIN_VERSION.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingEntreprise() throws Exception {
        // Get the entreprise
        restEntrepriseMockMvc.perform(get("/api/entreprises/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEntreprise() throws Exception {
        // Initialize the database
        entrepriseRepository.saveAndFlush(entreprise);
        entrepriseSearchRepository.save(entreprise);
        int databaseSizeBeforeUpdate = entrepriseRepository.findAll().size();

        // Update the entreprise
        Entreprise updatedEntreprise = entrepriseRepository.findOne(entreprise.getId());
        updatedEntreprise
                .nom(UPDATED_NOM)
                .pays(UPDATED_PAYS)
                .numSiret(UPDATED_NUM_SIRET)
                .numSiren(UPDATED_NUM_SIREN)
                .telephone(UPDATED_TELEPHONE)
                .debutVersion(UPDATED_DEBUT_VERSION)
                .finVersion(UPDATED_FIN_VERSION);
        EntrepriseDTO entrepriseDTO = entrepriseMapper.entrepriseToEntrepriseDTO(updatedEntreprise);

        restEntrepriseMockMvc.perform(put("/api/entreprises")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(entrepriseDTO)))
            .andExpect(status().isOk());

        // Validate the Entreprise in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeUpdate);
        Entreprise testEntreprise = entrepriseList.get(entrepriseList.size() - 1);
        assertThat(testEntreprise.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEntreprise.getPays()).isEqualTo(UPDATED_PAYS);
        assertThat(testEntreprise.getNumSiret()).isEqualTo(UPDATED_NUM_SIRET);
        assertThat(testEntreprise.getNumSiren()).isEqualTo(UPDATED_NUM_SIREN);
        assertThat(testEntreprise.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testEntreprise.getDebutVersion()).isEqualTo(UPDATED_DEBUT_VERSION);
        assertThat(testEntreprise.getFinVersion()).isEqualTo(UPDATED_FIN_VERSION);

        // Validate the Entreprise in ElasticSearch
        Entreprise entrepriseEs = entrepriseSearchRepository.findOne(testEntreprise.getId());
        assertThat(entrepriseEs).isEqualToComparingFieldByField(testEntreprise);
    }

    @Test
    @Transactional
    public void updateNonExistingEntreprise() throws Exception {
        int databaseSizeBeforeUpdate = entrepriseRepository.findAll().size();

        // Create the Entreprise
        EntrepriseDTO entrepriseDTO = entrepriseMapper.entrepriseToEntrepriseDTO(entreprise);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restEntrepriseMockMvc.perform(put("/api/entreprises")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(entrepriseDTO)))
            .andExpect(status().isCreated());

        // Validate the Entreprise in the database
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteEntreprise() throws Exception {
        // Initialize the database
        entrepriseRepository.saveAndFlush(entreprise);
        entrepriseSearchRepository.save(entreprise);
        int databaseSizeBeforeDelete = entrepriseRepository.findAll().size();

        // Get the entreprise
        restEntrepriseMockMvc.perform(delete("/api/entreprises/{id}", entreprise.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean entrepriseExistsInEs = entrepriseSearchRepository.exists(entreprise.getId());
        assertThat(entrepriseExistsInEs).isFalse();

        // Validate the database is empty
        List<Entreprise> entrepriseList = entrepriseRepository.findAll();
        assertThat(entrepriseList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchEntreprise() throws Exception {
        // Initialize the database
        entrepriseRepository.saveAndFlush(entreprise);
        entrepriseSearchRepository.save(entreprise);

        // Search the entreprise
        restEntrepriseMockMvc.perform(get("/api/_search/entreprises?query=id:" + entreprise.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entreprise.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].pays").value(hasItem(DEFAULT_PAYS.toString())))
            .andExpect(jsonPath("$.[*].numSiret").value(hasItem(DEFAULT_NUM_SIRET.toString())))
            .andExpect(jsonPath("$.[*].numSiren").value(hasItem(DEFAULT_NUM_SIREN.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE.toString())))
            .andExpect(jsonPath("$.[*].debutVersion").value(hasItem(DEFAULT_DEBUT_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].finVersion").value(hasItem(DEFAULT_FIN_VERSION.intValue())));
    }
}
