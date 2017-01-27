package fr.istic.crm.service.impl;

import fr.istic.crm.service.SiteService;
import fr.istic.crm.domain.Site;
import fr.istic.crm.repository.SiteRepository;
import fr.istic.crm.repository.search.SiteSearchRepository;
import fr.istic.crm.service.dto.SiteDTO;
import fr.istic.crm.service.mapper.SiteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Site.
 */
@Service
@Transactional
public class SiteServiceImpl implements SiteService{

    private final Logger log = LoggerFactory.getLogger(SiteServiceImpl.class);
    
    @Inject
    private SiteRepository siteRepository;

    @Inject
    private SiteMapper siteMapper;

    @Inject
    private SiteSearchRepository siteSearchRepository;

    /**
     * Save a site.
     *
     * @param siteDTO the entity to save
     * @return the persisted entity
     */
    public SiteDTO save(SiteDTO siteDTO) {
        log.debug("Request to save Site : {}", siteDTO);
        Site site = siteMapper.siteDTOToSite(siteDTO);
        site = siteRepository.save(site);
        SiteDTO result = siteMapper.siteToSiteDTO(site);
        siteSearchRepository.save(site);
        return result;
    }

    /**
     *  Get all the sites.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<SiteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Sites");
        Page<Site> result = siteRepository.findAll(pageable);
        return result.map(site -> siteMapper.siteToSiteDTO(site));
    }

    /**
     *  Get one site by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public SiteDTO findOne(Long id) {
        log.debug("Request to get Site : {}", id);
        Site site = siteRepository.findOne(id);
        SiteDTO siteDTO = siteMapper.siteToSiteDTO(site);
        return siteDTO;
    }

    /**
     *  Delete the  site by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Site : {}", id);
        siteRepository.delete(id);
        siteSearchRepository.delete(id);
    }

    /**
     * Search for the site corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<SiteDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Sites for query {}", query);
        Page<Site> result = siteSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(site -> siteMapper.siteToSiteDTO(site));
    }
}
