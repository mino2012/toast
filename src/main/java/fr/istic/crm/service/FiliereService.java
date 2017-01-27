package fr.istic.crm.service;

import fr.istic.crm.service.dto.FiliereDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing Filiere.
 */
public interface FiliereService {

    /**
     * Save a filiere.
     *
     * @param filiereDTO the entity to save
     * @return the persisted entity
     */
    FiliereDTO save(FiliereDTO filiereDTO);

    /**
     *  Get all the filieres.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<FiliereDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" filiere.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    FiliereDTO findOne(Long id);

    /**
     *  Delete the "id" filiere.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the filiere corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<FiliereDTO> search(String query, Pageable pageable);
}
