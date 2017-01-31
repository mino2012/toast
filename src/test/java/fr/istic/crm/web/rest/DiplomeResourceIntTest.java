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
import java.util.Date;
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
     *
     * dateCreation and dateModification are automatically set using envers
     */
    public static Diplome createEntity(EntityManager em) {
        Diplome diplome = new Diplome()
                .nom(DEFAULT_NOM);
        return diplome;
    }

    @Before
    public void initTest() {
        diplomeSearchRepository.deleteAll();
        setTimestampCreation(new Long(new Date().getTime()));
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
        assertThat(testDiplome.getDateCreation().longValue() == getTimestampCreation().longValue());
        assertThat(testDiplome.getDateModification().longValue() == getTimestampCreation().longValue());

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
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(diplome.getDateCreation().longValue())))
            .andExpect(jsonPath("$.[*].dateModification").value(hasItem(diplome.getDateModification().longValue())));
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
            .andExpect(jsonPath("$.dateCreation").value(diplome.getDateCreation().longValue()))
            .andExpect(jsonPath("$.dateModification").value(diplome.getDateModification().longValue()));
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

        setTimestampModification(new Date().getTime());

        // dateCreation and dateModification are updated automatically using envers
        updatedDiplome
                .nom(UPDATED_NOM);
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
        assertThat(testDiplome.getDateCreation().longValue() == getTimestampCreation().longValue());
        assertThat(testDiplome.getDateModification().longValue() == getTimestampModification().longValue());

        assertThat(testDiplome.getDateCreation().longValue()).isLessThan(testDiplome.getDateModification().longValue());

        // Validate the Diplome in ElasticSearch
        Diplome diplomeEs = diplomeSearchRepository.findOne(testDiplome.getId());
        assertThat(diplomeEs).isEqualToIgnoringGivenFields(testDiplome, "dateModification");
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
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(diplome.getDateCreation().longValue())))
            .andExpect(jsonPath("$.[*].dateModification").value(hasItem(diplome.getDateModification().longValue())));
    }
}
