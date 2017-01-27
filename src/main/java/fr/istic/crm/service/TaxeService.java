package fr.istic.crm.service;

import fr.istic.crm.service.dto.TaxeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing Taxe.
 */
public interface TaxeService {

    /**
     * Save a taxe.
     *
     * @param taxeDTO the entity to save
     * @return the persisted entity
     */
    TaxeDTO save(TaxeDTO taxeDTO);

    /**
     *  Get all the taxes.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<TaxeDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" taxe.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    TaxeDTO findOne(Long id);

    /**
     *  Delete the "id" taxe.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the taxe corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<TaxeDTO> search(String query, Pageable pageable);
}
