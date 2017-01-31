package fr.istic.crm.web.rest;

import fr.istic.crm.CrmisticApp;

import fr.istic.crm.domain.Professionnel;
import fr.istic.crm.repository.ProfessionnelRepository;
import fr.istic.crm.service.ProfessionnelService;
import fr.istic.crm.repository.search.ProfessionnelSearchRepository;
import fr.istic.crm.service.dto.ProfessionnelDTO;
import fr.istic.crm.service.mapper.ProfessionnelMapper;

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
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProfessionnelResource REST controller.
 *
 * @see ProfessionnelResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrmisticApp.class)
public class ProfessionnelResourceIntTest {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String DEFAULT_MAIL = "AAAAAAAAAA";
    private static final String UPDATED_MAIL = "BBBBBBBBBB";

    private static final String DEFAULT_FONCTION = "AAAAAAAAAA";
    private static final String UPDATED_FONCTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ANCIEN_ETUDIANT = false;
    private static final Boolean UPDATED_ANCIEN_ETUDIANT = true;

    @Inject
    private ProfessionnelRepository professionnelRepository;

    @Inject
    private ProfessionnelMapper professionnelMapper;

    @Inject
    private ProfessionnelService professionnelService;

    @Inject
    private ProfessionnelSearchRepository professionnelSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restProfessionnelMockMvc;

    private Professionnel professionnel;

    private Long timestampCreation;

    private Long timestampModification;

    public Long getTimestampCreation() {
        return timestampCreation;
    }

    public void setTimestampCreation(Long timestampCreation) {
        this.timestampCreation = timestampCreation;
    }

    public Long getTimestampModification() {
        return timestampModification;
    }

