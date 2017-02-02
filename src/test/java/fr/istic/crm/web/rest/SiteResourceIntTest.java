package fr.istic.crm.web.rest;

import fr.istic.crm.CrmisticApp;

import fr.istic.crm.domain.Site;
import fr.istic.crm.repository.SiteRepository;
import fr.istic.crm.service.SiteService;
import fr.istic.crm.repository.search.SiteSearchRepository;
import fr.istic.crm.service.dto.SiteDTO;
import fr.istic.crm.service.mapper.SiteMapper;

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
 * Test class for the SiteResource REST controller.
 *
 * @see SiteResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrmisticApp.class)
public class SiteResourceIntTest {

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final String DEFAULT_CODE_POSTAL = "AAAAAAAAAA";
    private static final String UPDATED_CODE_POSTAL = "BBBBBBBBBB";

    private static final String DEFAULT_VILLE = "AAAAAAAAAA";
    private static final String UPDATED_VILLE = "BBBBBBBBBB";

    private static final String DEFAULT_PAYS = "AAAAAAAAAA";
    private static final String UPDATED_PAYS = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    @Inject
    private SiteRepository siteRepository;

    @Inject
    private SiteMapper siteMapper;

    @Inject
    private SiteService siteService;

    @Inject
    private SiteSearchRepository siteSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restSiteMockMvc;

    private Site site;

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
        SiteResource siteResource = new SiteResource();
        ReflectionTestUtils.setField(siteResource, "siteService", siteService);
        this.restSiteMockMvc = MockMvcBuilders.standaloneSetup(siteResource)
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
    public static Site createEntity(EntityManager em) {
        Site site = new Site()
                .adresse(DEFAULT_ADRESSE)
                .codePostal(DEFAULT_CODE_POSTAL)
                .ville(DEFAULT_VILLE)
                .pays(DEFAULT_PAYS)
                .telephone(DEFAULT_TELEPHONE);
        return site;
    }

    @Before
    public void initTest() {
        siteSearchRepository.deleteAll();
        setTimestampCreation(new Long(new Date().getTime()));
        site = createEntity(em);
    }

