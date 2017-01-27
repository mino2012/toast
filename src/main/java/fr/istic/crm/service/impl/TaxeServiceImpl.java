package fr.istic.crm.service.impl;

import fr.istic.crm.service.TaxeService;
import fr.istic.crm.domain.Taxe;
import fr.istic.crm.repository.TaxeRepository;
import fr.istic.crm.repository.search.TaxeSearchRepository;
import fr.istic.crm.service.dto.TaxeDTO;
import fr.istic.crm.service.mapper.TaxeMapper;
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
 * Service Implementation for managing Taxe.
 */
@Service
@Transactional
public class TaxeServiceImpl implements TaxeService{

    private final Logger log = LoggerFactory.getLogger(TaxeServiceImpl.class);
    
    @Inject
    private TaxeRepository taxeRepository;

    @Inject
    private TaxeMapper taxeMapper;

    @Inject
    private TaxeSearchRepository taxeSearchRepository;

    /**
     * Save a taxe.
     *
     * @param taxeDTO the entity to save
     * @return the persisted entity
     */
    public TaxeDTO save(TaxeDTO taxeDTO) {
        log.debug("Request to save Taxe : {}", taxeDTO);
        Taxe taxe = taxeMapper.taxeDTOToTaxe(taxeDTO);
        taxe = taxeRepository.save(taxe);
        TaxeDTO result = taxeMapper.taxeToTaxeDTO(taxe);
        taxeSearchRepository.save(taxe);
        return result;
    }

    /**
     *  Get all the taxes.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<TaxeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Taxes");
        Page<Taxe> result = taxeRepository.findAll(pageable);
        return result.map(taxe -> taxeMapper.taxeToTaxeDTO(taxe));
    }

    /**
     *  Get one taxe by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public TaxeDTO findOne(Long id) {
        log.debug("Request to get Taxe : {}", id);
        Taxe taxe = taxeRepository.findOne(id);
        TaxeDTO taxeDTO = taxeMapper.taxeToTaxeDTO(taxe);
        return taxeDTO;
    }

    /**
     *  Delete the  taxe by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Taxe : {}", id);
        taxeRepository.delete(id);
        taxeSearchRepository.delete(id);
    }

    /**
     * Search for the taxe corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<TaxeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Taxes for query {}", query);
        Page<Taxe> result = taxeSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(taxe -> taxeMapper.taxeToTaxeDTO(taxe));
    }
}
