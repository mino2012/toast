package fr.istic.crm.web.rest;

import fr.istic.crm.CrmisticApp;

import fr.istic.crm.domain.Tuteur;
import fr.istic.crm.repository.TuteurRepository;
import fr.istic.crm.service.TuteurService;
import fr.istic.crm.repository.search.TuteurSearchRepository;
import fr.istic.crm.service.dto.TuteurDTO;
import fr.istic.crm.service.mapper.TuteurMapper;

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
 * Test class for the TuteurResource REST controller.
 *
 * @see TuteurResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrmisticApp.class)
public class TuteurResourceIntTest {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String DEFAULT_MAIL = "AAAAAAAAAA";
    private static final String UPDATED_MAIL = "BBBBBBBBBB";

    @Inject
    private TuteurRepository tuteurRepository;

    @Inject
    private TuteurMapper tuteurMapper;

    @Inject
    private TuteurService tuteurService;

    @Inject
    private TuteurSearchRepository tuteurSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restTuteurMockMvc;

    private Tuteur tuteur;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TuteurResource tuteurResource = new TuteurResource();
        ReflectionTestUtils.setField(tuteurResource, "tuteurService", tuteurService);
        this.restTuteurMockMvc = MockMvcBuilders.standaloneSetup(tuteurResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tuteur createEntity(EntityManager em) {
        Tuteur tuteur = new Tuteur()
                .nom(DEFAULT_NOM)
                .prenom(DEFAULT_PRENOM)
                .telephone(DEFAULT_TELEPHONE)
                .mail(DEFAULT_MAIL);
        return tuteur;
    }

    @Before
    public void initTest() {
        tuteurSearchRepository.deleteAll();
        tuteur = createEntity(em);
    }

    @Test
    @Transactional
    public void createTuteur() throws Exception {
        int databaseSizeBeforeCreate = tuteurRepository.findAll().size();

        // Create the Tuteur
        TuteurDTO tuteurDTO = tuteurMapper.tuteurToTuteurDTO(tuteur);

        restTuteurMockMvc.perform(post("/api/tuteurs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tuteurDTO)))
            .andExpect(status().isCreated());

        // Validate the Tuteur in the database
        List<Tuteur> tuteurList = tuteurRepository.findAll();
        assertThat(tuteurList).hasSize(databaseSizeBeforeCreate + 1);
        Tuteur testTuteur = tuteurList.get(tuteurList.size() - 1);
        assertThat(testTuteur.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testTuteur.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testTuteur.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testTuteur.getMail()).isEqualTo(DEFAULT_MAIL);

        // Validate the Tuteur in ElasticSearch
        Tuteur tuteurEs = tuteurSearchRepository.findOne(testTuteur.getId());
        assertThat(tuteurEs).isEqualToComparingFieldByField(testTuteur);
    }

    @Test
    @Transactional
    public void createTuteurWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tuteurRepository.findAll().size();

        // Create the Tuteur with an existing ID
        Tuteur existingTuteur = new Tuteur();
        existingTuteur.setId(1L);
        TuteurDTO existingTuteurDTO = tuteurMapper.tuteurToTuteurDTO(existingTuteur);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTuteurMockMvc.perform(post("/api/tuteurs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingTuteurDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Tuteur> tuteurList = tuteurRepository.findAll();
        assertThat(tuteurList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllTuteurs() throws Exception {
        // Initialize the database
        tuteurRepository.saveAndFlush(tuteur);

        // Get all the tuteurList
        restTuteurMockMvc.perform(get("/api/tuteurs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tuteur.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE.toString())))
            .andExpect(jsonPath("$.[*].mail").value(hasItem(DEFAULT_MAIL.toString())));
    }

    @Test
    @Transactional
    public void getTuteur() throws Exception {
        // Initialize the database
        tuteurRepository.saveAndFlush(tuteur);

        // Get the tuteur
        restTuteurMockMvc.perform(get("/api/tuteurs/{id}", tuteur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tuteur.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM.toString()))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM.toString()))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE.toString()))
            .andExpect(jsonPath("$.mail").value(DEFAULT_MAIL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTuteur() throws Exception {
        // Get the tuteur
        restTuteurMockMvc.perform(get("/api/tuteurs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTuteur() throws Exception {
        // Initialize the database
        tuteurRepository.saveAndFlush(tuteur);
        tuteurSearchRepository.save(tuteur);
        int databaseSizeBeforeUpdate = tuteurRepository.findAll().size();

        // Update the tuteur
        Tuteur updatedTuteur = tuteurRepository.findOne(tuteur.getId());
        updatedTuteur
                .nom(UPDATED_NOM)
                .prenom(UPDATED_PRENOM)
                .telephone(UPDATED_TELEPHONE)
                .mail(UPDATED_MAIL);
        TuteurDTO tuteurDTO = tuteurMapper.tuteurToTuteurDTO(updatedTuteur);

        restTuteurMockMvc.perform(put("/api/tuteurs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tuteurDTO)))
            .andExpect(status().isOk());

        // Validate the Tuteur in the database
        List<Tuteur> tuteurList = tuteurRepository.findAll();
        assertThat(tuteurList).hasSize(databaseSizeBeforeUpdate);
        Tuteur testTuteur = tuteurList.get(tuteurList.size() - 1);
        assertThat(testTuteur.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testTuteur.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testTuteur.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testTuteur.getMail()).isEqualTo(UPDATED_MAIL);

        // Validate the Tuteur in ElasticSearch
        Tuteur tuteurEs = tuteurSearchRepository.findOne(testTuteur.getId());
        assertThat(tuteurEs).isEqualToComparingFieldByField(testTuteur);
    }

    @Test
    @Transactional
    public void updateNonExistingTuteur() throws Exception {
        int databaseSizeBeforeUpdate = tuteurRepository.findAll().size();

        // Create the Tuteur
        TuteurDTO tuteurDTO = tuteurMapper.tuteurToTuteurDTO(tuteur);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTuteurMockMvc.perform(put("/api/tuteurs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tuteurDTO)))
            .andExpect(status().isCreated());

        // Validate the Tuteur in the database
        List<Tuteur> tuteurList = tuteurRepository.findAll();
        assertThat(tuteurList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTuteur() throws Exception {
        // Initialize the database
        tuteurRepository.saveAndFlush(tuteur);
        tuteurSearchRepository.save(tuteur);
        int databaseSizeBeforeDelete = tuteurRepository.findAll().size();

        // Get the tuteur
        restTuteurMockMvc.perform(delete("/api/tuteurs/{id}", tuteur.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean tuteurExistsInEs = tuteurSearchRepository.exists(tuteur.getId());
        assertThat(tuteurExistsInEs).isFalse();

        // Validate the database is empty
        List<Tuteur> tuteurList = tuteurRepository.findAll();
        assertThat(tuteurList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTuteur() throws Exception {
        // Initialize the database
        tuteurRepository.saveAndFlush(tuteur);
        tuteurSearchRepository.save(tuteur);

        // Search the tuteur
        restTuteurMockMvc.perform(get("/api/_search/tuteurs?query=id:" + tuteur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tuteur.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE.toString())))
            .andExpect(jsonPath("$.[*].mail").value(hasItem(DEFAULT_MAIL.toString())));
    }
}
