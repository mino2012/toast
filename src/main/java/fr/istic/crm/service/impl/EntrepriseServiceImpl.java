package fr.istic.crm.service.impl;

import fr.istic.crm.service.EntrepriseService;
import fr.istic.crm.domain.Entreprise;
import fr.istic.crm.repository.EntrepriseRepository;
import fr.istic.crm.repository.search.EntrepriseSearchRepository;
import fr.istic.crm.service.dto.EntrepriseDTO;
import fr.istic.crm.service.mapper.EntrepriseMapper;
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
 * Service Implementation for managing Entreprise.
 */
@Service
@Transactional
public class EntrepriseServiceImpl implements EntrepriseService{

    private final Logger log = LoggerFactory.getLogger(EntrepriseServiceImpl.class);
    
    @Inject
    private EntrepriseRepository entrepriseRepository;

    @Inject
    private EntrepriseMapper entrepriseMapper;

    @Inject
    private EntrepriseSearchRepository entrepriseSearchRepository;

    /**
     * Save a entreprise.
     *
     * @param entrepriseDTO the entity to save
     * @return the persisted entity
     */
    public EntrepriseDTO save(EntrepriseDTO entrepriseDTO) {
        log.debug("Request to save Entreprise : {}", entrepriseDTO);
        Entreprise entreprise = entrepriseMapper.entrepriseDTOToEntreprise(entrepriseDTO);
        entreprise = entrepriseRepository.save(entreprise);
        EntrepriseDTO result = entrepriseMapper.entrepriseToEntrepriseDTO(entreprise);
        entrepriseSearchRepository.save(entreprise);
        return result;
    }

    /**
     *  Get all the entreprises.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<EntrepriseDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Entreprises");
        Page<Entreprise> result = entrepriseRepository.findAll(pageable);
        return result.map(entreprise -> entrepriseMapper.entrepriseToEntrepriseDTO(entreprise));
    }


    /**
     *  get all the entreprises where Siege is null.
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<EntrepriseDTO> findAllWhereSiegeIsNull() {
        log.debug("Request to get all entreprises where Siege is null");
        return StreamSupport
            .stream(entrepriseRepository.findAll().spliterator(), false)
            .filter(entreprise -> entreprise.getSiege() == null)
            .map(entrepriseMapper::entrepriseToEntrepriseDTO)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     *  get all the entreprises where Contact is null.
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<EntrepriseDTO> findAllWhereContactIsNull() {
        log.debug("Request to get all entreprises where Contact is null");
        return StreamSupport
            .stream(entrepriseRepository.findAll().spliterator(), false)
            .filter(entreprise -> entreprise.getContact() == null)
            .map(entrepriseMapper::entrepriseToEntrepriseDTO)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get one entreprise by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public EntrepriseDTO findOne(Long id) {
        log.debug("Request to get Entreprise : {}", id);
        Entreprise entreprise = entrepriseRepository.findOne(id);
        EntrepriseDTO entrepriseDTO = entrepriseMapper.entrepriseToEntrepriseDTO(entreprise);
        return entrepriseDTO;
    }

    /**
     *  Delete the  entreprise by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Entreprise : {}", id);
        entrepriseRepository.delete(id);
        entrepriseSearchRepository.delete(id);
    }

    /**
     * Search for the entreprise corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<EntrepriseDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Entreprises for query {}", query);
        Page<Entreprise> result = entrepriseSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(entreprise -> entrepriseMapper.entrepriseToEntrepriseDTO(entreprise));
    }
}