    public void setTimestampModification(Long timestampModification) {
        this.timestampModification = timestampModification;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProfessionnelResource professionnelResource = new ProfessionnelResource();
        ReflectionTestUtils.setField(professionnelResource, "professionnelService", professionnelService);
        this.restProfessionnelMockMvc = MockMvcBuilders.standaloneSetup(professionnelResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     *
     * dateCreation and dateModification are automatically set using envers
     */
    public static Professionnel createEntity(EntityManager em) {
        Professionnel professionnel = new Professionnel()
                .nom(DEFAULT_NOM)
                .prenom(DEFAULT_PRENOM)
                .telephone(DEFAULT_TELEPHONE)
                .mail(DEFAULT_MAIL)
                .fonction(DEFAULT_FONCTION)
                .ancienEtudiant(DEFAULT_ANCIEN_ETUDIANT);
        return professionnel;
    }

    @Before
    public void initTest() {
        professionnelSearchRepository.deleteAll();
        setTimestampCreation(new Long(new Date().getTime()));
        professionnel = createEntity(em);
    }

    @Test
    @Transactional
    public void createProfessionnel() throws Exception {
        int databaseSizeBeforeCreate = professionnelRepository.findAll().size();

        // Create the Professionnel
        ProfessionnelDTO professionnelDTO = professionnelMapper.professionnelToProfessionnelDTO(professionnel);

        restProfessionnelMockMvc.perform(post("/api/professionnels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(professionnelDTO)))
            .andExpect(status().isCreated());

        // Validate the Professionnel in the database
        List<Professionnel> professionnelList = professionnelRepository.findAll();
        assertThat(professionnelList).hasSize(databaseSizeBeforeCreate + 1);
        Professionnel testProfessionnel = professionnelList.get(professionnelList.size() - 1);
        assertThat(testProfessionnel.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testProfessionnel.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testProfessionnel.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testProfessionnel.getMail()).isEqualTo(DEFAULT_MAIL);
        assertThat(testProfessionnel.getFonction()).isEqualTo(DEFAULT_FONCTION);
        assertThat(testProfessionnel.isAncienEtudiant()).isEqualTo(DEFAULT_ANCIEN_ETUDIANT);
        assertThat(testProfessionnel.getDateCreation().longValue() == getTimestampCreation().longValue());
        assertThat(testProfessionnel.getDateModification().longValue() == getTimestampCreation().longValue());

        // Validate the Professionnel in ElasticSearch
        Professionnel professionnelEs = professionnelSearchRepository.findOne(testProfessionnel.getId());
        assertThat(professionnelEs).isEqualToComparingFieldByField(testProfessionnel);
    }

    @Test
    @Transactional
    public void createProfessionnelWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = professionnelRepository.findAll().size();

        // Create the Professionnel with an existing ID
        Professionnel existingProfessionnel = new Professionnel();
        existingProfessionnel.setId(1L);
        ProfessionnelDTO existingProfessionnelDTO = professionnelMapper.professionnelToProfessionnelDTO(existingProfessionnel);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfessionnelMockMvc.perform(post("/api/professionnels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingProfessionnelDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Professionnel> professionnelList = professionnelRepository.findAll();
        assertThat(professionnelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllProfessionnels() throws Exception {
        // Initialize the database
        professionnelRepository.saveAndFlush(professionnel);

        // Get all the professionnelList
        restProfessionnelMockMvc.perform(get("/api/professionnels?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(professionnel.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE.toString())))
            .andExpect(jsonPath("$.[*].mail").value(hasItem(DEFAULT_MAIL.toString())))
            .andExpect(jsonPath("$.[*].fonction").value(hasItem(DEFAULT_FONCTION.toString())))
            .andExpect(jsonPath("$.[*].ancienEtudiant").value(hasItem(DEFAULT_ANCIEN_ETUDIANT.booleanValue())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(professionnel.getDateCreation().longValue())))
            .andExpect(jsonPath("$.[*].dateModification").value(hasItem(professionnel.getDateModification().longValue())));
    }

    @Test
    @Transactional
    public void getProfessionnel() throws Exception {
        // Initialize the database
        professionnelRepository.saveAndFlush(professionnel);

        // Get the professionnel
        restProfessionnelMockMvc.perform(get("/api/professionnels/{id}", professionnel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(professionnel.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM.toString()))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM.toString()))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE.toString()))
            .andExpect(jsonPath("$.mail").value(DEFAULT_MAIL.toString()))
            .andExpect(jsonPath("$.fonction").value(DEFAULT_FONCTION.toString()))
            .andExpect(jsonPath("$.ancienEtudiant").value(DEFAULT_ANCIEN_ETUDIANT.booleanValue()))
            .andExpect(jsonPath("$.dateCreation").value(professionnel.getDateCreation().longValue()))
            .andExpect(jsonPath("$.dateModification").value(professionnel.getDateModification().longValue()));
    }

    @Test
    @Transactional
    public void getNonExistingProfessionnel() throws Exception {
        // Get the professionnel
        restProfessionnelMockMvc.perform(get("/api/professionnels/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProfessionnel() throws Exception {
        // Initialize the database
        professionnelRepository.saveAndFlush(professionnel);
        professionnelSearchRepository.save(professionnel);
        int databaseSizeBeforeUpdate = professionnelRepository.findAll().size();

        // Update the professionnel
        Professionnel updatedProfessionnel = professionnelRepository.findOne(professionnel.getId());

        setTimestampModification(new Date().getTime());

        // dateCreation and dateModification are updated automatically using envers
        updatedProfessionnel
                .nom(UPDATED_NOM)
                .prenom(UPDATED_PRENOM)
                .telephone(UPDATED_TELEPHONE)
                .mail(UPDATED_MAIL)
                .fonction(UPDATED_FONCTION)
                .ancienEtudiant(UPDATED_ANCIEN_ETUDIANT);
        ProfessionnelDTO professionnelDTO = professionnelMapper.professionnelToProfessionnelDTO(updatedProfessionnel);

        restProfessionnelMockMvc.perform(put("/api/professionnels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(professionnelDTO)))
            .andExpect(status().isOk());

        // Validate the Professionnel in the database
        List<Professionnel> professionnelList = professionnelRepository.findAll();
        assertThat(professionnelList).hasSize(databaseSizeBeforeUpdate);
        Professionnel testProfessionnel = professionnelList.get(professionnelList.size() - 1);
        assertThat(testProfessionnel.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testProfessionnel.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testProfessionnel.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testProfessionnel.getMail()).isEqualTo(UPDATED_MAIL);
        assertThat(testProfessionnel.getFonction()).isEqualTo(UPDATED_FONCTION);
        assertThat(testProfessionnel.isAncienEtudiant()).isEqualTo(UPDATED_ANCIEN_ETUDIANT);
        assertThat(testProfessionnel.getDateCreation().longValue() == getTimestampCreation().longValue());
        assertThat(testProfessionnel.getDateModification().longValue() ==  getTimestampModification().longValue());
        assertThat(testProfessionnel.getDateCreation().longValue()).isLessThan(testProfessionnel.getDateModification().longValue());

        // Validate the Professionnel in ElasticSearch
        Professionnel professionnelEs = professionnelSearchRepository.findOne(testProfessionnel.getId());
        assertThat(professionnelEs).isEqualToIgnoringGivenFields(testProfessionnel, "dateModification");
    }

    @Test
    @Transactional
    public void updateNonExistingProfessionnel() throws Exception {
        int databaseSizeBeforeUpdate = professionnelRepository.findAll().size();

        // Create the Professionnel
        ProfessionnelDTO professionnelDTO = professionnelMapper.professionnelToProfessionnelDTO(professionnel);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProfessionnelMockMvc.perform(put("/api/professionnels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(professionnelDTO)))
            .andExpect(status().isCreated());

        // Validate the Professionnel in the database
        List<Professionnel> professionnelList = professionnelRepository.findAll();
        assertThat(professionnelList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteProfessionnel() throws Exception {
        // Initialize the database
        professionnelRepository.saveAndFlush(professionnel);
        professionnelSearchRepository.save(professionnel);
        int databaseSizeBeforeDelete = professionnelRepository.findAll().size();

        // Get the professionnel
        restProfessionnelMockMvc.perform(delete("/api/professionnels/{id}", professionnel.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean professionnelExistsInEs = professionnelSearchRepository.exists(professionnel.getId());
        assertThat(professionnelExistsInEs).isFalse();

        // Validate the database is empty
        List<Professionnel> professionnelList = professionnelRepository.findAll();
        assertThat(professionnelList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProfessionnel() throws Exception {
        // Initialize the database
        professionnelRepository.saveAndFlush(professionnel);
        professionnelSearchRepository.save(professionnel);

        // Search the professionnel
        restProfessionnelMockMvc.perform(get("/api/_search/professionnels?query=id:" + professionnel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(professionnel.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE.toString())))
            .andExpect(jsonPath("$.[*].mail").value(hasItem(DEFAULT_MAIL.toString())))
            .andExpect(jsonPath("$.[*].fonction").value(hasItem(DEFAULT_FONCTION.toString())))
            .andExpect(jsonPath("$.[*].ancienEtudiant").value(hasItem(DEFAULT_ANCIEN_ETUDIANT.booleanValue())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(professionnel.getDateCreation().longValue())))
            .andExpect(jsonPath("$.[*].dateModification").value(hasItem(professionnel.getDateModification().longValue())));
    }
}
