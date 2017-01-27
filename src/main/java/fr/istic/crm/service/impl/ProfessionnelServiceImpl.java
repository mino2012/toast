package fr.istic.crm.service.impl;

import fr.istic.crm.service.ProfessionnelService;
import fr.istic.crm.domain.Professionnel;
import fr.istic.crm.repository.ProfessionnelRepository;
import fr.istic.crm.repository.search.ProfessionnelSearchRepository;
import fr.istic.crm.service.dto.ProfessionnelDTO;
import fr.istic.crm.service.mapper.ProfessionnelMapper;
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
 * Service Implementation for managing Professionnel.
 */
@Service
@Transactional
public class ProfessionnelServiceImpl implements ProfessionnelService{

    private final Logger log = LoggerFactory.getLogger(ProfessionnelServiceImpl.class);
    
    @Inject
    private ProfessionnelRepository professionnelRepository;

    @Inject
    private ProfessionnelMapper professionnelMapper;

    @Inject
    private ProfessionnelSearchRepository professionnelSearchRepository;

    /**
     * Save a professionnel.
     *
     * @param professionnelDTO the entity to save
     * @return the persisted entity
     */
    public ProfessionnelDTO save(ProfessionnelDTO professionnelDTO) {
        log.debug("Request to save Professionnel : {}", professionnelDTO);
        Professionnel professionnel = professionnelMapper.professionnelDTOToProfessionnel(professionnelDTO);
        professionnel = professionnelRepository.save(professionnel);
        ProfessionnelDTO result = professionnelMapper.professionnelToProfessionnelDTO(professionnel);
        professionnelSearchRepository.save(professionnel);
        return result;
    }

    /**
     *  Get all the professionnels.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ProfessionnelDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Professionnels");
        Page<Professionnel> result = professionnelRepository.findAll(pageable);
        return result.map(professionnel -> professionnelMapper.professionnelToProfessionnelDTO(professionnel));
    }

    /**
     *  Get one professionnel by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ProfessionnelDTO findOne(Long id) {
        log.debug("Request to get Professionnel : {}", id);
        Professionnel professionnel = professionnelRepository.findOneWithEagerRelationships(id);
        ProfessionnelDTO professionnelDTO = professionnelMapper.professionnelToProfessionnelDTO(professionnel);
        return professionnelDTO;
    }

    /**
     *  Delete the  professionnel by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Professionnel : {}", id);
        professionnelRepository.delete(id);
        professionnelSearchRepository.delete(id);
    }

    /**
     * Search for the professionnel corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProfessionnelDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Professionnels for query {}", query);
        Page<Professionnel> result = professionnelSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(professionnel -> professionnelMapper.professionnelToProfessionnelDTO(professionnel));
    }
}
