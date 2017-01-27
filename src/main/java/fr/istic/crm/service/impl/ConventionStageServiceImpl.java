package fr.istic.crm.service.impl;

import fr.istic.crm.service.ConventionStageService;
import fr.istic.crm.domain.ConventionStage;
import fr.istic.crm.repository.ConventionStageRepository;
import fr.istic.crm.repository.search.ConventionStageSearchRepository;
import fr.istic.crm.service.dto.ConventionStageDTO;
import fr.istic.crm.service.mapper.ConventionStageMapper;
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
 * Service Implementation for managing ConventionStage.
 */
@Service
@Transactional
public class ConventionStageServiceImpl implements ConventionStageService{

    private final Logger log = LoggerFactory.getLogger(ConventionStageServiceImpl.class);
    
    @Inject
    private ConventionStageRepository conventionStageRepository;

    @Inject
    private ConventionStageMapper conventionStageMapper;

    @Inject
    private ConventionStageSearchRepository conventionStageSearchRepository;

    /**
     * Save a conventionStage.
     *
     * @param conventionStageDTO the entity to save
     * @return the persisted entity
     */
    public ConventionStageDTO save(ConventionStageDTO conventionStageDTO) {
        log.debug("Request to save ConventionStage : {}", conventionStageDTO);
        ConventionStage conventionStage = conventionStageMapper.conventionStageDTOToConventionStage(conventionStageDTO);
        conventionStage = conventionStageRepository.save(conventionStage);
        ConventionStageDTO result = conventionStageMapper.conventionStageToConventionStageDTO(conventionStage);
        conventionStageSearchRepository.save(conventionStage);
        return result;
    }

    /**
     *  Get all the conventionStages.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ConventionStageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ConventionStages");
        Page<ConventionStage> result = conventionStageRepository.findAll(pageable);
        return result.map(conventionStage -> conventionStageMapper.conventionStageToConventionStageDTO(conventionStage));
    }

    /**
     *  Get one conventionStage by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ConventionStageDTO findOne(Long id) {
        log.debug("Request to get ConventionStage : {}", id);
        ConventionStage conventionStage = conventionStageRepository.findOne(id);
        ConventionStageDTO conventionStageDTO = conventionStageMapper.conventionStageToConventionStageDTO(conventionStage);
        return conventionStageDTO;
    }

    /**
     *  Delete the  conventionStage by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ConventionStage : {}", id);
        conventionStageRepository.delete(id);
        conventionStageSearchRepository.delete(id);
    }

    /**
     * Search for the conventionStage corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ConventionStageDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ConventionStages for query {}", query);
        Page<ConventionStage> result = conventionStageSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(conventionStage -> conventionStageMapper.conventionStageToConventionStageDTO(conventionStage));
    }
}
