package fr.istic.crm.service.impl;

import fr.istic.crm.service.DiplomeService;
import fr.istic.crm.domain.Diplome;
import fr.istic.crm.repository.DiplomeRepository;
import fr.istic.crm.repository.search.DiplomeSearchRepository;
import fr.istic.crm.service.dto.DiplomeDTO;
import fr.istic.crm.service.mapper.DiplomeMapper;
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
 * Service Implementation for managing Diplome.
 */
@Service
@Transactional
public class DiplomeServiceImpl implements DiplomeService{

    private final Logger log = LoggerFactory.getLogger(DiplomeServiceImpl.class);

    @Inject
    private DiplomeRepository diplomeRepository;

    @Inject
    private DiplomeMapper diplomeMapper;

    @Inject
    private DiplomeSearchRepository diplomeSearchRepository;

    /**
     * Save a diplome.
     *
     * @param diplomeDTO the entity to save
     * @return the persisted entity
     */
    public DiplomeDTO save(DiplomeDTO diplomeDTO) {
        log.debug("Request to save Diplome : {}", diplomeDTO);
        Diplome diplome = diplomeMapper.diplomeDTOToDiplome(diplomeDTO);
        diplome = diplomeRepository.save(diplome);
        DiplomeDTO result = diplomeMapper.diplomeToDiplomeDTO(diplome);
        diplomeSearchRepository.save(diplome);
        return result;
    }

    /**
     *  Get all the diplomes.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<DiplomeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Diplomes");
        Page<Diplome> result = diplomeRepository.findAll(pageable);
        return result.map(diplome -> diplomeMapper.diplomeToDiplomeDTO(diplome));
    }

    /**
     *  Get one diplome by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public DiplomeDTO findOne(Long id) {
        log.debug("Request to get Diplome : {}", id);
        Diplome diplome = diplomeRepository.findOne(id);
        DiplomeDTO diplomeDTO = diplomeMapper.diplomeToDiplomeDTO(diplome);
        return diplomeDTO;
    }

    /**
     *  Delete the  diplome by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Diplome : {}", id);
        diplomeRepository.delete(id);
        diplomeSearchRepository.delete(id);
    }

    /**
     * Search for the diplome corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<DiplomeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Diplomes for query {}", query);
        Page<Diplome> result = diplomeSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(diplome -> diplomeMapper.diplomeToDiplomeDTO(diplome));
    }
}
