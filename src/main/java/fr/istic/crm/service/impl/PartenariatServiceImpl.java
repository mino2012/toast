package fr.istic.crm.service.impl;

import fr.istic.crm.service.PartenariatService;
import fr.istic.crm.domain.Partenariat;
import fr.istic.crm.repository.PartenariatRepository;
import fr.istic.crm.repository.search.PartenariatSearchRepository;
import fr.istic.crm.service.dto.PartenariatDTO;
import fr.istic.crm.service.mapper.PartenariatMapper;
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
 * Service Implementation for managing Partenariat.
 */
@Service
@Transactional
public class PartenariatServiceImpl implements PartenariatService{

    private final Logger log = LoggerFactory.getLogger(PartenariatServiceImpl.class);
    
    @Inject
    private PartenariatRepository partenariatRepository;

    @Inject
    private PartenariatMapper partenariatMapper;

    @Inject
    private PartenariatSearchRepository partenariatSearchRepository;

    /**
     * Save a partenariat.
     *
     * @param partenariatDTO the entity to save
     * @return the persisted entity
     */
    public PartenariatDTO save(PartenariatDTO partenariatDTO) {
        log.debug("Request to save Partenariat : {}", partenariatDTO);
        Partenariat partenariat = partenariatMapper.partenariatDTOToPartenariat(partenariatDTO);
        partenariat = partenariatRepository.save(partenariat);
        PartenariatDTO result = partenariatMapper.partenariatToPartenariatDTO(partenariat);
        partenariatSearchRepository.save(partenariat);
        return result;
    }

    /**
     *  Get all the partenariats.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<PartenariatDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Partenariats");
        Page<Partenariat> result = partenariatRepository.findAll(pageable);
        return result.map(partenariat -> partenariatMapper.partenariatToPartenariatDTO(partenariat));
    }

    /**
     *  Get one partenariat by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public PartenariatDTO findOne(Long id) {
        log.debug("Request to get Partenariat : {}", id);
        Partenariat partenariat = partenariatRepository.findOneWithEagerRelationships(id);
        PartenariatDTO partenariatDTO = partenariatMapper.partenariatToPartenariatDTO(partenariat);
        return partenariatDTO;
    }

    /**
     *  Delete the  partenariat by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Partenariat : {}", id);
        partenariatRepository.delete(id);
        partenariatSearchRepository.delete(id);
    }

    /**
     * Search for the partenariat corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PartenariatDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Partenariats for query {}", query);
        Page<Partenariat> result = partenariatSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(partenariat -> partenariatMapper.partenariatToPartenariatDTO(partenariat));
    }
}
