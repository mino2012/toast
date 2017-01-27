package fr.istic.crm.web.rest;

import fr.istic.crm.CrmisticApp;

import fr.istic.crm.domain.Groupe;
import fr.istic.crm.repository.GroupeRepository;
import fr.istic.crm.service.GroupeService;
import fr.istic.crm.repository.search.GroupeSearchRepository;
import fr.istic.crm.service.dto.GroupeDTO;
import fr.istic.crm.service.mapper.GroupeMapper;

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
 * Test class for the GroupeResource REST controller.
 *
 * @see GroupeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrmisticApp.class)
public class GroupeResourceIntTest {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Long DEFAULT_DEBUT_VERSION = 1L;
    private static final Long UPDATED_DEBUT_VERSION = 2L;

    private static final Long DEFAULT_FIN_VERSION = 1L;
    private static final Long UPDATED_FIN_VERSION = 2L;

    @Inject
    private GroupeRepository groupeRepository;

    @Inject
    private GroupeMapper groupeMapper;

    @Inject
    private GroupeService groupeService;

    @Inject
    private GroupeSearchRepository groupeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restGroupeMockMvc;

    private Groupe groupe;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        GroupeResource groupeResource = new GroupeResource();
        ReflectionTestUtils.setField(groupeResource, "groupeService", groupeService);
        this.restGroupeMockMvc = MockMvcBuilders.standaloneSetup(groupeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Groupe createEntity(EntityManager em) {
        Groupe groupe = new Groupe()
                .nom(DEFAULT_NOM)
                .debutVersion(DEFAULT_DEBUT_VERSION)
                .finVersion(DEFAULT_FIN_VERSION);
        return groupe;
    }

    @Before
    public void initTest() {
        groupeSearchRepository.deleteAll();
        groupe = createEntity(em);
    }

    @Test
    @Transactional
    public void createGroupe() throws Exception {
        int databaseSizeBeforeCreate = groupeRepository.findAll().size();

        // Create the Groupe
        GroupeDTO groupeDTO = groupeMapper.groupeToGroupeDTO(groupe);

        restGroupeMockMvc.perform(post("/api/groupes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(groupeDTO)))
            .andExpect(status().isCreated());

        // Validate the Groupe in the database
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeCreate + 1);
        Groupe testGroupe = groupeList.get(groupeList.size() - 1);
        assertThat(testGroupe.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testGroupe.getDebutVersion()).isEqualTo(DEFAULT_DEBUT_VERSION);
        assertThat(testGroupe.getFinVersion()).isEqualTo(DEFAULT_FIN_VERSION);

        // Validate the Groupe in ElasticSearch
        Groupe groupeEs = groupeSearchRepository.findOne(testGroupe.getId());
        assertThat(groupeEs).isEqualToComparingFieldByField(testGroupe);
    }

    @Test
    @Transactional
    public void createGroupeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = groupeRepository.findAll().size();

        // Create the Groupe with an existing ID
        Groupe existingGroupe = new Groupe();
        existingGroupe.setId(1L);
        GroupeDTO existingGroupeDTO = groupeMapper.groupeToGroupeDTO(existingGroupe);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGroupeMockMvc.perform(post("/api/groupes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingGroupeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllGroupes() throws Exception {
        // Initialize the database
        groupeRepository.saveAndFlush(groupe);

        // Get all the groupeList
        restGroupeMockMvc.perform(get("/api/groupes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groupe.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].debutVersion").value(hasItem(DEFAULT_DEBUT_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].finVersion").value(hasItem(DEFAULT_FIN_VERSION.intValue())));
    }

    @Test
    @Transactional
    public void getGroupe() throws Exception {
        // Initialize the database
        groupeRepository.saveAndFlush(groupe);

        // Get the groupe
        restGroupeMockMvc.perform(get("/api/groupes/{id}", groupe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(groupe.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM.toString()))
            .andExpect(jsonPath("$.debutVersion").value(DEFAULT_DEBUT_VERSION.intValue()))
            .andExpect(jsonPath("$.finVersion").value(DEFAULT_FIN_VERSION.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingGroupe() throws Exception {
        // Get the groupe
        restGroupeMockMvc.perform(get("/api/groupes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGroupe() throws Exception {
        // Initialize the database
        groupeRepository.saveAndFlush(groupe);
        groupeSearchRepository.save(groupe);
        int databaseSizeBeforeUpdate = groupeRepository.findAll().size();

        // Update the groupe
        Groupe updatedGroupe = groupeRepository.findOne(groupe.getId());
        updatedGroupe
                .nom(UPDATED_NOM)
                .debutVersion(UPDATED_DEBUT_VERSION)
                .finVersion(UPDATED_FIN_VERSION);
        GroupeDTO groupeDTO = groupeMapper.groupeToGroupeDTO(updatedGroupe);

        restGroupeMockMvc.perform(put("/api/groupes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(groupeDTO)))
            .andExpect(status().isOk());

        // Validate the Groupe in the database
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeUpdate);
        Groupe testGroupe = groupeList.get(groupeList.size() - 1);
        assertThat(testGroupe.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testGroupe.getDebutVersion()).isEqualTo(UPDATED_DEBUT_VERSION);
        assertThat(testGroupe.getFinVersion()).isEqualTo(UPDATED_FIN_VERSION);

        // Validate the Groupe in ElasticSearch
        Groupe groupeEs = groupeSearchRepository.findOne(testGroupe.getId());
        assertThat(groupeEs).isEqualToComparingFieldByField(testGroupe);
    }

    @Test
    @Transactional
    public void updateNonExistingGroupe() throws Exception {
        int databaseSizeBeforeUpdate = groupeRepository.findAll().size();

        // Create the Groupe
        GroupeDTO groupeDTO = groupeMapper.groupeToGroupeDTO(groupe);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restGroupeMockMvc.perform(put("/api/groupes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(groupeDTO)))
            .andExpect(status().isCreated());

        // Validate the Groupe in the database
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteGroupe() throws Exception {
        // Initialize the database
        groupeRepository.saveAndFlush(groupe);
        groupeSearchRepository.save(groupe);
        int databaseSizeBeforeDelete = groupeRepository.findAll().size();

        // Get the groupe
        restGroupeMockMvc.perform(delete("/api/groupes/{id}", groupe.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean groupeExistsInEs = groupeSearchRepository.exists(groupe.getId());
        assertThat(groupeExistsInEs).isFalse();

        // Validate the database is empty
        List<Groupe> groupeList = groupeRepository.findAll();
        assertThat(groupeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchGroupe() throws Exception {
        // Initialize the database
        groupeRepository.saveAndFlush(groupe);
        groupeSearchRepository.save(groupe);

        // Search the groupe
        restGroupeMockMvc.perform(get("/api/_search/groupes?query=id:" + groupe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groupe.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())))
            .andExpect(jsonPath("$.[*].debutVersion").value(hasItem(DEFAULT_DEBUT_VERSION.intValue())))
            .andExpect(jsonPath("$.[*].finVersion").value(hasItem(DEFAULT_FIN_VERSION.intValue())));
    }
}
