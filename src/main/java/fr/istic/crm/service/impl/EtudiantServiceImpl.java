package fr.istic.crm.service.impl;

import fr.istic.crm.service.EtudiantService;
import fr.istic.crm.domain.Etudiant;
import fr.istic.crm.repository.EtudiantRepository;
import fr.istic.crm.repository.search.EtudiantSearchRepository;
import fr.istic.crm.service.dto.EtudiantDTO;
import fr.istic.crm.service.mapper.EtudiantMapper;
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
 * Service Implementation for managing Etudiant.
 */
@Service
@Transactional
public class EtudiantServiceImpl implements EtudiantService{

    private final Logger log = LoggerFactory.getLogger(EtudiantServiceImpl.class);
    
    @Inject
    private EtudiantRepository etudiantRepository;

    @Inject
    private EtudiantMapper etudiantMapper;

    @Inject
    private EtudiantSearchRepository etudiantSearchRepository;

    /**
     * Save a etudiant.
     *
     * @param etudiantDTO the entity to save
     * @return the persisted entity
     */
    public EtudiantDTO save(EtudiantDTO etudiantDTO) {
        log.debug("Request to save Etudiant : {}", etudiantDTO);
        Etudiant etudiant = etudiantMapper.etudiantDTOToEtudiant(etudiantDTO);
        etudiant = etudiantRepository.save(etudiant);
        EtudiantDTO result = etudiantMapper.etudiantToEtudiantDTO(etudiant);
        etudiantSearchRepository.save(etudiant);
        return result;
    }

    /**
     *  Get all the etudiants.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<EtudiantDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Etudiants");
        Page<Etudiant> result = etudiantRepository.findAll(pageable);
        return result.map(etudiant -> etudiantMapper.etudiantToEtudiantDTO(etudiant));
    }

    /**
     *  Get one etudiant by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public EtudiantDTO findOne(Long id) {
        log.debug("Request to get Etudiant : {}", id);
        Etudiant etudiant = etudiantRepository.findOneWithEagerRelationships(id);
        EtudiantDTO etudiantDTO = etudiantMapper.etudiantToEtudiantDTO(etudiant);
        return etudiantDTO;
    }

    /**
     *  Delete the  etudiant by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Etudiant : {}", id);
        etudiantRepository.delete(id);
        etudiantSearchRepository.delete(id);
    }

    /**
     * Search for the etudiant corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<EtudiantDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Etudiants for query {}", query);
        Page<Etudiant> result = etudiantSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(etudiant -> etudiantMapper.etudiantToEtudiantDTO(etudiant));
    }
}