    @Test
    @Transactional
    public void createSite() throws Exception {
        int databaseSizeBeforeCreate = siteRepository.findAll().size();

        // Create the Site
        SiteDTO siteDTO = siteMapper.siteToSiteDTO(site);

        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isCreated());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeCreate + 1);
        Site testSite = siteList.get(siteList.size() - 1);
        assertThat(testSite.getAdresse()).isEqualTo(DEFAULT_ADRESSE);
        assertThat(testSite.getCodePostal()).isEqualTo(DEFAULT_CODE_POSTAL);
        assertThat(testSite.getVille()).isEqualTo(DEFAULT_VILLE);
        assertThat(testSite.getPays()).isEqualTo(DEFAULT_PAYS);
        assertThat(testSite.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testSite.getDateCreation().longValue() == getTimestampCreation().longValue());
        assertThat(testSite.getDateModification().longValue() == getTimestampCreation().longValue());

        // Validate the Site in ElasticSearch
        Site siteEs = siteSearchRepository.findOne(testSite.getId());
        assertThat(siteEs).isEqualToComparingFieldByField(testSite);
    }

    @Test
    @Transactional
    public void createSiteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = siteRepository.findAll().size();

        // Create the Site with an existing ID
        Site existingSite = new Site();
        existingSite.setId(1L);
        SiteDTO existingSiteDTO = siteMapper.siteToSiteDTO(existingSite);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSiteMockMvc.perform(post("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingSiteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllSites() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get all the siteList
        restSiteMockMvc.perform(get("/api/sites?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(site.getId().intValue())))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE.toString())))
            .andExpect(jsonPath("$.[*].codePostal").value(hasItem(DEFAULT_CODE_POSTAL.toString())))
            .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE.toString())))
            .andExpect(jsonPath("$.[*].pays").value(hasItem(DEFAULT_PAYS.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE.toString())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(site.getDateCreation().longValue())))
            .andExpect(jsonPath("$.[*].dateModification").value(hasItem(site.getDateModification().longValue())));
    }

    @Test
    @Transactional
    public void getSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);

        // Get the site
        restSiteMockMvc.perform(get("/api/sites/{id}", site.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(site.getId().intValue()))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE.toString()))
            .andExpect(jsonPath("$.codePostal").value(DEFAULT_CODE_POSTAL.toString()))
            .andExpect(jsonPath("$.ville").value(DEFAULT_VILLE.toString()))
            .andExpect(jsonPath("$.pays").value(DEFAULT_PAYS.toString()))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE.toString()))
            .andExpect(jsonPath("$.dateCreation").value(site.getDateCreation().longValue()))
            .andExpect(jsonPath("$.dateModification").value(site.getDateModification().longValue()));
    }

    @Test
    @Transactional
    public void getNonExistingSite() throws Exception {
        // Get the site
        restSiteMockMvc.perform(get("/api/sites/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);
        siteSearchRepository.save(site);
        int databaseSizeBeforeUpdate = siteRepository.findAll().size();

        // Update the site
        Site updatedSite = siteRepository.findOne(site.getId());

        setTimestampModification(new Date().getTime());

        // dateCreation and dateModification are updated automatically using envers
        updatedSite
                .adresse(UPDATED_ADRESSE)
                .codePostal(UPDATED_CODE_POSTAL)
                .ville(UPDATED_VILLE)
                .pays(UPDATED_PAYS)
                .telephone(UPDATED_TELEPHONE);
        SiteDTO siteDTO = siteMapper.siteToSiteDTO(updatedSite);

        restSiteMockMvc.perform(put("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isOk());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate);
        Site testSite = siteList.get(siteList.size() - 1);
        assertThat(testSite.getAdresse()).isEqualTo(UPDATED_ADRESSE);
        assertThat(testSite.getCodePostal()).isEqualTo(UPDATED_CODE_POSTAL);
        assertThat(testSite.getVille()).isEqualTo(UPDATED_VILLE);
        assertThat(testSite.getPays()).isEqualTo(UPDATED_PAYS);
        assertThat(testSite.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testSite.getDateCreation().longValue() == getTimestampCreation().longValue());
        assertThat(testSite.getDateModification().longValue() == getTimestampModification().longValue());

        assertThat(testSite.getDateCreation().longValue()).isLessThan(testSite.getDateModification().longValue());

        // Validate the Site in ElasticSearch
        Site siteEs = siteSearchRepository.findOne(testSite.getId());
        assertThat(siteEs).isEqualToIgnoringGivenFields(testSite, "dateModification");
    }

    @Test
    @Transactional
    public void updateNonExistingSite() throws Exception {
        int databaseSizeBeforeUpdate = siteRepository.findAll().size();

        // Create the Site
        SiteDTO siteDTO = siteMapper.siteToSiteDTO(site);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSiteMockMvc.perform(put("/api/sites")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(siteDTO)))
            .andExpect(status().isCreated());

        // Validate the Site in the database
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);
        siteSearchRepository.save(site);
        int databaseSizeBeforeDelete = siteRepository.findAll().size();

        // Get the site
        restSiteMockMvc.perform(delete("/api/sites/{id}", site.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean siteExistsInEs = siteSearchRepository.exists(site.getId());
        assertThat(siteExistsInEs).isFalse();

        // Validate the database is empty
        List<Site> siteList = siteRepository.findAll();
        assertThat(siteList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSite() throws Exception {
        // Initialize the database
        siteRepository.saveAndFlush(site);
        siteSearchRepository.save(site);

        // Search the site
        restSiteMockMvc.perform(get("/api/_search/sites?query=id:" + site.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(site.getId().intValue())))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE.toString())))
            .andExpect(jsonPath("$.[*].codePostal").value(hasItem(DEFAULT_CODE_POSTAL.toString())))
            .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE.toString())))
            .andExpect(jsonPath("$.[*].pays").value(hasItem(DEFAULT_PAYS.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE.toString())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(site.getDateCreation().longValue())))
            .andExpect(jsonPath("$.[*].dateModification").value(hasItem(site.getDateModification().longValue())));
    }
}
