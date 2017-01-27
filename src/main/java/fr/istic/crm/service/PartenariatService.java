package fr.istic.crm.service;

import fr.istic.crm.service.dto.PartenariatDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing Partenariat.
 */
public interface PartenariatService {

    /**
     * Save a partenariat.
     *
     * @param partenariatDTO the entity to save
     * @return the persisted entity
     */
    PartenariatDTO save(PartenariatDTO partenariatDTO);

    /**
     *  Get all the partenariats.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<PartenariatDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" partenariat.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    PartenariatDTO findOne(Long id);

    /**
     *  Delete the "id" partenariat.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the partenariat corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<PartenariatDTO> search(String query, Pageable pageable);
}
